package com.fang.fangaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import com.fang.fangaiagent.factory.LoveAppContextualQueryAugmenterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashscopeApiKey;

    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        DashScopeDocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName("恋爱大师").build());
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                // 不允许空的输入，若为空，则会提供默认 userText
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }

    @Bean
    public Advisor lovePartnerRagCloudAdvisor() {
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(dashscopeApiKey).build();
        DashScopeDocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName("恋爱对象").build());
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever).build();
    }
}
