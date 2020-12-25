package com.pengxh.secretkey.utils

import com.pengxh.secretkey.R
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
import java.util.regex.Pattern

class StringHelper {
    companion object {
        /**
         * 判断是否为汉字
         */
        fun isChinese(s: String): Boolean {
            val chinesePattern: Pattern = Pattern.compile("[\\u4e00-\\u9fa5]+")
            return chinesePattern.matcher(s).matches()
        }

        /**
         * 判断是否为数字
         */
        fun isNumber(s: String): Boolean {
            val numberPattern: Pattern = Pattern.compile("^[0-9]*\$")//判断数字
            return numberPattern.matcher(s).matches()
        }

        /**
         * 根据类型获取图片
         * */
        fun obtainResource(type: String?): Int {
            when (type) {
                "网站" -> return R.drawable.ic_web
                "APP" -> return R.drawable.ic_app
                "游戏" -> return R.drawable.ic_game
                "银行卡" -> return R.drawable.ic_card
                "工作" -> return R.drawable.ic_work
                "邮箱" -> return R.drawable.ic_email
                "聊天" -> return R.drawable.ic_chat
                "其他" -> return R.mipmap.other
            }
            return R.mipmap.other
        }

        /**
         * 获取汉语拼音
         */
        fun obtainHanYuPinyin(chinese: String): String {
            val pinyinStr = StringBuilder()
            //如果是多音字需要手动纠正
            when {
                chinese.startsWith("0") -> {
                    pinyinStr.append("LING")
                }
                chinese.startsWith("1") -> {
                    pinyinStr.append("YI")
                }
                chinese.startsWith("2") -> {
                    pinyinStr.append("ER")
                }
                chinese.startsWith("3") -> {
                    pinyinStr.append("SAN")
                }
                chinese.startsWith("4") -> {
                    pinyinStr.append("SI")
                }
                chinese.startsWith("5") -> {
                    pinyinStr.append("WU")
                }
                chinese.startsWith("6") -> {
                    pinyinStr.append("LIU")
                }
                chinese.startsWith("7") -> {
                    pinyinStr.append("QI")
                }
                chinese.startsWith("8") -> {
                    pinyinStr.append("BA")
                }
                chinese.startsWith("9") -> {
                    pinyinStr.append("JIU")
                }
                else -> {
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
            }
            return pinyinStr.substring(0, 1)
        }
    }
}