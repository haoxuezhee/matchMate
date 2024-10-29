package com.hxt.matchMate.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: UserTeam
 * Package: com.hxt.matchMate.bean
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/19 14:49
 * @Version 1.0
 */
@Data
public class UserTeam implements Serializable {

    private static final long serialVersionUID = 3L;
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(value = "userId")
    private Long userId;
    @TableField(value = "teamId")
    private Long teamId;
    @TableField(value = "joinTime")
    private Date joinTime;
    @TableField(value = "createTime",fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(value = "updateTime",fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(value = "isDelete")
    private Integer isDelete;
}
