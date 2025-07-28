package com.ecommerce.agent.tools;

import cn.hutool.extra.tokenizer.engine.analysis.AnalysisResult;
import com.ecommerce.agent.model.ProductData;
import com.ecommerce.agent.service.EasyExcelParser;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class PricingAnalysisTool  {

    @Tool(description = "Obtain data on competitors and own products in Excel")
    public List<ProductData> execute(
            @ToolParam(description = "Path to the Excel file to analyze") String filePath
    ) {
        try {
            return EasyExcelParser.parse(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Excel分析失败", e);
        }
    }

}
