package com.hxt.matchMate.bean.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: UserVo
 * Package: com.hxt.matchMate.bean.vo
 * Description:
 *          用户封装类
 * @Author hxt
 * @Create 2024/9/20 19:38
 * @Version 1.0
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = -4616663524606437264L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @TableField(value = "userAccount")
    private String userAccount;
    @TableField(value = "avatarUrl")
    private String avatarUrl;
    private Integer gender;
    private String phone;
    private String email;
    @TableField(value = "userStatus")
    private Integer userStatus;
    @TableField(value = "createTime",fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(value = "updateTime",fill = FieldFill.UPDATE)
    private Date updateTime;
    @TableField(value = "userRole")
    private Integer userRole;
    @TableField(value = "planetCode")
    private String planetCode;
    private String tags;

    private String profile;

}
