package com.ecommerce.agent.app;

import com.ecommerce.agent.advisor.MyLoggerAdvisor;
import com.ecommerce.agent.config.ConsoleColorConfig;
import com.ecommerce.agent.model.ToolCallAgent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * 实现ReAct规划引擎将"大促活动策划"复杂任务分解为自动化子任务，需要结合任务分解、工具调用、动态决策和循环验证机制。
 */
@Component
@Slf4j
public class EventPlanManus extends ToolCallAgent {

    private final ChatClient chatClient;

    @Resource
    private ToolCallback[] allTools;

    public EventPlanManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);

        this.setName("EventPlanManus");
        String SYSTEM_PROMPT = """
                1. 竞品分析：获取TOP3竞品数据
                2. 折扣计算：结合历史数据计算最优折扣力度
                3. 文案生成：根据商品特性生成营销话术
                4. 海报设计：匹配文案生成视觉素材
                输出格式：{"steps":[...],"dependencies":{"步骤3":["步骤1","步骤2"]}}
               """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools. \s
                For complex tasks, you can break down the problem and use different tools step by step to solve it. \s
                After using each tool, clearly explain the execution results and suggest the next steps. \s
                If you want to stop the interaction at any point, use the `terminate` tool/function call. \s
               \s""";
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(20);
        // 初始化客户端
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }

    public String grandPromotion(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                // .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info(ConsoleColorConfig.BLUE + "content: {}", content);
        return content;
    }
}
