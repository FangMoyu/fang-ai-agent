package com.fang.fangaiagent.status;

import com.fang.fangaiagent.entity.User;
import lombok.Data;
@Data
public class LoginStatus {
    private Long userId;
    private long loginTime;
    private long expireTime;
    private User loginUser;

    public LoginStatus(Long userId, User loginUser, long expireAfterSeconds) {
        this.userId = userId;
        this.loginTime = System.currentTimeMillis();
        this.expireTime = this.loginTime + (expireAfterSeconds * 1000);
        this.loginUser = loginUser;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime;
    }
}