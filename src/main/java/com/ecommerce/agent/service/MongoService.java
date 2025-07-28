package com.ecommerce.agent.service;

import com.ecommerce.agent.entity.ChatEntity;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class MongoService {

    @Resource
    private MongoTemplate mongoTemplate;

    // 创建新对话
    public void saveChat(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            ChatEntity chat = new ChatEntity();
            chat.setConversationId(conversationId);
            chat.setChatId(UUID.randomUUID().toString());
            chat.setText(message.getText());
            mongoTemplate.save(chat);
        }

    }

    // 获取会话
    public List<ChatEntity> getChats(String conversationId, int lastN) {
        // 1. 构建查询条件（按 conversation_id 筛选）
        Query query = Query.query(Criteria.where("conversation_id").is(conversationId));

        // 2. 按时间戳降序排序（假设有 create_time 字段）
        query.with(Sort.by(Sort.Direction.DESC, "date"));

        // 3. 限制返回结果数量（取最新的 lastN 条）
        query.limit(lastN);

        // 4. 执行查询
        List<ChatEntity> chatEntities = mongoTemplate.find(query, ChatEntity.class);

        // 5. 反转结果（如果需要保持时间升序）
        Collections.reverse(chatEntities);

        return chatEntities;

    }


    // 删除对话
    public long deleteChat(String conversationId) {
        Query query = Query.query(Criteria.where("conversation_id").is(conversationId));
        return mongoTemplate.remove(query, ChatEntity.class).getDeletedCount();
    }

    // 自定义查询：获取长时间未活跃的会话
    public List<ChatEntity> findInactiveSessions(long inactiveThresholdMs) {
        long cutoffTime = System.currentTimeMillis() - inactiveThresholdMs;
        Query query = Query.query(
                Criteria.where("lastAccessTime").lt(cutoffTime)
        );
        return mongoTemplate.find(query, ChatEntity.class);
    }
}
