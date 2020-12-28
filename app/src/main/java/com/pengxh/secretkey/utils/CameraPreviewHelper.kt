package com.pengxh.secretkey.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import androidx.core.content.ContextCompat
import com.pengxh.app.multilib.widget.EasyToast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Long.signum
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 相机部分功能封装
 * @date: 2020年11月16日19:26:32
 */
class CameraPreviewHelper(
    private val mContext: Context, textureView: TextureView,
    cameraId: String, facing: Int
) {
    private val mTextureView: TextureView = textureView
    private val mPreviewHandler: Handler
    private var mCameraId = cameraId
    private var mCameraFacing = facing
    private var mCharacteristics: CameraCharacteristics? = null
    private var mSensorOrientation: Int? = null
    private var mImageReader: ImageReader? = null
    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var canTakePic = false
    private var imageCallback: OnCaptureImageCallback? = null
    private var mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT) //预览大小
    private var mSavePicSize = Size(SAVE_WIDTH, SAVE_HEIGHT)       //保存图片大小

    fun setImageCallback(callback: OnCaptureImageCallback?) {
        imageCallback = callback
    }

    companion object {
        private const val Tag = "CameraPreviewHelper"
        const val PREVIEW_WIDTH = 720        //预览的宽度
        const val PREVIEW_HEIGHT = 1280      //预览的高度
        const val SAVE_WIDTH = 720           //保存图片的宽度
        const val SAVE_HEIGHT = 1280         //保存图片的高度
    }

    init {
        //打开相机和创建会话等都是耗时操作，所以我们启动一个HandlerThread在子线程中来处理
        val mPreviewThread = HandlerThread("CameraPreviewThread")
        mPreviewThread.start()
        mPreviewHandler = Handler(mPreviewThread.looper)
        mTextureView.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                try {
                    initCamera()
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                stopPreview()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    /**
     * 初始化相机
     */
    @Throws(CameraAccessException::class)
    private fun initCamera() {
        val mCameraManager = (mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
        val cameraIdList = mCameraManager.cameraIdList
        if (cameraIdList.isEmpty()) {
            EasyToast.showToast("没有可用相机", EasyToast.WARING)
            return
        }
        for (id in cameraIdList) {
            val characteristics = mCameraManager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)!!
            if (mCameraFacing == facing) {
                mCameraId = id
                mCharacteristics = characteristics
                break
            }
        }
        Log.d(Tag, "设备中的摄像头: $mCameraId")
        //获取摄像头方向
        mSensorOrientation = mCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION)
        val configurationMap =
            mCharacteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val savePicSize = configurationMap?.getOutputSizes(ImageFormat.JPEG)          //保存照片尺寸
        val previewSize = configurationMap?.getOutputSizes(SurfaceTexture::class.java) //预览尺寸
        mSavePicSize = obtainBestSize(
            mSavePicSize.height,
            mSavePicSize.width,
            mSavePicSize.height,
            mSavePicSize.width,
            savePicSize!!.toList()
        )

        mPreviewSize = obtainBestSize(
            mPreviewSize.height,
            mPreviewSize.width,
            mTextureView.height,
            mTextureView.width,
            previewSize!!.toList()
        )

        mTextureView.surfaceTexture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
        mImageReader =
            ImageReader.newInstance(mSavePicSize.width, mSavePicSize.height, ImageFormat.JPEG, 1)
        mImageReader?.setOnImageAvailableListener(mImageAvailableListener, mPreviewHandler)
        //打开箱机
        if (ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            EasyToast.showToast("没有相机权限", EasyToast.WARING)
            return
        }
        mCameraManager.openCamera(mCameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.d(Tag, "onOpened: " + camera.id)
                mCameraDevice = camera
                try {
                    createCaptureSession(camera)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(Tag, "onDisconnected: " + camera.id)
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(Tag, "onError: " + camera.id)
                EasyToast.showToast("打开相机失败，错误码：$error", EasyToast.ERROR)
            }
        }, mPreviewHandler)
    }

    /**
     * 根据提供的参数值返回与指定宽高相等或最接近的尺寸
     *
     * @param targetWidth   目标宽度
     * @param targetHeight  目标高度
     * @param maxWidth      最大宽度(即TextureView的宽度)
     * @param maxHeight     最大高度(即TextureView的高度)
     * @param sizeList      支持的Size列表
     *
     * @return  返回与指定宽高相等或最接近的尺寸
     */
    private fun obtainBestSize(
        targetWidth: Int,
        targetHeight: Int,
        maxWidth: Int,
        maxHeight: Int,
        sizeList: List<Size>
    ): Size {
        val biggerTargetSizeList = ArrayList<Size>()     //比指定宽高大的Size列表
        val littleTargetSizeList = ArrayList<Size>()  //比指定宽高小的Size列表

        for (size in sizeList) {
            //宽<=最大宽度  &&  高<=最大高度  &&  宽高比 == 目标值宽高比
            if (size.width <= maxWidth && size.height <= maxHeight && size.width == size.height * targetWidth / targetHeight) {
                if (size.width >= targetWidth && size.height >= targetHeight) biggerTargetSizeList.add(
                    size
                )
                else littleTargetSizeList.add(size)
            }
        }
        Log.d(
            Tag,
            "目标尺寸 ：$targetWidth * $targetHeight, 比例 ：${targetWidth.toFloat() / targetHeight}"
        )
        //选择biggerTargetSizeList中最小的值 或littleTargetSizeList中最大的值
        return when {
            biggerTargetSizeList.size > 0 -> Collections.min(
                biggerTargetSizeList,
                CompareSizesByArea()
            )
            littleTargetSizeList.size > 0 -> Collections.min(
                littleTargetSizeList,
                CompareSizesByArea()
            )
            else -> sizeList[0]
        }
    }

    /**
     * 创建预览会话
     */
    @Throws(CameraAccessException::class)
    private fun createCaptureSession(camera: CameraDevice) {
        val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW) //预览
        val surface = Surface(mTextureView.surfaceTexture)
        captureRequestBuilder.addTarget(surface) //将CaptureRequest的构建器与Surface对象绑定在一起
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        ) // 闪光灯
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        ) // 自动对焦

        //为相机预览，创建一个CameraCaptureSession对象
        val surfaceList = listOf(surface, mImageReader!!.surface) //画面帧集合
        camera.createCaptureSession(surfaceList, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                mCaptureSession = session
                try {
                    session.setRepeatingRequest(
                        captureRequestBuilder.build(),
                        mCaptureCallBack,
                        mPreviewHandler
                    )
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                EasyToast.showToast("开启预览会话失败！", EasyToast.ERROR)
            }
        }, mPreviewHandler)
    }

    /**
     * 预览回调
     */
    private val mCaptureCallBack: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            canTakePic = true
        }

        override fun onCaptureFailed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            failure: CaptureFailure
        ) {
            super.onCaptureFailed(session, request, failure)
            Log.d(Tag, "onCaptureFailed: $failure")
            EasyToast.showToast("开启预览失败！", EasyToast.ERROR)
            canTakePic = false
        }
    }

    /**
     * 拍照
     */
    fun takePicture() {
        Log.d(Tag, "takePicture: 拍照")
        if (mCameraDevice == null || !mTextureView.isAvailable) return
        if (canTakePic) {
            try {
                val captureRequestBuilder =
                    mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE) //拍照
                captureRequestBuilder.addTarget(mImageReader!!.surface)
                captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                ) // 自动对焦
                captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                ) // 闪光灯
                captureRequestBuilder.set(
                    CaptureRequest.JPEG_ORIENTATION,
                    mSensorOrientation
                ) ////根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
                mCaptureSession!!.capture(captureRequestBuilder.build(), null, mPreviewHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private val mImageAvailableListener = OnImageAvailableListener { reader ->
        val image = reader.acquireNextImage()
        val byteBuffer = image.planes[0].buffer
        val bytes = ByteArray(byteBuffer.remaining())
        byteBuffer[bytes]
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        /**
         * 图片处理
         */
        val parentPath = mContext.filesDir.toString() + File.separator
        val file = File(parentPath)
        if (!file.exists()) {
            file.mkdir()
        }
        val mPictureFile = File(parentPath, System.currentTimeMillis().toString() + ".jpg")
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(mPictureFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) //压缩
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //直接将bitmap回调到需要的地方
        imageCallback!!.captureImage(mPictureFile.absolutePath, bitmap)
        image.close()
    }

    /**
     * 回调拍照本地路径和Bitmap
     */
    interface OnCaptureImageCallback {
        fun captureImage(localPath: String?, bitmap: Bitmap?)
    }

    /**
     * 停止预览
     */
    fun stopPreview() {
        Log.d(Tag, "stopPreview: 停止预览")
        if (mCaptureSession != null) {
            mCaptureSession!!.close()
            mCaptureSession = null
        }
        if (mCameraDevice != null) {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
        if (mImageReader != null) {
            mImageReader!!.close()
            mImageReader = null
        }
    }

    private class CompareSizesByArea : Comparator<Size> {
        override fun compare(size1: Size, size2: Size): Int {
            return signum(size1.width.toLong() * size1.height - size2.width.toLong() * size2.height)
        }
    }
}