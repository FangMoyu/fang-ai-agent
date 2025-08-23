package com.fang.fangaiagent.app;

import com.fang.fangaiagent.advisor.MyLoggerAdvisor;
import com.fang.fangaiagent.chatmemory.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MySafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。\" +\n" +
            "            \"围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；\" +\n" +
            "            \"恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。\" +\n" +
            "            \"引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";
// 构造函数，传入ChatModel参数
//使用 Spring 的构造器注入方式来注入阿里大模型 dashscopeChatModel 对象
    public LoveApp(ChatModel dashscopeChatModel) {
        // 创建ChatMemory对象
        ChatMemory chatMemory = new InMemoryChatMemory();
        // 使用ChatClient.builder()方法创建ChatClient对象，并设置默认的系统提示和默认的顾问
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MySafeGuardAdvisor(ForbiddenWordsDictionary.getForbiddenWords())
                )
                .build();
    }

//    public LoveApp(ChatModel dashscopeChatModel) {
//        // 初始化基于文件的对话记忆
//        String fileDir = System.getProperty("user.dir") + "/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        new MessageChatMemoryAdvisor(chatMemory),
//                        new SafeGuardAdvisor(ForbiddenWordsDictionary.getForbiddenWords())
//                )
//                .build();
//    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                // 设置对话的 id，只有相同 id 的情况下AI才会记忆上下文
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置上下文记忆条数
                .call()
                .chatResponse();
        String content;
        if (response != null) {
           content = response.getResult().getOutput().getText();
        }else {
            throw new RuntimeException("调用AI出现错误");
        }
        log.info("content: {}", content);
        return content;
    }

    /**
     * record类，用于封装AI返回的标题和推荐内容
     * @param title
     * @param suggestions
     * record 类会自动生成构造器，equals(), hashCode(), toString() 方法,以及各个属性的 getter 方法。
     * record 类的字段默认加上了 final 关键字，不可变
     */
    public record LoveReport(String title, List<String> suggestions) {

    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置上下文记忆条数
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }
}
