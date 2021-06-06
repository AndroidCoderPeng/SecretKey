package com.pengxh.secretkey.ui

import android.content.Intent
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.pengxh.app.multilib.utils.DensityUtil
import com.pengxh.secretkey.BaseActivity
import com.pengxh.secretkey.BaseApplication
import com.pengxh.secretkey.R
import com.pengxh.secretkey.bean.SecretSQLiteBean
import com.pengxh.secretkey.utils.Constant
import com.pengxh.secretkey.utils.ToastHelper
import kotlinx.android.synthetic.main.activity_secret_add.*


/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/1 22:53
 */
class AddSecretActivity : BaseActivity() {

    companion object {
        const val requestCode = 777
    }

    private lateinit var selectCategory: String
    private var newTitle: String? = null
    private var newAccount: String? = null
    private var newPassword: String? = null

    override fun initLayoutView(): Int = R.layout.activity_secret_add

    override fun setupTopBarLayout() {
        topLayout.setTitle("添加密码").setTextColor(ContextCompat.getColor(this, R.color.white))
        topLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.mainThemeColor))
    }

    override fun initData() {
        val intentCategory = intent.getStringExtra("secretCategory")
        if (intentCategory == null) {
            val spinnerAdapter: ArrayAdapter<String> =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, Constant.CATEGORY)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.dropDownVerticalOffset = DensityUtil.dp2px(this, 40.0f)
            categorySpinner.dropDownWidth = DensityUtil.dp2px(this, 90.0f)
            categorySpinner.adapter = spinnerAdapter
        } else {
            //选中当前分类
            for (index in 0..Constant.CATEGORY.size) {
                if (intentCategory == Constant.CATEGORY[index]) {
                    categorySpinner.setSelection(index, true)
                    selectCategory = intentCategory
                    break
                }
            }
        }
    }

    override fun initEvent() {
        categorySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                selectCategory = Constant.CATEGORY[pos]
                if (selectCategory == "银行卡") {
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

        saveButton.setChangeAlphaWhenPress(true)
        saveButton.setOnClickListener {
            newTitle = inputTitle.text.toString().trim()
            if (newTitle == null || newTitle == "") {
                ToastHelper.showToast("标题未填写，请检查", ToastHelper.WARING)
                return@setOnClickListener
            }

            newAccount = inputAccount.text.toString().trim()
            if (newAccount == null || newAccount == "") {
                ToastHelper.showToast("账号未填写，请检查", ToastHelper.WARING)
                return@setOnClickListener
            }

            newPassword = inputPassword.text.toString().trim()
            if (newPassword == null || newPassword == "") {
                ToastHelper.showToast("密码未填写，请检查", ToastHelper.WARING)
                return@setOnClickListener
            }

            //将数据存数据库，然后结束当前页面
            val secretSQLiteBean = SecretSQLiteBean()
            secretSQLiteBean.category = selectCategory
            secretSQLiteBean.title = newTitle
            secretSQLiteBean.account = newAccount
            secretSQLiteBean.password = newPassword
            secretSQLiteBean.remarks = inputRemarks.text.toString().trim()
            BaseApplication.instance().obtainDaoSession().secretSQLiteBeanDao.insert(
                secretSQLiteBean
            )
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