package com.fang.fangaiagent;

import com.fang.fangaiagent.tools.TerminalOperationTool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TerminalOperationToolTest {

    @Test
    void executeCommand() {
        TerminalOperationTool tool = new TerminalOperationTool();
        // 执行Windows命令提示符命令
        String result1 = tool.executeCommand("dir");
        String result2 = tool.executeCommand("ipconfig");
// 执行带超时的命令
        String result3 = tool.executeCommandWithTimeout("systeminfo", 60);
        Assertions.assertNotNull(result1);
        Assertions.assertNotNull(result2);
        Assertions.assertNotNull(result3);
    }

    @Test
    void executePowerShell() {
        TerminalOperationTool tool = new TerminalOperationTool();
        // 执行PowerShell命令
        String result3 = tool.executePowerShell("Get-Process");
        String result4 = tool.executePowerShell("Get-Service | Where-Object {$_.Status -eq 'Running'}");
        Assertions.assertNotNull(result3);
        Assertions.assertNotNull(result4);
    }
}