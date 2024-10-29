package com.hxt.matchMate.common;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: PageRequest
 * Package: com.hxt.matchMate.common
 * Description:
 *              通用分页请求参数
 * @Author hxt
 * @Create 2024/9/19 15:29
 * @Version 1.0
 */
@Data
public class PageRequest implements Serializable {

    /**
     * 页面大小
     */
    protected int pageSize=10;
    /**
     * 当前页面第几页
     */
    protected int pageNum=1;
}
