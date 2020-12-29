package com.pengxh.secretkey.ui

import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.R
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.SQLiteUtil
import kotlinx.android.synthetic.main.activity_secret_add.*
import kotlinx.android.synthetic.main.include_title_cyan.*


/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/1 22:53
 */
class AddSecretActivity : BaseActivity() {

    companion object {
        private const val Tag = "AddSecretActivity"
        const val requestCode = 777
    }

    private var category: String? = null
    private var title: String? = null
    private var account: String? = null
    private var password: String? = null
    private var remarks: String? = null

    override fun initLayoutView(): Int = R.layout.activity_secret_add

    override fun initData() {
        mTitleView.text = "添加密码"
    }

    override fun initEvent() {
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, Constant.category)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        categorySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                category = Constant.category[pos]
                if (category.equals("银行卡")) {
                    Log.d(Tag, "initEvent: 银行卡")
                    codeScannerView.visibility = View.VISIBLE
                    inputAccount.inputType = InputType.TYPE_CLASS_NUMBER
                } else {
                    codeScannerView.visibility = View.GONE
                    inputAccount.inputType = InputType.TYPE_CLASS_TEXT
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        codeScannerView.setOnClickListener {
            startActivityForResult(Intent(this, OcrBankCardActivity::class.java), 777)
        }

        saveButton.setOnClickListener {
            title = inputTitle.text.toString().trim()
            if (title == null || title == "") {
                EasyToast.showToast("标题未填写，请检查", EasyToast.WARING)
                return@setOnClickListener
            }

            account = inputAccount.text.toString().trim()
            if (account == null || account == "") {
                EasyToast.showToast("账号未填写，请检查", EasyToast.WARING)
                return@setOnClickListener
            }

            password = inputPassword.text.toString().trim()
            if (password == null || password == "") {
                EasyToast.showToast("密码未填写，请检查", EasyToast.WARING)
                return@setOnClickListener
            }
            remarks = inputRemarks.text.toString().trim()

            //将数据存数据库，然后结束当前页面
            SQLiteUtil().saveSecret(category!!, title!!, account!!, password!!, remarks)
            this.finish()
        }
    }

    /**
     * 接收OCR银行卡识别结果
     * */
    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (reqCode == requestCode && resultCode == OcrBankCardActivity.resultCode) {
            inputTitle.setText(data?.getStringExtra("bankName"))
            inputAccount.setText(data?.getStringExtra("bankCardNumber"))
        }
    }
}