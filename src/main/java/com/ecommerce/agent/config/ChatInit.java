package com.ecommerce.agent.config;

import com.ecommerce.agent.chatmemory.ChatRedisMemory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 如果需要自定义 Bean 名称、作用域、依赖实例或添加创建逻辑：
 */
//@Configuration
//@RequiredArgsConstructor
//public class ChatInit {
//
//
//    @Bean
//    public ChatMemory chatRedisMemory(RedisTemplate<String, Object> messageRedisTemplate) {
//        return new ChatRedisMemory(messageRedisTemplate);
//    }
//
//}
