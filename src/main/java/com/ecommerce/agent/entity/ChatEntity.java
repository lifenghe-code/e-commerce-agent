package com.ecommerce.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.session.Session;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "agentSessions")
public class ChatEntity implements Serializable {
    @Id
    private String id;  // 会话ID

    @Field("conversation_id")  // MongoDB 字段名
    String conversationId;  // 所属会话的ID
    String chatId; // 对话ID
    String type;
    String text;
    Long date = new Date().getTime(); // 最近更新时间
}
