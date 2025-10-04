package com.fang.fangaiagent;

import com.fang.fangaiagent.tools.QQEmailSenderTool;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;

@SpringBootTest
public class QQEmailSenderToolTest {

    @Test
    void sendTextEmail() throws MessagingException {
        try {
            QQEmailSenderTool.sendTextEmail(
                "fang@jmu.edu.cn",  // 替换为目标邮箱
                "测试邮件",
                "这是一封通过Java发送的测试邮件。"
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendHtmlEmail() {
        try {
            String html = "<h2>🎉 这是一封HTML测试邮件</h2>" +
                          "<p><b>加粗内容</b>，<a href='https://example.com'>点击链接</a></p>";
            QQEmailSenderTool.sendHtmlEmail("fang@jmu.edu.cn", "HTML测试", html);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
