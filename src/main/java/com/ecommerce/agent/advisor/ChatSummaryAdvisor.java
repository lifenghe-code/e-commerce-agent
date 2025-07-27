package com.ecommerce.agent.advisor;

import com.ecommerce.agent.config.ConsoleColorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatResponse;

@Slf4j
public class ChatSummaryAdvisor implements CallAroundAdvisor {
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }


    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

        String originalSystemPrompt = advisedRequest.systemText();

        String newSystemPrompt = originalSystemPrompt + "\n请在回答完用户问题后，再为问题和答案生成一个简要的摘要";
        AdvisedRequest newAdvisedRequest= AdvisedRequest.from(advisedRequest).systemText(newSystemPrompt).build();


        AdvisedResponse advisedResponse = chain.nextAroundCall(newAdvisedRequest);

        return advisedResponse;
    }

}
