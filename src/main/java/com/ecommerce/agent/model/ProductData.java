package com.ecommerce.agent.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProductData {
    @ExcelProperty("产品名称") // 匹配表头
    private String productName;

    @ExcelProperty("自身成本价")
    private Double ourCost;

    @ExcelProperty("竞品价格")
    private Double competitorPrice;

    @ExcelProperty("产品分类") // 按列索引匹配
    private String category;
}
