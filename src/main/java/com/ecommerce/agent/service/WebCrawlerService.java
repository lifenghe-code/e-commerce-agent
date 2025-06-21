package com.ecommerce.agent.service;

import com.ecommerce.agent.advisor.MyLoggerAdvisor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebCrawlerService {

    private final ChatClient chatClient;

    public WebCrawlerService(ChatModel dashscopeChatModel) {
        // 初始化客户端
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
    }

    /**
     * 获取网页原始内容
     */
    public String fetchRawContent(String url) throws IOException {
        return Jsoup.connect(url).get().html();
    }

    /**
     * 获取网页文本内容（去除HTML标签）
     */
    public String fetchTextContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.text();
    }

    /**
     * 获取特定CSS选择器的内容
     */
    public String fetchContentBySelector(String url, String cssSelector) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(cssSelector);
        return elements.text();
    }

    /**
     * 使用AI分析网页内容
     */
    public String analyzeContentWithAI(String content) {
        String analysisPrompt = """
                你是一个专业的电商运营AI助手，需要根据用户的问题深度分析网页内容，并输出结构化报告。
                """;
        PromptTemplate promptTemplate = new PromptTemplate(analysisPrompt);
        Map<String, Object> model = new HashMap<>();
        model.put("content", content);
        Prompt prompt = promptTemplate.create(model);
        ChatResponse chatResponse = chatClient
                .prompt(prompt)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 提取并结构化网页内容
     */
    public String extractStructuredData(String url) throws IOException {
        String extractionPrompt = """
                Extract structured data from the following web content.\s
                Identify key information such as titles, authors, dates, main content, etc.
                Present the data in JSON format.
                Content: {content}
               \s""";
        String content = fetchTextContent(url);
        PromptTemplate promptTemplate = new PromptTemplate(extractionPrompt);
        Map<String, Object> model = new HashMap<>();
        model.put("content", content);
        Prompt prompt = promptTemplate.create(model);
        ChatResponse chatResponse = chatClient.prompt(prompt).messages().call().chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }
}
