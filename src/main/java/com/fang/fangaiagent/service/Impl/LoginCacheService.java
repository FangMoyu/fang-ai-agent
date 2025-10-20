package com.fang.fangaiagent.service.Impl;// LoginCacheService.java

import cn.hutool.core.util.ObjUtil;
import com.fang.fangaiagent.common.ErrorCode;
import com.fang.fangaiagent.entity.User;
import com.fang.fangaiagent.exception.BusinessException;
import com.fang.fangaiagent.status.LoginStatus;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginCacheService {
    // 使用 Caffeine 构建一个缓存，key 是 token，value 是 LoginStatus
    private final Cache<String, LoginStatus> loginCache;
    public LoginCacheService() {
        this.loginCache = Caffeine.newBuilder()
                .maximumSize(1000) // 最多缓存 1000 个登录会话
                .expireAfterWrite(30, TimeUnit.MINUTES) // 登录状态30分钟后过期
                .recordStats() // 开启统计（可选）
                .build();
    }

    /**
     * 用户登录，生成 token 并缓存登录状态
     */
    public void login(User loginUser) {
        if(ObjUtil.isNull(loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        Long userId = loginUser.getId();
        LoginStatus status = new LoginStatus(loginUser.getId(), loginUser,30 * 60); // 30分钟有效期
        loginCache.put(String.valueOf(userId), status);
        log.info("用户: {} 登录成功", loginUser.getUserName());
    }

    /**
     * 验证 token 是否有效
     * @return 如果有效返回 LoginStatus，否则返回 null
     */
    public User validateToken(Long userId) {
        LoginStatus status = loginCache.getIfPresent(String.valueOf(userId));
        if (status != null && !status.isExpired()) {
            return status.getLoginUser();
        } else {
            // 如果缓存中存在但已过期，手动移除（Caffeine 会自动清理，但这里可主动处理）
            if (status != null) {
                loginCache.invalidate(String.valueOf(userId));
            }
            return null;
        }
    }

    /**
     * 用户登出，清除缓存
     *
     */
    public void logout(Long userId) {
        loginCache.invalidate(String.valueOf(userId));
        log.info("id: {} 用户已退出登录", userId);
    }

    /**
     * 获取缓存统计信息（可选）
     */
    public String getStats() {
        return loginCache.stats().toString();
    }
}