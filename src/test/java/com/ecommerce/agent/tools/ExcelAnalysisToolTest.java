package com.ecommerce.agent.tools;

import com.ecommerce.agent.app.CustomerServiceApp;
import com.ecommerce.agent.config.ConsoleColorConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@SpringBootTest
@Slf4j
class ExcelAnalysisToolTest {

    @Resource
    CustomerServiceApp customerServiceApp;

    // AI 工具调用
    @Resource
    private ToolCallback[] allTools;

    @Test
    void readFile() {
        UUID uuid = UUID.randomUUID();
        String fileName = "H:\\Java\\Project\\e-commerce-agent\\src\\main\\resources\\河北省定向选调.xlsx";
        String message = "现在我想你提供文件的路径："+ fileName +"请帮我回答：我是工学硕士，报考哪个岗位比较合适？";
        String result = customerServiceApp.doChatWithTools(message, String.valueOf(uuid));


        log.info(ConsoleColorConfig.BLUE + "content: {}", result);

    }
}