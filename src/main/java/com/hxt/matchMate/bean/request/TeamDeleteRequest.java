package com.hxt.matchMate.bean.request;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: TeamQuitRequest
 * Package: com.hxt.matchMate.bean.request
 * Description:
 *          用户推出队伍
 * @Author hxt
 * @Create 2024/9/21 16:48
 * @Version 1.0
 */
@Data
public class TeamDeleteRequest implements Serializable {

    private static final long serialVersionUID = 51365361228448655L;
    private Long teamId;
}
