package com.hxt.matchMate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxt.matchMate.bean.Team;
import com.hxt.matchMate.bean.User;
import com.hxt.matchMate.bean.dto.TeamQuery;
import com.hxt.matchMate.bean.request.TeamDeleteRequest;
import com.hxt.matchMate.bean.request.TeamJoinRequest;
import com.hxt.matchMate.bean.request.TeamQuitRequest;
import com.hxt.matchMate.bean.request.TeamUpdateRequest;
import com.hxt.matchMate.bean.vo.TeamUserVo;

import java.util.List;


/**
* @author 86185
* @description 针对表【team】的数据库操作Service
* @createDate 2024-09-19 14:41:41
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team 队伍信息
     * @param loginUser 登录用户
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     *
     * @param teamQuery 允许返回的队伍信息
     * @param isAdmin
     * @return
     */
    List<TeamUserVo> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest 允许返回的队伍信息
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 用户推出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean deleteTeam(TeamDeleteRequest teamQuitRequest, User loginUser);
}
