package com.fang.fangaiagent.gsonAdapter;

import com.google.gson.*;
import org.springframework.ai.chat.messages.*;

import java.lang.reflect.Type;

public class MessageTypeAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
    
    private static final String TYPE_FIELD = "messageType";
    
/**
 * 重写序列化方法，将Message对象序列化为JsonElement
 * @param message 要序列化的Message对象
 * @param type 序列化目标类型
 * @param context JSON序列化上下文
 * @return 包含类型信息的JsonElement对象
 */
    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
    // 使用上下文序列化Message对象，获取其JsonElement表示
        JsonElement jsonElement = context.serialize(message, message.getClass());
    // 在Json对象中添加类型字段，值为Message类的简单名称
        jsonElement.getAsJsonObject().addProperty(TYPE_FIELD, message.getClass().getSimpleName());
    // 返回添加了类型信息的JsonElement
        return jsonElement;
    }
    
    @Override
    public Message deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String messageType = jsonObject.get(TYPE_FIELD).getAsString();
        
        switch (messageType) {
            case "USER":
                return context.deserialize(json, UserMessage.class);
            case "SYSTEM":
                return context.deserialize(json, SystemMessage.class);
            case "ASSISTANT":
                return context.deserialize(json, AssistantMessage.class);
            case "TOOL":
                return context.deserialize(json, ToolResponseMessage.class);
            default:
                throw new JsonParseException("Unknown message type: " + messageType);
        }
    }
}