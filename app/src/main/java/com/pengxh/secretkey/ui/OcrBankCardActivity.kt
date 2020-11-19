package com.pengxh.secretkey.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
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
import com.pengxh.secretkey.utils.StatusBarColorUtil
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
        StatusBarColorUtil.setColor(this, Color.WHITE)
        ImmersionBar.with(this).statusBarDarkFont(true).init()
    }

    override fun initEvent() {

    }

    override fun onResume() {
        super.onResume()
        cameraPreviewHelper = CameraPreviewHelper(this, targetPreView)
        cameraPreviewHelper.setImageCallback(this)
        recognizeButton.setOnClickListener {
            cameraPreviewHelper.takePicture()
        }
    }

    override fun captureImage(localPath: String?, bitmap: Bitmap?) {
        val param = BankCardParams()
        param.imageFile = File(localPath!!)
        OCR.getInstance(this).recognizeBankCard(param, object : OnResultListener<BankCardResult> {
            override fun onResult(bankCardResult: BankCardResult?) {
                Log.d(TAG, "onResult: " + Gson().toJson(bankCardResult))
                EasyToast.showToast("识别成功", EasyToast.SUCCESS)
                val intent = Intent()
                intent.putExtra("bankName", bankCardResult?.bankName)
                intent.putExtra("bankCardNumber", bankCardResult?.bankCardNumber)
                setResult(888, intent)
                finish()
            }

            override fun onError(ocrError: OCRError?) {
                if (ocrError != null) {
                    EasyToast.showToast("识别失败，请重试", EasyToast.WARING)
                }
            }
        })
    }
}