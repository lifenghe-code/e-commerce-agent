package com.ecommerce.agent.app;

import com.ecommerce.agent.advisor.MyLoggerAdvisor;
import com.ecommerce.agent.model.ToolCallAgent;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;

/**
 * 实现ReAct规划引擎将"大促活动策划"复杂任务分解为自动化子任务，需要结合任务分解、工具调用、动态决策和循环验证机制。
 */
public class EventPlanManus extends ToolCallAgent {

    @Resource
    private ToolCallback[] allTools;

    public EventPlanManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        super(allTools);
        this.setName("EventPlanManus");
        String SYSTEM_PROMPT = """
                1. 竞品分析：获取TOP3竞品当前活动页数据
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
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
