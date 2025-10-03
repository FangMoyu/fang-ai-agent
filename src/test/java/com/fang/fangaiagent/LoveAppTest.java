package com.fang.fangaiagent;

import cn.hutool.core.lang.UUID;
import com.fang.fangaiagent.app.LoveApp;
import com.fang.fangaiagent.entity.LoveReport;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是程序员小方";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        message = "我想让另一半（编程导航）更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小方, 我想让另一半(纳西妲)更爱我，但我不知道该怎么做";
        LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是小方, 我想让另一半(纳西妲)更爱我，我们结婚多年了，但我不知道该怎么做";
        String context = loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(context);
    }

    @Test
    void doChatWithRagCloud() {
        String chatId = UUID.randomUUID().toString();
        String message = "Hello, I'm Xiao Fang. I have been in love with and married to Nahida for a long time. How can I maintain our relationship?";
        String context = loveApp.doChatWithRagCloud(message, chatId);
        Assertions.assertNotNull(context);
    }

    @Test
    void doChatWithPartnerRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我是一个安静但内心丰富的人。\n" +
                "\n" +
                "白天在出版社校对文字，晚上画画、写日记、泡一杯花茶听老歌。\n" +
                "\n" +
                "不太喜欢喧闹的聚会，但很享受和一个人散步、看晚霞、聊心事的时光。\n" +
                "\n" +
                "我相信温柔的力量，也正在学习如何更好地表达爱与需求。\n" +
                "\n" +
                "我喜欢雨天，喜欢旧书店的气味，喜欢深夜的厨房灯光。\n" +
                "\n" +
                "理想的生活，是和一个能彼此倾听、共同成长的人，过简单而有温度的日子。请你为我找一个最适合的对象";
        String context = loveApp.doChatWithPartnerRag(message, chatId);
        Assertions.assertNotNull(context);
    }
}
