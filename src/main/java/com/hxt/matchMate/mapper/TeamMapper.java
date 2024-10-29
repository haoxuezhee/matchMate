package com.hxt.matchMate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxt.matchMate.bean.Team;
import com.hxt.matchMate.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
* @author 86185
* @description 针对表【team】的数据库操作Mapper
* @createDate 2024-09-19 14:41:41
* @Entity generator.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 查询入队用户信息
     */
    List<User> getUsersByTeam(Long teamId);

}




