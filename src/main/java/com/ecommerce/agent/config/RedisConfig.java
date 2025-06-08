package com.ecommerce.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @Author majinzhong
 * @Date 2025/5/7 14:49
 * @Version 1.0
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> messageRedisTemplate(RedisConnectionFactory factory, Jackson2ObjectMapperBuilder builder) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用String序列化器作为key的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        // 对value进行序列化
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        // 设置hash类型的key和value序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();
        return template;
    }
}
