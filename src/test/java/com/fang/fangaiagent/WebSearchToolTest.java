package com.fang.fangaiagent;

import com.fang.fangaiagent.tools.WebSearchTool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String apiKey;
    @Test
    public void testSearchWeb() {
        WebSearchTool tool = new WebSearchTool(apiKey);
        String query = "bilibili弹幕视频网";
        String result = tool.searchWeb(query);
        assertNotNull(result);
    }
}
