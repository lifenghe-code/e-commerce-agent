package com.ecommerce.agent.chatmemory;

import com.ecommerce.agent.entity.ChatEntity;
import com.ecommerce.agent.service.MongoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;

import java.util.*;
@Component
public class ChatMongoMemory implements ChatMemory {

    @Resource
    MongoService mongoService;



    @Override
    public void add(String conversationId, List<Message> messages) {

        mongoService.saveChat(conversationId, messages);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<ChatEntity> chats = mongoService.getChats(conversationId, lastN);
        List<Message> listOut = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object obj : chats) {
            ChatEntity chat = objectMapper.convertValue(obj, ChatEntity.class);
            if (MessageType.USER.getValue().equals(chat.getType())) {
                listOut.add(new UserMessage(chat.getText()));
            } else if (MessageType.ASSISTANT.getValue().equals(chat.getType())) {
                listOut.add(new AssistantMessage(chat.getText()));
            } else if (MessageType.SYSTEM.getValue().equals(chat.getType())) {
                listOut.add(new SystemMessage(chat.getText()));
            }
        }
        return listOut;
    }

    @Override
    public void clear(String conversationId) {
        mongoService.deleteChat(conversationId);
    }
}
