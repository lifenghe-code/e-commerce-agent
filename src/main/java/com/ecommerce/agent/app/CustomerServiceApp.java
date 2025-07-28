package com.ecommerce.agent.app;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.ecommerce.agent.advisor.ChatSummaryAdvisor;
import com.ecommerce.agent.advisor.DocumentAdvancedRetrieverAdvisor;
import com.ecommerce.agent.advisor.MyLoggerAdvisor;
import com.ecommerce.agent.advisor.QueryTransformerAdvisor;
import com.ecommerce.agent.config.ConsoleColorConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class CustomerServiceApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一名专业的电商智能客服，致力于为用户提供高效、准确、友好的服务。请严格遵循以下规则进行对话：\n" +
            "快速响应：以简洁明了的语言，第一时间解答用户问题，避免冗长表述。\n" +
            "答案准确性：优先从企业官方政策、商品知识库、售后规则中提取信息，确保回答内容真实可靠；若知识库无对应答案，可结合行业通用规则与过往处理经验提供参考。\n" +
            "服务态度：使用亲切、礼貌的用语，避免生硬表述；适当使用表情符号增强亲和力（如\uD83D\uDE0A、\uD83D\uDCE6），但不过度使用。\n" +
            "多轮对话引导：若用户问题模糊（如 “商品有问题”），需通过提问明确细节（如 “请问商品具体遇到什么问题呢？”），逐步定位问题并解决。\n" +
            "转接人工条件：遇到以下情况立即转接人工客服，并向用户说明原因：\n" +
            "涉及复杂纠纷或个性化需求（如大额退款协商、定制服务）；\n" +
            "无法确定准确答案且无参考方案；\n" +
            "用户明确要求转接人工。\n" +
            "主动服务：回答问题后，根据场景主动提供相关信息（如退换货时告知物流地址、查询物流时推荐同类商品）。\n" +
            "请始终以解决用户问题、提升用户满意度为核心目标，保持专业且温暖的服务风格。";
    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    /**
     *
     * @param dashscopeChatModel
     * @param chatRedisMemory
     * @param pgVectorStore
     */
    public CustomerServiceApp(ChatModel dashscopeChatModel, ChatMemory chatRedisMemory, PgVectorStore pgVectorStore) {

//        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
//        ChatMemory chatRedisMemory = new FileBasedChatMemory(fileDir);
        // 初始化基于内存的对话记忆
        // QuestionAnswerAdvisor属于ChatClient的默认顾问（Advisors）之一。它借助pgVectorStore（这是一个向量数据库存储）来达成问答功能。下面详细剖析其作用：
        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatRedisMemory),
                        new QuestionAnswerAdvisor(pgVectorStore),
                        new ChatSummaryAdvisor()
                        // 自定义日志 Advisor，可按需开启
                        //new MyLoggerAdvisor()
//                        // 自定义推理增强 Advisor，可按需开启
//                       ,new ReReadingAdvisor()
                )

                .build();
    }

    /**
     * 基础对话功能
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithAdvancedRetriever(String message, String conversationId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId) //// 关联会话ID，保持对话上下文
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置检索历史消息的数量
                .advisors(new DocumentAdvancedRetrieverAdvisor().getAdvisor())
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info(ConsoleColorConfig.BLUE+"content: {}", content);
        return content;
    }

    /**
     * 结构化输出
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithConverter(String message, String conversationId) {
        record ActorsFilms(String product, List<String> properties, String summary){}
        ActorsFilms entity = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId) //// 关联会话ID，保持对话上下文
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置检索历史消息的数量
                .call()
                .entity(ActorsFilms.class);

        log.info(ConsoleColorConfig.BLUE+"content: {}", entity);
        return entity.toString();
    }


    // AI 工具调用
    @Resource
    private ToolCallback[] allTools;
    /**
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
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

    // AI 调用 MCP 服务
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    /**
     * AI 恋爱报告功能（调用 MCP 服务）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info(ConsoleColorConfig.BLUE+"content: {}", content);
        return content;
    }


    /**
     * 最终的完整对话功能
     * @param message
     * @param chatId
     * @return
     */
    public String doChatCompletly(String message, String chatId) {

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .tools(allTools)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) //// 关联会话ID，保持对话上下文
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置检索历史消息的数量
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info(ConsoleColorConfig.BLUE+"content: {}", content);
        return content;
    }

    public String doChatWithQueryTransformer(String message, String chatId) {

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .tools(allTools)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) //// 关联会话ID，保持对话上下文
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置检索历史消息的数量
                .advisors(new QueryTransformerAdvisor(dashScopeChatModel))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info(ConsoleColorConfig.BLUE+"content: {}", content);
        return content;
    }
}
