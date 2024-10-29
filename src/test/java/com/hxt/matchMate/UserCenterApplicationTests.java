package com.hxt.matchMate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxt.matchMate.bean.User;
import com.hxt.matchMate.bean.UserTeam;
import com.hxt.matchMate.job.PreCacheJob;
import com.hxt.matchMate.mapper.TeamMapper;
import com.hxt.matchMate.mapper.UserMapper;
import com.hxt.matchMate.service.UserService;
import com.hxt.matchMate.service.UserTeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

@SpringBootTest
class UserCenterApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {

        User user = userMapper.selectById(1L);
        System.out.println(user);
    }


    @Test
    void testAddUser(){

        User user = new User();
        user.setUsername("lisi");
        user.setUserAccount("123");
        user.setAvatarUrl("xxx");
        user.setGender(0);
        user.setUserPassword("111111");
        user.setPhone("14312321232");
        user.setEmail("222@qq.com");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setPlanetCode("8796");
        user.setTags("bbb");
        boolean save = userService.save(user);
        System.out.println(user.getId()+":"+save);
    }


    @Test
    void testMd5(){
        final String SALT="HTH";
        String s = DigestUtils.md5DigestAsHex((SALT + "userPasswprd").getBytes());
        System.out.println(s);
    }

    @Test
    void userRegister(){
        String userAccount="hxt1";
        String userPassword="";
        String checkPassword="12345678";
       // long register = userService.userRegister(userAccount, userPassword, checkPassword);
       // Assertions.assertEquals(-1,register);


        userAccount="wangwu";
        userPassword="12345678";
        checkPassword="12345678";
       // register = userService.userRegister(userAccount, userPassword, checkPassword);
       // Assertions.assertTrue(register>0);



    }


    @Test
    void test02(){
        final String SALT="HTH";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);

    }


    @Test
    void testSearchByTags(){
        ArrayList<String> list = new ArrayList<>();
        list.add("java");
        List<User> users = userService.searchUserByTags(list);
        System.out.println(users);
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisConnection() {
        // 尝试设置一个键值对
        redisTemplate.opsForValue().set("testKey", "testValue");
        // 然后获取这个键的值
        String value = (String) redisTemplate.opsForValue().get("testKey");
        // 断言获取的值是否正确
        assert "testValue".equals(value) : "连接Redis失败";
        System.out.println("连接Redis成功，获取的值为：" + value);
    }

    @Test
    void testJob(){
       userService.triggerPreCacheJob();
    }

    @Autowired
    private TeamMapper teamMapper;
    @Test
    void testGetUserByTeam(){
        List<User> users = teamMapper.getUsersByTeam(1L);
        for (User user : users) {
            System.out.println(user);
        }
    }
    @Autowired
    private UserTeamService userTeamService;
    @Test
    void countUser(){
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        List<UserTeam> list = userTeamService.list(queryWrapper);
        list.forEach(userTeam -> {
            System.out.println(userTeam);
        });

    }

}
