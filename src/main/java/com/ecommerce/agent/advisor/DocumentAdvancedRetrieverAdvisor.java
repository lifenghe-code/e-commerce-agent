package com.ecommerce.agent.advisor;

import com.ecommerce.agent.config.ConsoleColorConfig;
import com.ecommerce.agent.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;

@Slf4j
public class DocumentAdvancedRetrieverAdvisor {

    private Advisor advisor;

    public DocumentAdvancedRetrieverAdvisor() {
        VectorStore customerServiceAppVectorStore = (VectorStore) SpringContextUtil.getBean("pgVectorVectorStore");
        // 4. 配置文档检索器
        DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(customerServiceAppVectorStore)
                .similarityThreshold(0.5)    // 设置相似度阈值
                .topK(3)                     // 返回前3个最相关的文档
                .build();

        // 5. 创建上下文感知的查询增强器
        this.advisor = RetrievalAugmentationAdvisor.builder()
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .documentRetriever(retriever)
                .build();
    }
    // 添加公共的getter方法
    public Advisor getAdvisor() {
        log.info(ConsoleColorConfig.BLUE + "高级检索顾问");
        return advisor;
    }
}
