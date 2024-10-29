package com.hxt.matchMate.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxt.matchMate.bean.User;
import com.hxt.matchMate.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.PipedReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: PreCacheJob
 * Package: com.hxt.matchMate.job
 * Description:  缓存预热任务
 *
 * @Author hxt
 * @Create 2024/9/18 19:20
 * @Version 1.0
 */
@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private RedisTemplate<String,Object> template;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedissonClient redissonClient;

    //重点用户(todo 可优化成从缓存或数据库中取)
    private List<Long> mainUserList = Arrays.asList(1l);

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Shanghai")
    public void doCacheRecommendUser(){
        log.info("Starting cache warmup...");

        RLock lock = redissonClient.getLock("mate:percachejob:docache:lock");
        try {
            if(lock.tryLock(0,30000,TimeUnit.MILLISECONDS)){
                for(Long userId : mainUserList){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userMapper.selectPage(new Page<>(1,20), queryWrapper);
                    String redisKey=String.format("mate:user:recommend:%s",userId);
                    ValueOperations<String, Object> valueOperations = template.opsForValue();
                    try {
                        valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redisKey error",e);
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }


    }

}
