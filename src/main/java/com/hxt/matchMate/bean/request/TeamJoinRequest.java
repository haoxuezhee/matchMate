package com.hxt.matchMate.bean.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: TeamAddRequest
 * Package: com.hxt.matchMate.bean.request
 * Description:
 *          队伍加入
 * @Author hxt
 * @Create 2024/9/19 17:02
 * @Version 1.0
 */
@Data
public class TeamJoinRequest implements Serializable {


    private static final long serialVersionUID = 8833118538278843394L;

    private Long teamId;

    private String password;
    private Long userId;
}


