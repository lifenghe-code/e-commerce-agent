package com.ecommerce.agent.chatmemory;

import com.ecommerce.agent.entity.ChatEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author majinzhong
 * @Date 2025/5/7 14:51
 * @Version 1.0
 */
@Slf4j
@Component
public class ChatRedisMemory implements ChatMemory {

    private static final String KEY_PREFIX = "agent:chat:history:";
    private final RedisTemplate<String, Object> redisTemplate;



    public ChatRedisMemory(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = KEY_PREFIX + conversationId;
        List<ChatEntity> listIn = new ArrayList<>();
        for(Message msg: messages){
            String[] strs = msg.getText().split("</think>");
            String text = strs.length==2?strs[1]:strs[0];

            ChatEntity ent = new ChatEntity();
            ent.setChatId(conversationId);
            ent.setType(msg.getMessageType().getValue());
            ent.setText(text);
            listIn.add(ent);
        }
        redisTemplate.opsForList().rightPushAll(key,listIn.toArray());
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {

        String key = KEY_PREFIX + conversationId;
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0){
            return Collections.emptyList();
        }

        int start = Math.max(0, (int) (size - lastN));
        List<Object> listTmp = redisTemplate.opsForList().range(key, start, -1);
        List<Message> listOut = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Object obj: listTmp){
            ChatEntity chat =  objectMapper.convertValue(obj, ChatEntity.class);
//            log.info("MessageType.USER [{}], chat.getType [{}]",MessageType.USER, chat.getType());
            if(MessageType.USER.getValue().equals(chat.getType())){
                listOut.add(new UserMessage(chat.getText()));
            }else if(MessageType.ASSISTANT.getValue().equals(chat.getType())){
                listOut.add(new AssistantMessage(chat.getText()));
            }else if(MessageType.SYSTEM.getValue().equals(chat.getType())){
                listOut.add(new SystemMessage(chat.getText()));
            }
        }
        return listOut;
    }

    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(KEY_PREFIX + conversationId);
    }
}
