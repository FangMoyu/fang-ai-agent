package com.fang.fangaiagent;

import com.fang.fangaiagent.agent.FangManus;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FangManusTest {
  
    @Resource
    private FangManus fangManus;
  
    @Test
    void run() {  
        String userPrompt = """  
                我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，  
                并结合一些网络图片，制定一份详细的约会计划，  
                并以 PDF 格式输出""";
        String answer = fangManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }  
}
