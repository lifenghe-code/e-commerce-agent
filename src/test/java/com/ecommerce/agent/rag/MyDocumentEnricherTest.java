package com.ecommerce.agent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class MyDocumentEnricherTest {
    @Resource
    MyDocumentEnricher myDocumentEnricher;

    @Resource
    MyDocumentLoader myDocumentLoader;

    @Resource
    ChatModel dashscopeChatModel;

    @Resource
    VectorStore pgVectorStore;

    @Test
    void enrichDocumentsByKeyword() {
        List<Document> documents = myDocumentLoader.loadMarkdowns();

        myDocumentEnricher.enrichDocumentsByKeyword(documents);
        // documents 中新增关键字

    }

    @Test
    void doChatWithRag() {

        ChatResponse response = ChatClient.builder(dashscopeChatModel)
                .build().prompt()
                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                .user("iPhone手机的重量")
                .call()
                .chatResponse();
        String text = response.getResult().getOutput().getText();
        assertNotNull(text);

    }
}