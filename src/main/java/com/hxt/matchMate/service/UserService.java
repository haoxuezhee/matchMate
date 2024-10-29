package com.hxt.matchMate.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxt.matchMate.bean.User;
import com.hxt.matchMate.bean.vo.UserVo;
import com.hxt.matchMate.common.BaseResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
* @author 86185
* @description 针对表【user】的数据库操作Service
* @createDate 2024-09-01 18:27:29
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登陆
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 用户请求
     * @return 用户信息
     */

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 信息脱敏
     * @param originalUser 未脱敏对象
     * @return
     */
    User getSafetyUser(User originalUser);

    /**
     * 退出登录,用户注销
     * @return
     */
    int UserLoginOut(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList 标签名集合
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取当前登陆用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

     boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    Page<User> getUsersByMemory(long pageSize, long pageNum, HttpServletRequest request);

    void triggerPreCacheJob();

    /**
     * 匹配最佳用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUser(long num, User loginUser);
}
