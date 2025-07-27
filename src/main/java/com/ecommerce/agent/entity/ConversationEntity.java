package com.ecommerce.agent.entity;

import java.io.Serializable;
import java.util.Date;

public class ConversationEntity implements Serializable {
    Long conversationId; //会话的ID
    String conversationName;
    Long date = new Date().getTime(); // 会话最后的更新时间
}
