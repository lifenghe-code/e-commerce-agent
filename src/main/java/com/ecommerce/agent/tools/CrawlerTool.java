package com.ecommerce.agent.tools;

import com.ecommerce.agent.service.WebCrawlerService;
import jakarta.annotation.Resource;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CrawlerTool {
    @Resource
    WebCrawlerService webCrawlerService;

    @Tool(description = "Analyze the content on the webpage")
    String analyzeContentFromWebpage(@ToolParam(description = "URL of the webpage")String url) throws IOException {
        String content = webCrawlerService.fetchTextContent(url);
        return webCrawlerService.analyzeContentWithAI(content);
    }

    @Tool(description = "Extract structured data from the webpage")
    String extractContentFromWebpage(@ToolParam(description = "URL of the webpage")String url) throws IOException {
        return webCrawlerService.extractStructuredData(url);
    }
}
