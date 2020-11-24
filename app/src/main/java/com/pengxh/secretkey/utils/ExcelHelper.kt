package com.pengxh.secretkey.utils

import android.util.Log
import com.google.gson.Gson
import com.pengxh.secretkey.bean.SecretBean
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.io.FileInputStream

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020年11月19日22:58:14
 */
class ExcelHelper {
    companion object {
        private const val TAG = "ExcelHelper"
        private const val UTF8_ENCODING = "UTF-8"
        private lateinit var arial14font: WritableFont
        private lateinit var arial14format: WritableCellFormat
        private lateinit var arial10font: WritableFont
        private lateinit var arial10format: WritableCellFormat
        private lateinit var arial12font: WritableFont
        private lateinit var arial12format: WritableCellFormat
        private lateinit var file: File

        /**
         * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
         */
        private fun format() {
            try {
                arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
                arial14font.colour = Colour.LIGHT_BLUE
                arial14format = WritableCellFormat(arial14font)
                arial14format.alignment = jxl.format.Alignment.CENTRE
                arial14format.setBorder(
                    jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN
                )
                arial14format.setBackground(Colour.VERY_LIGHT_YELLOW)
                arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
                arial10format = WritableCellFormat(arial10font)
                arial10format.alignment = jxl.format.Alignment.CENTRE
                arial10format.setBorder(
                    jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN
                )
                arial10format.setBackground(Colour.GRAY_25)
                arial12font = WritableFont(WritableFont.ARIAL, 10)
                arial12format = WritableCellFormat(arial12font)
                arial10format.alignment = jxl.format.Alignment.CENTRE //对齐格式
                arial12format.setBorder(
                    jxl.format.Border.ALL,
                    jxl.format.BorderLineStyle.THIN
                ) //设置边框
            } catch (e: WriteException) {
                e.printStackTrace()
            }
        }

        /**
         * 初始化Excel
         *
         * @param file
         * @param colName
         */
        fun initExcel(file: File, colName: Array<String>) {
            this.file = file
            format()
            var workbook: WritableWorkbook? = null
            try {
                workbook = Workbook.createWorkbook(file)
                val sheet: WritableSheet = workbook.createSheet(file.name, 0)
                //创建标题栏
                sheet.addCell(Label(0, 0, file.name, arial14format))
                for (col in colName.indices) {
                    sheet.addCell(Label(col, 0, colName[col], arial10format))
                }
                sheet.setRowView(0, 340) //设置行高
                workbook.write()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (workbook != null) {
                    try {
                        workbook.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         * 将数据写入Excel表格
         * */
        fun writeSecretToExcel(list: List<SecretBean>?) {
            if (list != null && list.isNotEmpty()) {
                var writeBook: WritableWorkbook? = null
                try {
                    val setEncode = WorkbookSettings()
                    setEncode.encoding = UTF8_ENCODING
                    val workbook: Workbook = Workbook.getWorkbook(FileInputStream(file))
                    writeBook = Workbook.createWorkbook(file, workbook)
                    val sheet: WritableSheet = writeBook.getSheet(0)
                    for (j in list.indices) {
                        val secretBean: SecretBean = list[j]
                        val secretCategory: String? = secretBean.secretCategory
                        val secretTitle: String? = secretBean.secretTitle
                        val secretAccount: String? = secretBean.secretAccount
                        val secretPassword: String? = secretBean.secretPassword
                        val secretRemarks: String? = secretBean.secretRemarks
                        //第一行留作表头
                        sheet.addCell(Label(0, j + 1, secretCategory, arial12format))
                        sheet.addCell(Label(1, j + 1, secretTitle, arial12format))
                        sheet.addCell(Label(2, j + 1, secretAccount, arial12format))
                        sheet.addCell(Label(3, j + 1, secretPassword, arial12format))
                        sheet.addCell(Label(4, j + 1, secretRemarks, arial12format))
                        sheet.setRowView(j + 1, 350) //设置行高
                    }
                    writeBook.write()
                    Log.d(TAG, "writeSecretToExcel: 导出表格到本地成功")
                    //然后导出到指定位置
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (writeBook != null) {
                        try {
                            writeBook.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        /**
         * 将Excel表格转化为Json数据
         * */
        fun transformExcelToJson(filePath: String): String {
            var result = ""
            val workbook = Workbook.getWorkbook(File(filePath))
            val sheet = workbook.getSheet(0)
            val header = sheet.getRow(0)
            for (i in 0 until sheet.rows) {
                val hashMap = HashMap<String, String>()
                for (j in 0 until sheet.columns) {
                    val cell = sheet.getCell(j, i)
                    hashMap[header[j].contents] = cell.contents
                }
                result = Gson().toJson(hashMap)
            }
            return result
        }
    }
}