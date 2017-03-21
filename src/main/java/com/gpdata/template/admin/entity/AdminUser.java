package com.gpdata.template.admin.entity;

import java.util.Date;

/**
 * Created by acer_liuyutong on 2017/3/20.
 */
public class AdminUser {
    private Long adminUserId;
    private String adminUserName;
    private String adminPassword;
    private Date createDate;

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public String getAdminUserName() {
        return adminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        this.adminUserName = adminUserName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "AdminUser{" +
                "adminUserId=" + adminUserId +
                ", adminUserName='" + adminUserName + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}
