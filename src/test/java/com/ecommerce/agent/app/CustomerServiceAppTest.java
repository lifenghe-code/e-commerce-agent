package com.ecommerce.agent.app;

import com.ecommerce.agent.config.ConsoleColorConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class CustomerServiceAppTest {


    @Resource
    CustomerServiceApp customerServiceApp;

    @Resource
    VectorStore pgVectorVectorStore;


    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，请说明一下你能做哪些事？";
        String answer = customerServiceApp.doChat(message, chatId);
        // 第二轮
    }

    @Test
    void doChatWithTools() {

        String chatId = UUID.randomUUID().toString();
        String message = "订单为 12345 ，我想生成退换货凭";
        String answer = customerServiceApp.doChatWithTools(message, chatId);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我居住在北京科技大学，请帮我找到 5 公里内合适的约会地点";
        String answer =  customerServiceApp.doChatWithMcp(message, chatId);
    }


    @Test
    void doChatWithConverter() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "介绍一下iphone16";
        String answer =  customerServiceApp.doChatWithConverter(message, chatId);
    }
}