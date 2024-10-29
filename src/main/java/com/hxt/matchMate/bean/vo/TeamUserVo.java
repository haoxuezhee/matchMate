package com.hxt.matchMate.bean.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName: TeamUserVo
 * Package: com.hxt.matchMate.bean.vo
 * Description:
 *          队伍用户的封装类
 * @Author hxt
 * @Create 2024/9/20 19:36
 * @Version 1.0
 */
@Data
public class TeamUserVo implements Serializable {


    private static final long serialVersionUID = 7641628948856342982L;
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

    @TableField(value = "createTime",fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "updateTime",fill = FieldFill.UPDATE)
    private Date updateTime;

    List<UserVo> userList;//入队用户列表

    UserVo user;//创建人用户信息

    Integer userNum;//加入人数

    boolean hasJoin = false;//用户是否加入

}
