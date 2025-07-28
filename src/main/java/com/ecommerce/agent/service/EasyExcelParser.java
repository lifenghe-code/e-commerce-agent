package com.ecommerce.agent.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.ecommerce.agent.model.ProductData;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

import static java.lang.System.in;

// EasyExcelParser.java

public class  EasyExcelParser {
    
    public static List<ProductData> parse(String filePath) {
            // 方式1：同步读取（适合小文件）
        return EasyExcel.read(filePath)
                    .head(ProductData.class)
                    .sheet()
                    .doReadSync();
    }
}
