package com.fang.fangaiagent.chatmemory;
import com.fang.fangaiagent.entity.conversation;
import com.fang.fangaiagent.gsonAdapter.MessageTypeAdapter;
import com.fang.fangaiagent.mapper.conversationMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataBasedChatMemory implements ChatMemory {

    @Resource
    private conversationMapper conversationMapper;

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageTypeAdapter())
            .create();


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
        conversationMapper.updateById(oldConversation);
    }


    public conversation getOrCreateConversationData(String conversationId) {
        conversation conversation;
            // 1. 获取到数据库中的 messages JSON 字符串
            conversation = conversationMapper.selectById(conversationId);
            if (conversation == null) {
                conversation = new conversation();
                conversation.setConversationId(conversationId);
                List<Message> messages = new ArrayList<>();
                conversation.setMessage(gson.toJson(messages));
                conversationMapper.insert(conversation);
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
            // 1. 获取到数据库中的 messages JSON 字符串
            conversationMapper.deleteById(conversationId);
    }
}
