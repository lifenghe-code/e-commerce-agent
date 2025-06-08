package com.ecommerce.agent.tools;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ExcelAnalysisTool {

    @Tool(description = "Analyze Excel file and answer questions based on its content")
    public String analyzeExcel(
            @ToolParam(description = "Path to the Excel file to analyze") String filePath,
            @ToolParam(description = "Question to ask about the Excel data") String question) {

        try {
            // 1. 读取Excel文件并获取摘要信息
            ExcelSummary summary = readExcelData(filePath);

            // 2. 构建分析提示
            String analysisPrompt = buildAnalysisPrompt(summary, question);

            // 3. 返回分析结果结构
            return String.format("""
                    Excel Analysis Result:
                    - File Content: %s
                    - Sheets: %d
                   
                    - Question: %s
                    - Analysis Prompt: %s
                    """,
                    summary, summary.getSheetCount(),
                    question, analysisPrompt);

        } catch (Exception e) {
            log.error("Error analyzing Excel file: {}", filePath, e);
            return "Error analyzing Excel file: " + e.getMessage();
        }
    }

    private ExcelSummary readExcelData(String filePath) {
        ExcelSummary summary = new ExcelSummary();

        try (ExcelReader excelReader = EasyExcel.read(filePath).build()) {
            // 获取所有sheet
            // 获取所有Sheet信息
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();

            // 读取每个sheet的数据
            for (ReadSheet sheet : sheets) {

                SheetData sheetData = new SheetData();
                EasyExcel.read(filePath, new AnalysisEventListener<Map<Integer, String>>() {
                    @Override
                    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                        sheetData.setHeaders(new ArrayList<>(headMap.values()));
                    }

                    @Override
                    public void invoke(Map<Integer, String> data, AnalysisContext context) {
                        if (sheetData.getSampleRows().size() < 100) {
                            sheetData.getSampleRows().add(new ArrayList<>(data.values()));
                        }
                        sheetData.incrementRowCount();
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        summary.addSheetSample(sheet.getSheetName(), String.valueOf(sheetData));
                    }
                }).sheet(sheet.getSheetNo()).doRead();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return summary;
    }


    private String buildAnalysisPrompt(ExcelSummary summary, String question) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a professional data analyst. Please analyze the following Excel data and answer the question.\n\n");

        prompt.append("Excel File Summary:\n");
        prompt.append("- Total Sheets: ").append(summary.getSheetCount()).append("\n");
        prompt.append("- Total Rows: ").append(summary.getTotalRows()).append("\n\n");

        return prompt.toString();
    }

    // 内部类：Excel摘要信息
    private static class ExcelSummary {
        private final Map<String, String> sheetSamples = new LinkedHashMap<>();
        private final AtomicInteger totalRows = new AtomicInteger(0);

        public void addSheetSample(String sheetName, String sample) {
            sheetSamples.put(sheetName, sample);
        }

        public void incrementRowCount(int count) {
            totalRows.addAndGet(count);
        }

        public int getSheetCount() {
            return sheetSamples.size();
        }

        public int getTotalRows() {
            return totalRows.get();
        }

        public Map<String, String> getSheetSamples() {
            return Collections.unmodifiableMap(sheetSamples);
        }
    }

    // Sheet分析监听器
    private static class SheetAnalysisListener implements ReadListener<Object> {
        private static final int SAMPLE_SIZE = 5;
        private final ExcelSummary summary;
        private String currentSheetName;
        private final List<Map<Integer, String>> sampleRows = new ArrayList<>();

        public SheetAnalysisListener(ExcelSummary summary) {
            this.summary = summary;
        }

        @Override
        public void invoke(Object data, AnalysisContext context) {
            if (sampleRows.size() < SAMPLE_SIZE) {
                Map<Integer, String> rowData = new HashMap<>();
                if (data instanceof Map) {
                    ((Map<?, ?>) data).forEach((k, v) ->
                            rowData.put((Integer)k, v != null ? v.toString() : ""));
                }
                sampleRows.add(rowData);
            }
            summary.incrementRowCount(1);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            StringBuilder sampleBuilder = new StringBuilder();
            for (int i = 0; i < Math.min(sampleRows.size(), SAMPLE_SIZE); i++) {
                sampleBuilder.append("Row ").append(i + 1).append(": ")
                        .append(sampleRows.get(i)).append("\n");
            }
            summary.addSheetSample(currentSheetName, sampleBuilder.toString());
            sampleRows.clear();
        }
    }
    @Data
    private static class SheetData {
        private String sheetName;
        private List<String> headers = new ArrayList<>();
        private List<List<String>> sampleRows = new ArrayList<>();
        private int rowCount = 0;

        public void incrementRowCount() {
            rowCount++;
        }
    }
}