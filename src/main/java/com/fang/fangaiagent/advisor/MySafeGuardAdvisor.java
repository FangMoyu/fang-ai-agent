package com.fang.fangaiagent.advisor;

import java.util.List;

import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * A {@link CallAroundAdvisor} and {@link StreamAroundAdvisor} that filters out the
 * response if the user input contains any of the sensitive words.
 * If the user input contains any of the sensitive words, the response is replaced with a failure response.
 */
public class MySafeGuardAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private final static String DEFAULT_FAILURE_RESPONSE = "抱歉，您的提问中包含了敏感词汇，请您对您的内容进行修改后再尝试发送";

    private final static int DEFAULT_ORDER = 0;

    private final String failureResponse;

    private final List<String> sensitiveWords;

    private final int order;

    public MySafeGuardAdvisor(List<String> sensitiveWords) {
        this(sensitiveWords, DEFAULT_FAILURE_RESPONSE, DEFAULT_ORDER);
    }

    public MySafeGuardAdvisor(List<String> sensitiveWords, String failureResponse, int order) {
        Assert.notNull(sensitiveWords, "敏感词不能为空");
        Assert.notNull(failureResponse, "失败响应结果不能为空");
        this.sensitiveWords = sensitiveWords;
        this.failureResponse = failureResponse;
        this.order = order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

/**
 * 检查输入文本是否包含敏感词
 * @param text 需要检查的文本内容
 * @return 如果文本为空或包含敏感词则返回true，否则返回false
 */
    private boolean check(String text) {
    // 检查文本是否为空，如果为空直接返回false
        if(text.isEmpty()) {
            return false;
        }

    // 检查敏感词集合是否为空，并且使用stream流检查文本是否包含任何敏感词
        return !CollectionUtils.isEmpty(this.sensitiveWords)
                && this.sensitiveWords.stream().anyMatch(text::contains);
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        if (check(advisedRequest.userText())) {
            return createFailureResponse(advisedRequest);
        }
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        if (advisedResponse.response() == null || check(advisedResponse.response().getResult().getOutput().getText())) {
            return createFailureResponse(advisedRequest);
        }
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        if (check(advisedRequest.userText())) {
            return Flux.just(createFailureResponse(advisedRequest));
        }
        return chain.nextAroundStream(advisedRequest);
    }

    private AdvisedResponse createFailureResponse(AdvisedRequest advisedRequest) {
        return new AdvisedResponse(ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage(this.failureResponse))))
                .build(), advisedRequest.adviseContext());
    }


    @Override
    public int getOrder() {
        return this.order;
    }

    public static final class Builder {

        private List<String> sensitiveWords;

        private String failureResponse = DEFAULT_FAILURE_RESPONSE;

        private int order = DEFAULT_ORDER;

        private Builder() {
        }

        public Builder sensitiveWords(List<String> sensitiveWords) {
            this.sensitiveWords = sensitiveWords;
            return this;
        }

        public Builder failureResponse(String failureResponse) {
            this.failureResponse = failureResponse;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        /**
         * @deprecated use {@link #sensitiveWords(List)} instead.
         */
        @Deprecated(forRemoval = true, since = "1.0.0-M5")
        public Builder withSensitiveWords(List<String> sensitiveWords) {
            this.sensitiveWords = sensitiveWords;
            return this;
        }

        /**
         * @deprecated use {@link #failureResponse(String)} instead.
         */
        @Deprecated(forRemoval = true, since = "1.0.0-M5")
        public Builder withFailureResponse(String failureResponse) {
            this.failureResponse = failureResponse;
            return this;
        }

        /**
         * @deprecated use {@link #order(int)} instead.
         */
        @Deprecated(forRemoval = true, since = "1.0.0-M5")
        public Builder withOrder(int order) {
            this.order = order;
            return this;
        }

        public SafeGuardAdvisor build() {
            return new SafeGuardAdvisor(this.sensitiveWords, this.failureResponse, this.order);
        }

    }

}
