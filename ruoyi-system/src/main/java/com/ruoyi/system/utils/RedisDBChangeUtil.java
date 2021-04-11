package com.ruoyi.system.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Aiolos
 * @date 2021/3/31 8:04 下午
 */
@Component
public class RedisDBChangeUtil {

    @Autowired
    private StringRedisTemplate redis;

    public void setDatabase(int index) {
        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redis.getConnectionFactory();
        if (connectionFactory != null && index != connectionFactory.getDatabase()) {
            connectionFactory.setDatabase(index);
            this.redis.setConnectionFactory(connectionFactory);
            connectionFactory.resetConnection();
        }
    }
}
