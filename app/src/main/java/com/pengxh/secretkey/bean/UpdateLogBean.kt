package com.pengxh.secretkey.bean

class UpdateLogBean {
    /**
     * updateTime : 2020-11-5
     * message : 发布1.1版本
     * updateLogs : ["1、【新增】修改账号密码功能","2、【新增】更新日志查看功能","3、【新增】数据导出/导入功能","4、【新增】扫码识图功能","5、【优化】搜索功能","6、【删除】数据恢复功能"]
     */
    var updateTime: String? = null
    var message: String? = null
    var updateLogs: ArrayList<String>? = null
}