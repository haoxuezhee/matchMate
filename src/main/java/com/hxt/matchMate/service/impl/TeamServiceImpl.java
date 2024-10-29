package com.hxt.matchMate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxt.matchMate.bean.Team;
import com.hxt.matchMate.bean.User;
import com.hxt.matchMate.bean.UserTeam;
import com.hxt.matchMate.bean.dto.TeamQuery;
import com.hxt.matchMate.bean.enums.TeamStatusEnum;
import com.hxt.matchMate.bean.request.*;
import com.hxt.matchMate.bean.vo.TeamUserVo;
import com.hxt.matchMate.bean.vo.UserVo;
import com.hxt.matchMate.common.ErrorCode;
import com.hxt.matchMate.exception.BusinessException;
import com.hxt.matchMate.mapper.TeamMapper;
import com.hxt.matchMate.service.TeamService;
import com.hxt.matchMate.service.UserService;
import com.hxt.matchMate.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author 86185
* @description 针对表【team】的数据库操作Service实现
* @createDate 2024-09-19 14:41:41
*/
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private UserService userService;
    /**
     * 创建队伍
     * @param team 队伍信息
     * @param loginUser 登录用户
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser){
        //1. 请求参数是否为空？
        if(team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //        2. 是否登录，未登录不允许创建
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        final long userId= loginUser.getId();
        //        3. 校验信息
        //        1. 队伍人数 > 1 且 <= 20
          int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if(maxNum <1 || maxNum >20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足要求");
        }
        //        2. 队伍标题 <= 20
        String teamName = team.getName();
        if(StringUtils.isBlank(teamName) || teamName.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不符合要求");
        }
        //        3. 描述 <= 512
        String teamDescription = team.getDescription();
        if(StringUtils.isNotBlank(teamDescription) && teamDescription.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述信息不符合要求");
        }
        //        4. status 是否公开（int）不传默认为 0（公开）

        int teamStatus =Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if(statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合要求");
        }
        //        5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        String password = team.getPassword();
        if(TeamStatusEnum.SECRET.equals(statusEnum) &&
                (StringUtils.isBlank(password) || password.length() >32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
        }
        //        6. 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间 > 当前时间");
        }
        //        7. 校验用户最多创建 5 个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long count = count(queryWrapper);
        if(count  >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍数量超出限制");
        }
        //        8. 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if(!result || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        //        9. 插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if(!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return team.getId();
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQuery teamQuery, boolean isAdmin) {

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if(teamQuery != null){
            Long id = teamQuery.getId();
            if(id !=null && id>0){
                queryWrapper.eq("id",id);
            }
            List<Long> idList = teamQuery.getIdList();
            if(CollectionUtils.isNotEmpty(idList)){
                queryWrapper.in("id",idList);
            }
            String searchText = teamQuery.getSearchText();
            if(StringUtils.isNotBlank(searchText)){
                queryWrapper.and(qw->qw.like("name",searchText)
                        .or().like("description",searchText));
            }
            String name = teamQuery.getName();
            if(StringUtils.isNotBlank(name)){
                queryWrapper.like("name",name);
            }
            String description = teamQuery.getDescription();
            if(StringUtils.isNotBlank(description)){
                queryWrapper.like("description",description);
            }

            Integer maxNum = teamQuery.getMaxNum();
            if(maxNum !=null && maxNum > 0 ){
                queryWrapper.eq("maxNum",maxNum);
            }
            Long userId = teamQuery.getUserId();//根据创建人查询
            if(userId !=null && userId > 0 ){
                queryWrapper.eq("userId",userId);
            }


            //不是管理员只能查看公开队伍
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if(statusEnum == null){
                statusEnum=TeamStatusEnum.PUBLIC;
            }
            if(!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status",statusEnum.getValue());
        }
        //不展示过期队伍
        queryWrapper.and(qw -> qw.isNull("expireTime").or().gt("expireTime", new Date()));

        List<Team> teamList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamUserVo> teamVoList = new ArrayList<>();
        for (Team team : teamList) {
            TeamUserVo teamUserVo = new TeamUserVo();
            try {
                BeanUtils.copyProperties(teamUserVo, team);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }

            // 关联查询队伍创建人信息
            if (team.getUserId() != null) {
                User user = userService.getById(team.getUserId());
                UserVo userVo = new UserVo();
                try {
                    BeanUtils.copyProperties(userVo, user);
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                teamUserVo.setUser(userVo);
            }

            // 关联查询入队用户信息
            if (team.getId() != null) {
                List<User> userList = teamMapper.getUsersByTeam(team.getId());
                List<UserVo> userVoList = new ArrayList<>();
                for (User user : userList) {
                    UserVo userVo = new UserVo();
                    try {
                        BeanUtils.copyProperties(userVo, user);
                    } catch (Exception e) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                    }
                    userVoList.add(userVo);
                }
                teamUserVo.setUserList(userVoList);
                teamUserVo.setUserNum(userVoList.size());
            }

            teamVoList.add(teamUserVo);
        }
        return teamVoList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if(teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if(id == null || id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam ==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //只有管理员和队伍创建人才能修改队伍
        if(!((oldTeam.getUserId() != loginUser.getId()) && (!userService.isAdmin(loginUser)))){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(oldTeam.getStatus());
        //如果队伍为加密，一顶要有密码
        if(TeamStatusEnum.SECRET.equals(statusEnum)){
            if(StringUtils.isBlank(oldTeam.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间一定要有密码");
            }
        }
        //如果队伍是公开，密码清除
        if(TeamStatusEnum.PUBLIC.equals(statusEnum)){
            oldTeam.setPassword("");
        }
        Team updateTeam = new Team();
        try {
            BeanUtils.copyProperties(updateTeam,teamUpdateRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if(teamJoinRequest ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long teamId = teamJoinRequest.getTeamId();
        if(teamId == null || teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team createdTeam = this.getById(teamId);
        if(createdTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"加入队伍不存在");
        }

        if(createdTeam.getExpireTime() == null && new Date().after(createdTeam.getExpireTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已经过期");
        }
//        if(createdTeam.getUserId() == loginUser.getId()){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入自己创建的队伍");
//        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(createdTeam.getStatus());
        if(TeamStatusEnum.PRIVATE.equals(statusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入私有队伍");
        }
        if(TeamStatusEnum.SECRET.equals(statusEnum)){
            if(StringUtils.isBlank(createdTeam.getPassword()) || !createdTeam.getPassword().equals(teamJoinRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不匹配，无法加入，请重输");
            }
        }
        Long userId = loginUser.getId();

        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long count = userTeamService.count(queryWrapper);//用户加入队伍数量(包括自己创建一共5个)
        if(count > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多加入5个队伍");
        }
            queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("teamId", teamId);
            long teamHasJoinNum = userTeamService.count(queryWrapper);//队伍中用户数
            if(teamHasJoinNum > createdTeam.getMaxNum()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数已满");
            }

//        queryWrapper.eq("userId",userId);
//        List<UserTeam> userTeams = userTeamService.list(queryWrapper);
//        for (UserTeam userTeam : userTeams) {
//            if(userTeam.getTeamId() == teamJoinRequest.getTeamId()){
//                throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入重复队伍");
//            }
//        }
        queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId",userId);
        long hasUserJoinTeam = userTeamService.count(queryWrapper);
        if(hasUserJoinTeam >0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"禁止加入重复队伍");
        }

        UserTeam userTeam=new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(createdTeam.getId());
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if(teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if(team == null ){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId",userId);
        long hasUserJoinTeam = userTeamService.count(queryWrapper);
        if(hasUserJoinTeam <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有加入队伍");
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        long userCount = userTeamService.count(queryWrapper);//队伍中人数
        //队伍中只有一人，解散
        if(userCount == 1){
            this.removeById(teamId);
        }else {
            log.info("是否为队长：{}",team.getUserId().equals( userId));
            //是队长
            if(team.getUserId().equals( userId)){
                //把队长转移给最早加入用户
                QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
                wrapper.eq("teamId",teamId);
                 wrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(wrapper);
                if(CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextUserLeaderId = nextUserTeam.getUserId();
                //更新当前队伍队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextUserLeaderId);
                boolean result = this.updateById(updateTeam);
                if(!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队长失败");
                }
            }

        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId",userId);
        return userTeamService.remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(TeamDeleteRequest teamDeleteRequest, User loginUser) {
        Long teamId = teamDeleteRequest.getTeamId();
        if(teamId == null || teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team createdTeam = this.getById(teamId);
        if(createdTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"删除队伍不存在");
        }
        Long userId = loginUser.getId();
        if(!createdTeam.getUserId().equals( userId)){
            throw new BusinessException(ErrorCode.FORBIDDEN,"无访问权限");
        }

        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍关联信息失败");
        }
        return this.removeById(teamId);
    }

}




