package com.fang.fangaiagent;

import com.alibaba.dashscope.aigc.conversation.Conversation;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fang.fangaiagent.entity.conversation;
import com.fang.fangaiagent.mapper.conversationMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisStandalone {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            // ✅ 必须使用 MyBatis-Plus 的 builder！
            sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize SqlSessionFactory", e);
        }
    }

    public static void main(String[] args) {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            conversationMapper mapper = session.getMapper(conversationMapper.class);
            // 现在可以正常使用 selectById
            System.out.println("Mapper class: " + conversationMapper.class.getName());
            System.out.println("Mapper proxy created: " + mapper);
            conversation conversation = mapper.selectById(1);
            session.commit();
        }
    }
}