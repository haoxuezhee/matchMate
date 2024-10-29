package com.hxt.matchMate.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: User
 * Package: com.hxt.usercenter.bean
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/1 17:24
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    @TableField(value = "userAccount")
    private String userAccount;
    @TableField(value = "avatarUrl")
    private String avatarUrl;
    private Integer gender;
    @TableField(value = "userPassword")
    private String userPassword;
    private String phone;
    private String email;
    @TableField(value = "userStatus")
    private Integer userStatus;
    @TableField(value = "createTime",fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(value = "updateTime",fill = FieldFill.UPDATE)
    private Date updateTime;
    @TableField(value = "isDelete")
    @TableLogic
    private Integer isDelete;
    @TableField(value = "userRole")
    private Integer userRole;
    @TableField(value = "planetCode")
    private String planetCode;
    private String tags;

    private String profile;

}
