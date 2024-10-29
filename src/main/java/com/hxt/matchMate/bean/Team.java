package com.hxt.matchMate.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: Team
 * Package: com.hxt.matchMate.bean
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/19 14:42
 * @Version 1.0
 */
@Data
public class Team implements Serializable {
    private static final long serialVersionUID = 2L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    @TableField(value = "maxNum")
    private Integer maxNum;

    @TableField(value = "expireTime")
    private Date expireTime;

    @TableField(value = "userId")
    private Long userId;

    private Integer status;

    private String password;

    @TableField(value = "createTime",fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "updateTime",fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(value = "isDelete")
    private Integer isDelete;
}
