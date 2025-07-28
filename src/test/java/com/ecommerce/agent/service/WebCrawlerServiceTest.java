package com.ecommerce.agent.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class WebCrawlerServiceTest {
    @Resource
    private WebCrawlerService webCrawlerService;

    @Test
    void analyzeContentWithAI() throws IOException {
        String url = "https://detail.tmall.com/item.htm?fpChannel=101&fpChannelSig=fbeb7fdc7c17d1f4ebadfa44787479566d329812&id=839248262640&ns=1&pisk=gzmZYHYV_hKZTeNTSDZVTaR82NZTooRSSmNbnxD0C5VgCjZqubFCcdE6kol4TjF_5lV10mPnEKI6BFU0gfEgkVNb1SrqHbJBN3tSBAEYjQOWV1MNyXqRS-jcGWq33oyGFTqHJAEYmIWCmUtrBf3-wr7gnJvUh-_gIjbGK9VLhNVmi54ht8y8moc0Swb338f0iSjgxpyTHO20ii4h--y5mSc0mpk39-q0Ijqmgu40zMyYSKCCbrq581aTZAVFmgW8ePfoBZslqAeg7mcQTJwIYRzaZrD_2CoiOxmsXyLNSoHIzjumZB__s4yrTzgwaGlr6-D4nf-RUWmZ3DztR1bgUlParc4y0LDTo2oUfcAAyvMUszrIRed_lllZy7UMJBhqL5GmbyfwOSgSdc4EZBsEMrumfkmMt3Sy-seHsanx7sb4SJeUNpJUPRjmgo-5JZbAkP1LL79MIZQYSJeUNpJFkZU6JJPWIdf..&priceTId=2150470217505833078258797e1213&spm=a21n57.sem.item.50.252d3903hk5YQS&u_channel=bybtqdyh&umpChannel=bybtqdyh&utparam=%7B%22aplus_abtest%22%3A%224bf30d015f3f2c7aa704502f453c4fc4%22%7D&xxc=ad_ztc&sku_properties=20105%3A41420%3B20122%3A11835346";

        String s = webCrawlerService.fetchTextContent(url);
        log.info(s);
    }

    @Test
    void extractStructuredData() {
    }
}