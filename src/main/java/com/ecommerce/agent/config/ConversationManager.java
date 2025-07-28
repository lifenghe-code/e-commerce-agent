package com.ecommerce.agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class ConversationManager {

    private static final String SESSION_KEY = "active_sessions";
    // TODO 实现过期删除策略
    private static final String HISTORY_KEY = "agent:chat:history";
    private static final String SUMMARY_KEY = "agent:chat:summary";
    private static final long SESSION_TIMEOUT_MINUTES = 1;

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;


    public ConversationManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * 创建新会话并设置过期时间
     */
    public void createOrRefreshSession(String conversationId) {

        if (exists(conversationId)) {
            refreshSession(conversationId);
        }
        else{
            log.info("Creating session for conversationId: " + conversationId);
            long expireTime = Instant.now().plus(Duration.ofMinutes(SESSION_TIMEOUT_MINUTES)).getEpochSecond();
            zSetOperations.add(SESSION_KEY, conversationId, expireTime);
        }

    }

    /**
     * 刷新会话过期时间
     */
    public boolean refreshSession(String conversationId) {
        if (!exists(conversationId)) {
            return false;
        }
        zSetOperations.remove(SESSION_KEY, conversationId);
        long expireTime = Instant.now().plus(Duration.ofMinutes(SESSION_TIMEOUT_MINUTES)).getEpochSecond();
        return Boolean.TRUE.equals(zSetOperations.add(SESSION_KEY, String.valueOf(conversationId), expireTime));
    }

    /**
     * 检查会话是否存在
     */
    public boolean exists(String conversationId) {
        return zSetOperations.score(SESSION_KEY, conversationId) != null;
    }

    /**
     * 删除会话
     */
    public boolean removeSession(String conversationId) {
        return zSetOperations.remove(SESSION_KEY, conversationId) > 0;
    }

    /**
     * 获取所有活跃会话
     */
    public Set<String> getAllActiveSessions() {
        long currentTime = Instant.now().getEpochSecond();
        return zSetOperations.rangeByScore(SESSION_KEY, currentTime, Double.POSITIVE_INFINITY);
    }

    /**
     * 每1分钟执行一次过期会话清理
     */
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredSessions() {
        log.info("开始清理过期会话");

        long currentTime = Instant.now().getEpochSecond();
        // 1. 获取分数在 [0, currentTime] 范围内的所有元素
        Set<ZSetOperations.TypedTuple<String>> tuples =
                zSetOperations.rangeByScoreWithScores(SESSION_KEY, 0, currentTime);

        // 2. 提取所有待删除的 key
        List<String> keysToRemove = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            keysToRemove.add(HISTORY_KEY + ":" + tuple.getValue());
            keysToRemove.add(SUMMARY_KEY + ":" + tuple.getValue());
        }

        // 3. 执行删除操作
        Long removedCount = zSetOperations.removeRangeByScore(SESSION_KEY, 0, currentTime);

        redisTemplate.delete(keysToRemove);

        // keysToRemove 即为被删除的元素的 key 列表
        System.out.println("被删除的元素数量: " + removedCount);
        System.out.println("被删除的元素 key: " + keysToRemove);


        if (removedCount != null && removedCount > 0) {
            log.info("清理了 " + removedCount + " 个过期会话");
        }
    }
}
