package com.hxt.matchMate.bean.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: TeamAddRequest
 * Package: com.hxt.matchMate.bean.request
 * Description:
 *          队伍修改请求体
 * @Author hxt
 * @Create 2024/9/19 17:02
 * @Version 1.0
 */
@Data
public class TeamUpdateRequest implements Serializable {


    private static final long serialVersionUID = 8833118538278843394L;

    private Long id;
    private String name;

    private String description;

    @TableField(value = "maxNum")
    private Integer maxNum;

    @TableField(value = "expireTime")
    private Date expireTime;
    
    private Integer status;

    private String password;
}


