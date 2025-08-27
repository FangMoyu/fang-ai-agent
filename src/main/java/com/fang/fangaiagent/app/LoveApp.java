package com.fang.fangaiagent.app;

import com.fang.fangaiagent.advisor.MyLoggerAdvisor;
import com.fang.fangaiagent.advisor.MySafeGuardAdvisor;
import com.fang.fangaiagent.chatmemory.DataBasedChatMemory;
import com.fang.fangaiagent.entity.LoveReport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

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
    public LoveApp(ChatModel dashscopeChatModel, DataBasedChatMemory dataBasedChatMemory) {
        // 使用ChatClient.builder()方法创建ChatClient对象，并设置默认的系统提示和默认的顾问
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(dataBasedChatMemory),
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

    /**
     * 基于文件保存信息的实现方案
     * @param message
     * @param chatId
     * @return
     */
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

    @Resource
    private VectorStore loveAppVectorStore;


    @Resource
    private Advisor loveAppRagCloudAdvisor;

    /**
     * 使用 RAG 进行问答
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        return ChatByRagCloud(message, chatId, loveAppRagCloudAdvisor);
    }

    @Resource
    private Advisor lovePartnerRagCloudAdvisor;

    private final String PARTNER_SYSTEM_PROMPT = "你必须恋爱对象列表中选出至少一位最适合用户情况的对象,不允许出现没有推荐任何人的情况";

    public String doChatWithPartnerRag(String message, String chatId) {
        message = message + PARTNER_SYSTEM_PROMPT;
        return ChatByRagCloud(message, chatId, lovePartnerRagCloudAdvisor);
    }

    private String ChatByRagCloud(String message, String chatId, Advisor... advisors) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置上下文记忆条数
                .advisors(new MyLoggerAdvisor())
                .advisors(advisors)
                .call()
                .chatResponse();
        String context = chatResponse.getResult().getOutput().getText();
        log.info("context: {}", context);
        return context;
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
