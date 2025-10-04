package com.fang.fangaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间操作工具
 */
public class TimeOperationTool {
    
    @Tool(description = "Get current date and time, optionally with custom format")
    public String getCurrentTime(
            @ToolParam(description = "Optional date time format pattern, e.g. yyyy-MM-dd, HH:mm:ss. If not provided, uses default format yyyy-MM-dd HH:mm:ss") 
            String format) {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            if (format != null && !format.trim().isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return now.format(formatter);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return now.format(formatter);
            }
        } catch (Exception e) {
            return "Error getting current time: " + e.getMessage();
        }
    }
}