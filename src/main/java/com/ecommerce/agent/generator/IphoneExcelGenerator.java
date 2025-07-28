package com.ecommerce.agent.generator;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IphoneExcelGenerator {

    public static void main(String[] args) {
        // 1. 生成100条iPhone数据
        List<ProductData> iphones = generateIphoneData(100);

        // 2. 写入Excel文件
        String fileName = "iphone_products_100.xlsx";
        EasyExcel.write(fileName, ProductData.class)
                .sheet("iPhone")
                .doWrite(iphones);

        System.out.println("成功生成100行Excel文件：" + fileName);
    }

    // 生成随机iPhone数据
    private static List<ProductData> generateIphoneData(int count) {
        List<ProductData> list = new ArrayList<>();
        Random random = new Random();

        String[] models = {"iPhone 15", "iPhone 15 Pro", "iPhone 14", "iPhone SE"};
        String[] storages = {"64GB", "128GB", "256GB", "512GB"};
        String[] colors = {"黑色", "白色", "金色", "蓝色"};
        String[] categories = {"旗舰机", "次旗舰", "入门款"};

        for (int i = 1; i <= count; i++) {
            String model = models[random.nextInt(models.length)];
            String storage = storages[random.nextInt(storages.length)];
            String color = colors[random.nextInt(colors.length)];
            String category = categories[random.nextInt(categories.length)];

            // 生成合理价格范围
            double baseCost = switch (model) {
                case "iPhone 15 Pro" -> 6000 + random.nextInt(3000);
                case "iPhone 15" -> 4000 + random.nextInt(2000);
                case "iPhone 14" -> 3000 + random.nextInt(1500);
                default -> 2000 + random.nextInt(1000); // SE
            };

            ProductData data = new ProductData(
                    String.format("%s %s %s", model, storage, color),
                    baseCost,
                    baseCost * (1.2 + random.nextDouble() * 0.3), // 竞品价格上浮20%~50%
                    category
            );
            list.add(data);
        }
        return list;
    }

    @Data
    public static class ProductData {
        @ExcelProperty("产品名称")
        private String productName;

        @ExcelProperty("自身成本价")
        private Double ourCost;

        @ExcelProperty("竞品价格")
        private Double competitorPrice;

        @ExcelProperty("产品分类")
        private String category;

        public ProductData() {}

        public ProductData(String name, Double cost, Double compPrice, String category) {
            this.productName = name;
            this.ourCost = cost;
            this.competitorPrice = compPrice;
            this.category = category;
        }
    }
}
