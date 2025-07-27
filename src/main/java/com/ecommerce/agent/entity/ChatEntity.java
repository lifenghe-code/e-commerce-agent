package com.ecommerce.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatEntity implements Serializable {
    Long conversationId;  // 所属会话的ID
    String chatId; // 对话ID
    String type;
    String text;
    Long date = new Date().getTime(); // 最近更新时间
}
