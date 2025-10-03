package com.fang.fangaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * 终端操作工具（Windows版本）
 */
public class TerminalOperationTool {
    
    @Tool(description = "Execute Windows command prompt command and get output")
    public String executeCommand(@ToolParam(description = "Command to execute in Windows command prompt") String command) {
        return executeCommandWithTimeout(command, 30);
    }
    
    @Tool(description = "Execute Windows command prompt command with custom timeout")
    public String executeCommandWithTimeout(
            @ToolParam(description = "Command to execute") String command,
            @ToolParam(description = "Timeout in seconds") int timeoutSeconds) {
        
        StringBuilder output = new StringBuilder();
        Process process = null;
        
        try {
            // 在Windows上使用cmd.exe执行命令
            process = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", command});
            
            // 读取标准输出
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "GBK")
            );
            
            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), "GBK")
            );
            
            // 等待进程完成或超时
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                return "Command execution timed out after " + timeoutSeconds + " seconds";
            }
            
            // 读取标准输出
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // 读取错误输出
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            
            // 如果有错误输出，添加到结果中
            if (errorOutput.length() > 0) {
                output.append("Error output:\n").append(errorOutput);
            }
            
            int exitCode = process.exitValue();
            output.append("\nExit code: ").append(exitCode);
            
            return output.toString();
            
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    
    @Tool(description = "Execute PowerShell command and get output")
    public String executePowerShell(@ToolParam(description = "PowerShell command to execute") String command) {
        return executePowerShellWithTimeout(command, 60);
    }
    
    @Tool(description = "Execute PowerShell command with custom timeout")
    public String executePowerShellWithTimeout(
            @ToolParam(description = "PowerShell command to execute") String command,
            @ToolParam(description = "Timeout in seconds") int timeoutSeconds) {
        
        StringBuilder output = new StringBuilder();
        Process process = null;
        
        try {
            // 在Windows上使用PowerShell执行命令
            process = Runtime.getRuntime().exec(new String[]{"powershell.exe", "-Command", command});
            
            // 读取标准输出
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "GBK")
            );
            
            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), "GBK")
            );
            
            // 等待进程完成或超时
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            
            if (!finished) {
                process.destroyForcibly();
                return "PowerShell command execution timed out after " + timeoutSeconds + " seconds";
            }
            
            // 读取标准输出
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // 读取错误输出
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            
            // 如果有错误输出，添加到结果中
            if (errorOutput.length() > 0) {
                output.append("Error output:\n").append(errorOutput);
            }
            
            int exitCode = process.exitValue();
            output.append("\nExit code: ").append(exitCode);
            
            return output.toString();
            
        } catch (Exception e) {
            return "Error executing PowerShell command: " + e.getMessage();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}