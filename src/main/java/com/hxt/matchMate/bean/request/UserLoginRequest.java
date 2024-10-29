package com.hxt.matchMate.bean.request;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserRegisterRequest
 * Package: com.hxt.usercenter.bean.request
 * Description:
 *              用户登录请求体
 * @Author hxt
 * @Create 2024/9/2 13:56
 * @Version 1.0
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 42L;

    private String userAccount;
    private String userPassword;

}
