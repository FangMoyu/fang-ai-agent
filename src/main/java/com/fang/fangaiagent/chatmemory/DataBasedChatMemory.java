package com.fang.fangaiagent.chatmemory;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.dashscope.aigc.conversation.Conversation;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.fang.fangaiagent.entity.conversation;
import com.fang.fangaiagent.gsonAdapter.MessageTypeAdapter;
import com.fang.fangaiagent.mapper.conversationMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DataBasedChatMemory implements ChatMemory {
    private static SqlSessionFactory sqlSessionFactory;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageTypeAdapter())
            .create();
    static {
        try {
            // 加载 mybatis-config.xml
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            // 使用 MyBatis-Plus 的 builder（支持 MP 的插件和自动填充等功能）
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize SqlSessionFactory", e);
        }
    }

    public DataBasedChatMemory() {

    }

    @Override
    public void add(String conversationId, Message message) {
        ChatMemory.super.add(conversationId, message);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        // 1. 获取到数据库中的 messages JSON 字符串
        conversation oldConversation = getOrCreateConversationData(conversationId);
        String oldMessagesJson = oldConversation.getMessage();
        // 2. 将JSON 字符串转为 List<Message> 对象
        Type type = new TypeToken<List<Message>>(){}.getType();
        List<Message> oldMessages = gson.fromJson(oldMessagesJson,type);
        // 3. 添加新的 messages 到 List<Message> 对象中
        oldMessages.addAll(messages);
        // 4. 将 List<Message> 对象转为 JSON 字符串
        String newMessagesJsonStr = gson.toJson(oldMessages);
        oldConversation.setMessage(newMessagesJsonStr);

        // 5. 将 JSON 字符串更新到数据库中
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 获取 Mapper 代理对象
            conversationMapper conversationMapper = session.getMapper(conversationMapper.class);
            conversationMapper.updateById(oldConversation);
            session.commit();
        }
    }


    public conversation getOrCreateConversationData(String conversationId) {
        conversation conversation;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 获取 Mapper 代理对象
            conversationMapper conversationMapper = session.getMapper(conversationMapper.class);
            // 1. 获取到数据库中的 messages JSON 字符串
            conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                conversation = new conversation();
                conversation.setConversationId(conversationId);
                List<Message> messages = new ArrayList<>();
                conversation.setMessage(gson.toJson(messages));
                conversationMapper.insert(conversation);
            }
            session.commit();
        }
        return conversation;
    }
    @Override
    public List<Message> get(String conversationId, int lastN) {
        conversation conversation = getOrCreateConversationData(conversationId);
        String messagesJson = conversation.getMessage();
        List<Message> messages = gson.fromJson(messagesJson, new TypeToken<List<Message>>(){}.getType());
        // 检查边界条件，如果 lastN 超出或小于消息总大小，直接返回整个消息列表
        if(lastN < 0) {
            return messages;
        } else if(lastN > messages.size()) {
            return messages;
        }
        // 返回倒数 lastN 项
        return messages.subList(messages.size() - lastN, messages.size());
    }

    @Override
    public void clear(String conversationId) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 获取 Mapper 代理对象
            conversationMapper conversationMapper = session.getMapper(conversationMapper.class);
            // 1. 获取到数据库中的 messages JSON 字符串
            conversationMapper.deleteById(conversationId);
            session.commit();
        }
    }
}
