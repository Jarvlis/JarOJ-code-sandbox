package com.jarvlis.jarojcodesandbox.utils;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.jarvlis.jarojcodesandbox.model.ExecuteMessage;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtil {
    /**
     * 执行进程并获取信息
     * @param runProcess 要执行的进程
     * @param opName 操作名称
     * @return 执行结果
     * @throws InterruptedException
     * @throws IOException
     */
    public static ExecuteMessage runProcessAndGetMsg(Process runProcess, String opName) throws InterruptedException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 等待程序读取，获取错误码
        int exitValue = runProcess.waitFor();
        ExecuteMessage executeMessage = new ExecuteMessage();
        executeMessage.setExitValue(exitValue);
        // 正常退出
        if (exitValue == 0) {
            System.out.println(opName + "成功");
            // 分批获取程序正常输出
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String compileOutputLine;
            List<String> outputStrList = new ArrayList<>();
            // 逐行读取
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                outputStrList.add(compileOutputLine);
            }
            executeMessage.setMessage(StringUtils.join(outputStrList, "\n"));

        } else {
            System.out.println(opName + "失败, 错误码：" + exitValue);
            // 分批获取程序正常输出
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            String compileOutputLine;
            List<String> outputStrList = new ArrayList<>();
            // 逐行读取
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                outputStrList.add(compileOutputLine);
            }
            executeMessage.setMessage(StringUtils.join(outputStrList, "\n"));
            // 分批获取程序错误输出
            BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
            String errorcompileOutputLine;

            List<String> outputErrStrList = new ArrayList<>();
            // 逐行读取
            while ((errorcompileOutputLine = errorBufferedReader.readLine()) != null) {
                outputErrStrList.add(errorcompileOutputLine);
            }
            executeMessage.setErrorMessage(StringUtils.join(outputErrStrList, "\n"));

        }
        stopWatch.stop();
        executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        return executeMessage;
    }

    public static ExecuteMessage runinteractProcessAndGetMsg(Process runProcess, String opName, String args) throws InterruptedException, IOException {
        ExecuteMessage executeMessage = new ExecuteMessage();

        // 向控制台输入程序
        OutputStream outputStream = runProcess.getOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        String[] arg = args.split(" ");
        outputStreamWriter.write(StrUtil.join("\n", arg) + "\n");
        // 执行输入的发送
        outputStreamWriter.flush();

        InputStream inputStream = runProcess.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String compileOutputLine;
        StringBuilder compileOutputBuilder = new StringBuilder();
        // 逐行读取
        while ((compileOutputLine = bufferedReader.readLine()) != null) {
            compileOutputBuilder.append(compileOutputLine);
        }
        executeMessage.setMessage(compileOutputBuilder.toString());

        // 资源的回收
        outputStreamWriter.close();
        outputStream.close();
        inputStream.close();
        runProcess.destroy();
        return executeMessage;
    }
}
