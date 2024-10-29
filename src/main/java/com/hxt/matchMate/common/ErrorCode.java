package com.hxt.matchMate.common;

/**
 * ClassName: ErrorCode
 * Package: com.hxt.usercenter.common
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/7 20:28
 * @Version 1.0
 */

public enum ErrorCode {
     SUCCESS(200,"ok",""),
     PARAMS_ERROR(40000,"请求参数为空",""),
     NULL_ERROR(40001,"数据为空",""),
     NOT_LOGIN(40100,"未登录",""),
     NO_AUTH(40101,"无权限",""),
     FORBIDDEN(40301,"无权限",""),
    SYSTEM_ERROR(50000,"系统内部异常","");


    private final int code;
    private final String msg;
    private final String description;

    ErrorCode(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }
}
