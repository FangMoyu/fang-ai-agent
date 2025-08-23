package com.fang.fangaiagent.gsonAdapter;

import com.google.gson.*;
import org.springframework.ai.chat.messages.*;

import java.lang.reflect.Type;

public class MessageTypeAdapter implements JsonSerializer<Message>, JsonDeserializer<Message> {
    
    private static final String TYPE_FIELD = "messageType";
    
    @Override
    public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
        JsonElement jsonElement = context.serialize(message, message.getClass());
        jsonElement.getAsJsonObject().addProperty(TYPE_FIELD, message.getClass().getSimpleName());
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