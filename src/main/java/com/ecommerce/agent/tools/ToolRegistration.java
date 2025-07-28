package com.ecommerce.agent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ToolRegistration {
//    @Value("${search-api.api-key}")
//    private String searchApiKey;
    @Bean
    public ToolCallback[] allTools() {
//        FileOperationTool fileOperationTool = new FileOperationTool();
//        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
//        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
//        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        DateTimeTool dateTimeTool = new DateTimeTool();
        TerminateTool terminateTool = new TerminateTool();
        ExcelAnalysisTool excelAnalysisTool = new ExcelAnalysisTool();
        PricingAnalysisTool pricingAnalysisTool = new PricingAnalysisTool();
        ExchangeTool exchangeTool = new ExchangeTool();
        ReturnVoucherTool returnVoucherTool = new ReturnVoucherTool();
        return ToolCallbacks.from(
                resourceDownloadTool,
                dateTimeTool,
                terminateTool,
                excelAnalysisTool,
                pdfGenerationTool,
                exchangeTool,
                returnVoucherTool,
                pricingAnalysisTool
        );
    }
}