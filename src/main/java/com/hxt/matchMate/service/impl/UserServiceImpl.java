package com.hxt.matchMate.service.impl;
import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hxt.matchMate.bean.User;
import com.hxt.matchMate.bean.vo.UserVo;
import com.hxt.matchMate.common.ErrorCode;
import com.hxt.matchMate.common.ResultUtils;
import com.hxt.matchMate.constant.UserConstant;
import com.hxt.matchMate.exception.BusinessException;
import com.hxt.matchMate.job.PreCacheJob;
import com.hxt.matchMate.mapper.UserMapper;
import com.hxt.matchMate.service.UserService;

import com.hxt.matchMate.utils.AlgorithmUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author 86185
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-09-01 18:27:29
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private RedisTemplate template;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT="HTH";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {

        //1.校验
        if(StringUtils.isAllBlank(userAccount,userPassword,checkPassword,planetCode)){
             throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }

        if(userPassword.length() < 8 || checkPassword.length() <8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }

        if(planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }

        //账号特殊字符
        String validPattern = "^[a-zA-Z0-9._]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }
        //账号名不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号名重复");
        }

        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号重复");
        }

        //密码和校验密码
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPlanetCode(planetCode);
        user.setUserPassword(encryptPassword);
        boolean saveRet = this.save(user);
        if(!saveRet){
            throw new BusinessException(ErrorCode.NULL_ERROR,"保存失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if(StringUtils.isAllBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }

        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        //账号特殊字符
        String validPattern = "^[a-zA-Z0-9._]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(!matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }
        //2.检验密码

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userPassword",encryptPassword);
        queryWrapper.eq("userAccount",userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user==null){
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码不匹配");
        }

        //4.信息脱敏
        User safetyUser = getSafetyUser(user);


        //3.记录用户登录状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }



    /**
     * 信息脱敏
     * @param originalUser
     * @return
     */
    public User getSafetyUser(User originalUser){
        if (originalUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originalUser.getId());
        safetyUser.setUsername(originalUser.getUsername());
        safetyUser.setUserAccount(originalUser.getUserAccount());
        safetyUser.setAvatarUrl(originalUser.getAvatarUrl());
        safetyUser.setGender(originalUser.getGender());
        safetyUser.setPhone(originalUser.getPhone());
        safetyUser.setEmail(originalUser.getEmail());
        safetyUser.setUserStatus(originalUser.getUserStatus());
        safetyUser.setUserRole(originalUser.getUserRole());
        safetyUser.setCreateTime(new Date());
        safetyUser.setPlanetCode(originalUser.getPlanetCode());
        safetyUser.setTags(originalUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int UserLoginOut(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }


    /**
     * 根据标签查询用户
     * @param tagNameList 标签名集合
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //判断搜索标签在内存中是否存在
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            log.info("tagStr:{}",tagStr);
            if (tagStr == null || tagStr.isEmpty()) {
                return false; // 如果没有标签，直接返回false
            }
            try {
                Set<String> tagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {}.getType());
                tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());//判空
                return tagNameList.stream().allMatch(tagNameSet::contains); // 使用allMatch来检查所有标签是否存在
            } catch (JsonSyntaxException e) {
                // 日志记录异常，或者进行其他处理
                log.error("Error parsing JSON: {}", e.getMessage());
                return false; // 如果解析失败，返回false
            }
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user,User loginUser) {
        Long id = user.getId();
        if(id == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果是管理员，允许更新任意用户
        //如果是普通用户，只允许更新自身信息
//        if((isAdmin(loginUser)) && (user.getId() != loginUser.getId())){
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
//        User oldUser = userMapper.selectById(id);
//        if(oldUser == null){
//            throw new BusinessException(ErrorCode.NULL_ERROR);
//        }
        return userMapper.updateById(user);
    }


    public Page<User> getUsersByMemory(long pageSize,long pageNum,HttpServletRequest request){
        User loginUser = getLoginUser(request);
        //如果有缓存，直接从缓存中取
        String redisKey=String.format("mate:user:recommend:%s",loginUser.getId());
        ValueOperations<String, Object> valueOperations = template.opsForValue();
        Page<User> userPage =(Page<User>) valueOperations.get(redisKey);
        if(userPage !=null){
            return userPage;
        }

        //如果没有缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userMapper.selectPage(new Page<>(pageNum,pageSize), queryWrapper);
        //将数据写入缓存
        try {
            valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redisKey error",e);
        }
        return userPage;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if(userObj==null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User)userObj;
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user == null || user.getUserRole() != UserConstant.ADMIN_ROLE;

    }

    public boolean isAdmin(User loginUser) {
        return loginUser == null || loginUser.getUserRole() != UserConstant.ADMIN_ROLE;

    }


    /**
     * 根据标签查询用户(sql)
     * @param tagNameList
     * @return
     */
    @Deprecated
    public List<User> searchUserByTagsSql(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        for (String tagName : tagNameList){
            wrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(wrapper);
        return userList.stream().map(user -> {
            User safetyUser = getSafetyUser(user);
            return safetyUser;
        }).collect(Collectors.toList());
    }


    @Autowired
    private PreCacheJob preCacheJob;

    public void triggerPreCacheJob() {
        preCacheJob.doCacheRecommendUser();
    }

    @Override
    public List<User> matchUser(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtil.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }


//    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("id","tags");
//        queryWrapper.isNotNull("tags");
//    List<User> userList = this.list(queryWrapper);
//    String tags = loginUser.getTags();
//    Gson gson = new Gson();
//    List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {}.getType());
//    //用户标签的下标，相似度
//    List<Pair<User,Long>> list =new ArrayList<>();
//    //SortedMap<Integer,Long> indexDistanceMap = new TreeMap<>();
//    //依次计算所有用户和当前用户相似度
//        for (int i = 0; i < userList.size(); i++) {
//        User user = userList.get(i);
//        String userTags = user.getTags();
//        //无下标或者为当前用户自己
//        if(StringUtils.isBlank(userTags)  || user.getId() == loginUser.getId()){
//            continue;
//        }
//        List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
//        }.getType());
//        //计算分数
//        long distance = AlgorithmUtil.minDistance(tagList, userTagList);
//        list.add(new Pair<>(user,distance));
//    }
//    //按编辑距离由小到大排序
//    List<Pair<User, Long>> topUserList = list.stream()
//            .sorted((a, b) -> (int) (b.getValue() - a.getValue()))
//            .limit(num)
//            .collect(Collectors.toList());
//    // 原本顺序的 userId 列表
//    List<Long> userIdList = topUserList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
//    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.in("id", userIdList);
//    // 1, 3, 2
//    // User1、User2、User3
//    // 1 => User1, 2 => User2, 3 => User3
//    Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
//            .stream()
//            .map(user -> getSafetyUser(user))
//            .collect(Collectors.groupingBy(User::getId));
//    List<User> finalUserList = new ArrayList<>();
//        for (Long userId : userIdList) {
//        finalUserList.add(userIdUserListMap.get(userId).get(0));
//    }
//        return finalUserList;
}




