package com.hxt.matchMate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxt.matchMate.bean.User;
import org.apache.ibatis.annotations.Mapper;


/**
* @author 86185
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-09-01 18:27:29
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




