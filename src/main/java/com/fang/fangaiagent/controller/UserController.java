package com.fang.fangaiagent.controller;

import cn.hutool.core.util.ObjUtil;
import com.fang.fangaiagent.common.BaseResponse;
import com.fang.fangaiagent.common.ErrorCode;
import com.fang.fangaiagent.common.ResultUtils;
import com.fang.fangaiagent.constant.UserConstant;
import com.fang.fangaiagent.entity.User;
import com.fang.fangaiagent.exception.BusinessException;
import com.fang.fangaiagent.service.Impl.LoginCacheService;
import com.fang.fangaiagent.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private LoginCacheService loginCacheService;

    @PostMapping("/register")
    public BaseResponse userRegister(String userName, String password, String checkPassword, HttpServletRequest request) {
        Boolean result = userService.userRegister(userName, password, checkPassword);
        if(result) {
            return ResultUtils.success("注册成功");
        }else {
            return ResultUtils.error("注册失败");
        }
    }

    @PostMapping("/login")
    public BaseResponse<User> login(String userName, String password, HttpServletRequest request) {
        User user = userService.userLogin(userName, password, request);
        if(ObjUtil.isNull(user)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        loginCacheService.login(user);
        return ResultUtils.success(user);
    }


    @GetMapping("/get/login")
    public BaseResponse<User> getLoginUser(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(ObjUtil.isNull(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "登录状态失效");
        }
        User loginUser = loginCacheService.validateToken(userId);
        if(ObjUtil.isNull(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(loginUser);
    }

    @GetMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        Long userId = (Long) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(ObjUtil.isNull(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户并未登录");
        }
        loginCacheService.logout(userId);
        return ResultUtils.success("退出登录成功");
    }
}
