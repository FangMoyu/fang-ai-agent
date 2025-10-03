package com.fang.fangaiagent.transapi;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 百度翻译API
 * 用于将 userText 转为中文
 */
public class TransApi {
    private static final String TRANS_API_HOST = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private final String appid;

    private final String securityKey;

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        String transResult = HttpGet.get(TRANS_API_HOST, params);
        if(StrUtil.isBlank(transResult)){
            return transResult;
        }
        Pattern pattern = Pattern.compile("\"dst\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(transResult);
        if (matcher.find()) {
            String dstValue = matcher.group(1);
            transResult = convertUnicode(dstValue);
        }
        return transResult;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }
    public static String convertUnicode(String str) {
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            char character = (char) Integer.parseInt(hexCode, 16);
            matcher.appendReplacement(sb, String.valueOf(character));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
