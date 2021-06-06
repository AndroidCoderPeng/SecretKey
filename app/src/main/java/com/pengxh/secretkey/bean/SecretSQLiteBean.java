package com.pengxh.secretkey.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 必须是java，不是kt
 */
@Entity
public class SecretSQLiteBean {
    @Id(autoincrement = true)
    private Long id;

    private String category;
    private String title;
    private String account;
    private String password;
    private String remarks;

    @Generated(hash = 1297544182)
    public SecretSQLiteBean(Long id, String category, String title, String account,
                            String password, String remarks) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.account = account;
        this.password = password;
        this.remarks = remarks;
    }

    @Generated(hash = 649567170)
    public SecretSQLiteBean() {

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}