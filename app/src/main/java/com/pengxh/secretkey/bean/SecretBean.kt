package com.pengxh.secretkey.bean

/**
 * @description: TODO
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/8/2 18:10
 */
class SecretBean {
    /**
     * secretCategory : 网站
     * secret : [{"secretTitle":"淘宝","secretAccount":"111111","secretPassword":"111111","recoverable":"1"},{"secretTitle":"京东","secretAccount":"111111","secretPassword":"111111","recoverable":"1"}]
     */
    var category: String? = null
    var secret: List<Secret>? = null

    class Secret {
        /**
         * secretTitle : 淘宝
         * secretAccount : 111111
         * secretPassword : 111111
         * recoverable : 1
         */
        var secretTitle: String? = null
        var secretAccount: String? = null
        var secretPassword: String? = null
        var recoverable: String? = null
    }
}