package com.gpdata.template.admin.service;

import com.gpdata.template.admin.entity.AdminUser;

/**
 * Created by acer_liuyutong on 2017/3/20.
 */
public interface AdminUserService {

    public AdminUser getAdminUserByUserName(String loginname);

}
