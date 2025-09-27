package com.fang.fangaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class FangAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(FangAiAgentApplication.class, args);
    }

}
