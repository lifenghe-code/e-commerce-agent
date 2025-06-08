package com.ecommerce.agent.app;

import com.ecommerce.agent.advisor.MyLoggerAdvisor;
import com.ecommerce.agent.model.ToolCallAgent;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class EManus extends ToolCallAgent {
        @Resource
        private ToolCallback[] allTools;
        public EManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
            super(allTools);
            this.setName("EManus");
            String SYSTEM_PROMPT = """
                You are EManus, an all-capable AI assistant, aimed at solving any task presented by the user. \s
                You have various tools at your disposal that you can call upon to efficiently complete complex requests. \s
               \s""";
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
