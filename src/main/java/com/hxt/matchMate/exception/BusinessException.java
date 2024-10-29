package com.hxt.matchMate.exception;

import com.hxt.matchMate.common.ErrorCode;

/**
 * ClassName: BusinessException
 * Package: com.hxt.usercenter.exception
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/9 16:55
 * @Version 1.0
 */
public class BusinessException extends RuntimeException{

    private final int code;

    private final String description;


    public BusinessException(String message,int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code=errorCode.getCode();
        this.description=errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMsg());
        this.code=errorCode.getCode();
        this.description=description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
