package com.ecommerce.agent.advisor;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.ecommerce.agent.config.ConsoleColorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
/**
 * 预检索阶段的 Advisor
 * 预检索阶段负责处理和优化用户的原始查询，以提高后续检索的质量。
 */
@Component
public class QueryTransformerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private final DashScopeChatModel dashScopeChatModel;

    public QueryTransformerAdvisor(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 同步调用拦截：在同步调用大语言模型（LLM）时触发，允许你在请求发送前修改请求，或在响应返回后处理结果。
     * @param advisedRequest
     * @param chain
     * @return
     */
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

        Query query = new Query(advisedRequest.userText());

        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashScopeChatModel);


        QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();

        Query transformedQuery = queryTransformer.transform(query);
        AdvisedRequest build = AdvisedRequest.from(advisedRequest)
                .userText(transformedQuery.text())
                .userParams(advisedRequest.userParams())
                .build();
        log.info(ConsoleColorConfig.BLUE + "用户原始提问：" +advisedRequest.userText());
        log.info(ConsoleColorConfig.BLUE + "转换后的提问："+build.userText());


        return chain.nextAroundCall(build);
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return null;
    }
}
