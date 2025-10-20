package com.fang.fangaiagent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fang.fangaiagent.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 */
public interface UserService extends IService<User> {
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);

    Boolean userRegister(String userName, String password, String checkPassword);
}