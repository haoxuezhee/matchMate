package com.hxt.matchMate.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: BaseResponse
 * Package: com.hxt.usercenter.common
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/7 20:10
 * @Version 1.0
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 42L;

    private int code;
    private String msg;
    private T data;
    private String description;

    public BaseResponse(int code, String msg, String description) {
        this.code = code;
        this.msg = msg;
        this.data=null;
        this.description = description;
    }
    public BaseResponse(int code, String msg, T data, String description) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this(code,"",data,"");
    }
    public BaseResponse(ErrorCode errorCode) {
        this.code=errorCode.getCode();
        this.msg= errorCode.getMsg();
        this.data=null;
        this.description= errorCode.getDescription();;
    }



}
