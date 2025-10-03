package com.fang.fangaiagent.tools;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.fang.fangaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileOperationTool {
    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";


    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (IORuntimeException e) {
            return "Error reading file" + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully";
        } catch (IORuntimeException e) {
            return "Error writing file" + e.getMessage();
        }
    }
}
