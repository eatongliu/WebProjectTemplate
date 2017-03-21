package com.gpdata.template.user.service;

import com.gpdata.template.user.entity.User;

/**
 * Created by acer_liuyutong on 2017/3/20.
 */
public interface UserService {

    /**
     * 通过主键获取用户
     * @return
     */
    public User getUserById(Long userId);

    User getUserByName(String account);
}
