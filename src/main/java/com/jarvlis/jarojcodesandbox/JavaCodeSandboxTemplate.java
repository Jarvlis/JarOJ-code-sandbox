package com.jarvlis.jarojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.jarvlis.jarojcodesandbox.model.ExecuteCodeRequest;
import com.jarvlis.jarojcodesandbox.model.ExecuteCodeResponse;
import com.jarvlis.jarojcodesandbox.model.ExecuteMessage;
import com.jarvlis.jarojcodesandbox.model.JudgeInfo;
import com.jarvlis.jarojcodesandbox.utils.ProcessUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class JavaCodeSandboxTemplate implements CodeSandBox {

    private static final String GLOBAL_CODE_DIR_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";
    private static final long TIME_OUT = 5000L;

    // 全局代码存放路径
    private static String globalCodePathName;

    // 用户代码文件夹路径
    private static String userCodeParentPath;
    // 用户代码路径
    private static String userCodePath;

    static {
        String userDir = System.getProperty("user.dir");
        globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
    }

    /**
     * 1.把用户代码保存为文件
     *
     * @param code 用户代码
     * @return
     */
    public File saveCodeToFile(String code) {
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        // 把用户的代码隔离存放
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 2.编译代码, 得到 class 文件
     *
     * @param userCodeFile 用户代码文件
     * @return
     */
    public ExecuteMessage compileCode(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMsg(process, "编译");
            if (executeMessage.getExitValue() != 0) {
                throw new RuntimeException("编译错误");
            }
            return executeMessage;
        } catch (Exception e) {
//            return getErrorResponse(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 3. 执行代码，得到输出结果
     *
     * @param userCodeFile 用户代码文件
     * @param inputLists   输入参数列表
     * @return
     */
    public List<ExecuteMessage> runCode(File userCodeFile, List<String> inputLists) {
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputLists) {
            String runCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
//                        System.out.println("超时了");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtil.runProcessAndGetMsg(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("程序执行异常:" + e);
            }
        }
        return executeMessageList;
    }

    /**
     * 4.收集整理输出结果
     *
     * @param executeMessageList
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        // 取用时最大值，便于判断是否超时
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotEmpty(executeMessage.getErrorMessage())) {
                executeCodeResponse.setMessage(errorMessage);
                // 执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }
        executeCodeResponse.setOutputLists(outputList);
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);

        executeCodeResponse.setJudgeInfo(judgeInfo);
        // 设置内存消耗信息
        judgeInfo.setMemory(0L);
        return executeCodeResponse;
    }

    public boolean clearFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeRequest) {

        List<String> inputLists = executeRequest.getInputLists();
        String code = executeRequest.getCode();
        String language = executeRequest.getLanguage();

        // 1.把用户代码保存为文件
        File userCodeFile = saveCodeToFile(code);

        // 2.编译代码, 得到class 文件
        ExecuteMessage executeMessage = compileCode(userCodeFile);
        System.out.println(executeMessage);

        // 3.执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = runCode(userCodeFile, inputLists);

        // 4. 收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);

        // 5. 文件清理
        boolean res = clearFile(userCodeFile);
        if (!res) {
            log.error("delete file error, userCodeFilePath: " + userCodeFile.getAbsolutePath());
        }

        return outputResponse;
    }

    /**
     * 6.获取错误响应
     *
     * @param e
     * @return
     */
    public ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputLists(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
