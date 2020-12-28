package com.pengxh.secretkey.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.view.MotionEvent
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.BankCardParams
import com.baidu.ocr.sdk.model.BankCardResult
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.app.multilib.base.BaseNormalActivity
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.CameraPreviewHelper
import com.pengxh.secretkey.utils.OtherUtils
import kotlinx.android.synthetic.main.activity_ocr.*
import java.io.File

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/11/17 23:13
 */
class OcrBankCardActivity : BaseNormalActivity(), CameraPreviewHelper.OnCaptureImageCallback {

    companion object {
        private const val TAG = "OcrBankCardActivity"
        const val resultCode = 888
    }

    private lateinit var cameraPreviewHelper: CameraPreviewHelper

    override fun initLayoutView(): Int = R.layout.activity_ocr

    override fun initData() {
        ImmersionBar.with(this).init()
    }

    override fun initEvent() {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        cameraPreviewHelper =
            CameraPreviewHelper(this, targetPreView, "0", CameraCharacteristics.LENS_FACING_BACK)
        cameraPreviewHelper.setImageCallback(this)
        //按钮缩小效果
        recognizeButton.setOnTouchListener { v, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    recognizeButton.animate().scaleX(0.75f).scaleY(0.75f)
                        .setDuration(100).start()
                }
                MotionEvent.ACTION_UP -> {
                    recognizeButton.animate().scaleX(1.0f).scaleY(1.0f)
                        .setDuration(100).start()
                    //拍照
                    if (OtherUtils.isNetworkAvailable(this)) {
                        cameraPreviewHelper.takePicture()
                    } else {
                        EasyToast.showToast("识别失败", EasyToast.ERROR)
                    }
                }
            }
            true
        }
    }

    override fun captureImage(localPath: String?, bitmap: Bitmap?) {
        val param = BankCardParams()
        param.imageFile = File(localPath!!)
        OCR.getInstance(this).recognizeBankCard(param, object : OnResultListener<BankCardResult> {
            override fun onResult(bankCardResult: BankCardResult?) {
                Log.d(TAG, "onResult: " + Gson().toJson(bankCardResult))
                val bankCardNumber = bankCardResult?.bankCardNumber
                if (bankCardNumber == "") {
                    EasyToast.showToast("识别失败", EasyToast.ERROR)
                } else {
                    EasyToast.showToast("识别成功", EasyToast.SUCCESS)
                    val intent = Intent()
                    intent.putExtra("bankName", bankCardResult?.bankName)
                    intent.putExtra("bankCardNumber", bankCardNumber)
                    setResult(resultCode, intent)
                    finish()
                }
            }

            override fun onError(ocrError: OCRError?) {
                Log.e(TAG, "onError: ${ocrError?.cause}")
            }
        })
    }
}