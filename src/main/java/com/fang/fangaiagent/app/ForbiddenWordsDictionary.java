package com.fang.fangaiagent.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 恋爱问答助手APP - 违禁词库示例
 * 该类用于演示如何使用List<String>结构来定义和存储违禁词
 */
public class ForbiddenWordsDictionary {

    // 使用静态List来存储违禁词，使得该词库在全局可访问
    private static final List<String> FORBIDDEN_WORDS = new ArrayList<>();

    // 静态初始化块，在类加载时初始化违禁词列表
    static {
//         1. 违法有害类词汇
        List<String> illegalWords = Arrays.asList(
            "毒品", "冰毒", "海洛因", "大麻", "摇头丸",
            "枪支", "弹药", "炸药", "恐怖主义", "极端主义"
        );
        FORBIDDEN_WORDS.addAll(illegalWords);

        // 2. 色情低俗类词汇 (恋爱类APP需特别注意此类)
        List<String> pornographicWords = Arrays.asList(
            "约炮", "裸聊", "一夜情", "包养", "卖淫",
            "嫖娼", "色情", "AV", "成人电影", "性爱"
        );
        FORBIDDEN_WORDS.addAll(pornographicWords);

        // 3. 人身攻击与侮辱类词汇
        List<String> insultingWords = Arrays.asList(
            "傻逼", "笨蛋", "白痴", "去死", "你妈的",
            "屌丝", "垃圾", "废物", "脑残", "贱人"
        );
        FORBIDDEN_WORDS.addAll(insultingWords);

        // 4. 广告骚扰类词汇
        List<String> advertisingWords = Arrays.asList(
            "加微信", "加QQ", "联系电话", "投资理财", "赌博",
            "彩票", "赚钱", "兼职", "刷单", "微商"
        );
        FORBIDDEN_WORDS.addAll(advertisingWords);

        // 5. 其他敏感词汇
        List<String> otherSensitiveWords = Arrays.asList(
            "自杀", "自残", "杀人", "暴力", "仇恨"
        );
        FORBIDDEN_WORDS.addAll(otherSensitiveWords);

//         注意：在实际应用中，这个词库应该从配置文件或数据库加载，而不是硬编码在代码中
    }

    /**
     * 获取违禁词列表（返回副本以避免外部修改）
     * @return 违禁词列表的副本
     */
    public static List<String> getForbiddenWords() {
        // 返回一个新的ArrayList，避免外部代码修改原始词库
        return new ArrayList<>(FORBIDDEN_WORDS);
    }

    /**
     * 检查文本是否包含违禁词（简单示例）
     * @param text 待检查的文本
     * @return true表示包含违禁词，false表示不包含
     */
    public static boolean containsForbiddenWord(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        for (String word : FORBIDDEN_WORDS) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}