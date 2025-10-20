package com.fang.fangaiagent.controller;

import com.fang.fangaiagent.agent.FangManus;
import com.fang.fangaiagent.app.LoveApp;
import com.fang.fangaiagent.common.BaseResponse;
import com.fang.fangaiagent.common.ResultUtils;
import com.fang.fangaiagent.constant.UserConstant;
import com.fang.fangaiagent.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.xml.transform.Result;
import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashScopeChatModel;

    @Autowired
    private ChatModel chatModel;

    @Resource
    private UserService userService;

    @GetMapping("/love_app/chat/sync")
    public BaseResponse<String> doChatWithLoveAppSync(String message, String chatId, HttpServletRequest request) {
        userService.getLoginUser(request);
        String result = loveApp.doChat(message, chatId);
        return ResultUtils.success(result);
    }

    /**
     * sse 调用 返回 Flux 响应式对象
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId, HttpServletRequest request) {
        userService.getLoginUser(request);
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * 返回 Flux 对象
     * @param message
     * @param chatId
     * @return
     */
//    @GetMapping("/love_app/chat/sse")
//    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSE2(String message, String chatId) {
//        return loveApp.doChatByStream(message, chatId)
//                .map(chunk -> ServerSentEvent.<String>builder().data(chunk)
//                        .build());
//    }

    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId, HttpServletRequest request) {
        userService.getLoginUser(request);
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }

    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message, HttpServletRequest request) {
        userService.getLoginUser(request);
        FangManus fangManus = new FangManus(allTools, dashScopeChatModel);
        return fangManus.runStream(message);
    }

}
