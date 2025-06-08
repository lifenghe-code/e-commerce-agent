package com.ecommerce.agent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class EManusTest {

    @Resource
    private EManus eManus;

    @Test
    void run() {
        String userPrompt = """  
            河北省定向选调公务员，省委、石家庄市、保定市、雄安新区，哪个地区容易上岸？将结果保存为pdf
            """;
        String fileName = "H:\\Java\\Project\\e-commerce-agent\\src\\main\\resources\\河北省定向选调.xlsx";
        String message = "现在我想你提供文件的路径："+ fileName +"，请帮我回答：我是工学硕士，报考哪个岗位比较合适？";
        String answer = eManus.run(message);
        log.info(answer);
        Assertions.assertNotNull(answer);
    }
}