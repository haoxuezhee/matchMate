package com.hxt.matchMate.common;

/**
 * ClassName: ResultUtils
 * Package: com.hxt.usercenter.common
 * Description:
 *          返回工具类
 * @Author hxt
 * @Create 2024/9/9 17:06
 * @Version 1.0
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,"ok",data,"");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }


    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode,String msg,String description){
        return new BaseResponse<>(errorCode.getCode(),msg,description);
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode.getCode(),errorCode.getMsg(),description);
    }


    /**
     * 失败
     * @param
     * @return
     */
    public static BaseResponse error(int code,String msg,String description){
        return new BaseResponse<>(code,msg,description);
    }



}
