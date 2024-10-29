package com.hxt.matchMate.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: RedissonConfig
 * Package: com.hxt.matchMate.config
 * Description:
 *
 * @Author hxt
 * @Create 2024/9/18 21:09
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    public RedissonClient redissonClient(){
        // 创建配置
        String redisAddress=String.format("redis://%s:%s",host,port);
        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(3);
        //创建实例
        RedissonClient redisson = Redisson.create(config);
        return  redisson;
    }
}
