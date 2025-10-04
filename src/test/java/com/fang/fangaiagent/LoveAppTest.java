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
        message = "我想让另一半（小黄）更爱我";
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
        String message = "你好，我是小方, 我想让另一半(纳西妲)更爱我，我们结婚多年了，但我不知道该怎么做?";
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
    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        //testMessage("周末想带女朋友去上海约会，请使用百度搜索推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        //testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");
        testMessage("帮我向fang@jmu.edu.cn发送一份邮件，邮件的内容是：你好鸭，可以和你一起学习Java吗？");
        testMessage("请告诉我当前的时间，且格式必须是 yyyy-mm-dd 的形式");
        // 测试资源下载：图片下载
       // testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        //testMessage("请利用终端CMD执行ping www.baidu.com，并向我展示结果");

        // 测试文件操作：保存用户档案
        //testMessage("保存我的恋爱档案为文件");
//
//        // 测试 PDF 生成
        //testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
