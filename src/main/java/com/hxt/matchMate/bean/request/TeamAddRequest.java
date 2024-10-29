package com.hxt.matchMate.bean.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: TeamAddRequest
 * Package: com.hxt.matchMate.bean.request
 * Description:
 *         添加队伍
 * @Author hxt
 * @Create 2024/9/19 17:02
 * @Version 1.0
 */
@Data
public class TeamAddRequest implements Serializable {

    private static final long serialVersionUID = 43L;
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
}


