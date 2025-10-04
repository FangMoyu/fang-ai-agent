package com.fang.fangaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TimeOperationToolTest {

    @Test
    void getCurrentTime() {
        TimeOperationTool timeOperationTool = new TimeOperationTool();
        String currentTime = timeOperationTool.getCurrentTime("yyyy-MM-dd HH:mm:ss");
        Assertions.assertNotNull(currentTime);
    }
}