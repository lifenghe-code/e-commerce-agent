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
        String rootPath = System.getProperty("user.dir");
        String filePath = rootPath + "\\src\\main\\resources\\document\\iphone_products_100.xlsx";
        log.info(filePath);
        String message = "现在我向你提供文件的路径："+ filePath +"请帮我分析竞品，并根据我方成本制定促销活动，计算利润率，并进行文案生成";
        String result = customerServiceApp.doChatWithTools(message, String.valueOf(uuid));


        log.info(ConsoleColorConfig.BLUE + "content: {}", result);

    }
}