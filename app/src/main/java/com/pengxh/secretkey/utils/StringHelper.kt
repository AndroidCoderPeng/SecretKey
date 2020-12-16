package com.pengxh.secretkey.utils

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

class StringHelper {
    companion object {
        /**
         * 获取汉语拼音
         */
        fun obtainHanYuPinyin(chinese: String): String {
            val pinyinStr = StringBuilder()
            //如果是多音字需要手动纠正
            if (chinese == "重庆") {
                pinyinStr.append("CHONGQING")
            } else {
                val newChar = chinese.toCharArray() //转为单个字符
                val defaultFormat = HanyuPinyinOutputFormat()
                defaultFormat.caseType = HanyuPinyinCaseType.UPPERCASE
                defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
                for (c in newChar) {
                    if (c.toInt() > 128) {
                        try {
                            pinyinStr.append(
                                PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0][0]
                            )
                        } catch (e: BadHanyuPinyinOutputFormatCombination) {
                            e.printStackTrace()
                        }
                    } else {
                        pinyinStr.append(c)
                    }
                }
            }
            return pinyinStr.toString()
        }

        /**
         * 获取汉字首字母
         * */
        fun obtainFirstHanYuPinyin(chinese: String): String? {
            val defaultFormat = HanyuPinyinOutputFormat()
            // 输出拼音全部小写
            defaultFormat.caseType = HanyuPinyinCaseType.UPPERCASE
            // 不带声调
            defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
            var pinyin = ""
            try {
                pinyin = PinyinHelper.toHanyuPinyinStringArray(chinese[0], defaultFormat)[0]
            } catch (e: BadHanyuPinyinOutputFormatCombination) {
                e.printStackTrace()
            }
            return pinyin.substring(0, 1)
        }
    }
}