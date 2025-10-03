package com.fang.fangaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 暂不支持文档过滤,仅展示功能实现
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {
/**
 * 创建一个自定义的LoveAppRagAdvisor，用于基于检索增强的生成建议
 *
 * @param vectorStore 向量存储，用于文档检索
 * @param status 状态值，用于过滤文档
 * @return 返回一个配置好的Advisor实例，可用于基于特定状态的文档检索和建议生成
 */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
    // 构建过滤条件表达式，只选择状态匹配的文档
        Filter.Expression expression = new FilterExpressionBuilder().eq("status", status)
                .build();
    // 创建文档检索器，配置向量存储、过滤条件、相似度阈值和返回文档数量
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression) // 过滤条件：只检索状态匹配的文档
                .similarityThreshold(0.5) // 相似度阈值：0.5，只返回相似度大于0.5的文档
                .topK(3) // 返回文档数量：最多返回3个最相似的文档
                .build();
    // 构建并返回检索增强型Advisor，使用配置好的文档检索器
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}
