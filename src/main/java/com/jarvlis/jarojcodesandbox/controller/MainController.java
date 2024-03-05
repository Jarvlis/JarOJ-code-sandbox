package com.jarvlis.jarojcodesandbox.controller;

import com.jarvlis.jarojcodesandbox.JavaNativeCodeSandbox;
import com.jarvlis.jarojcodesandbox.model.ExecuteCodeRequest;
import com.jarvlis.jarojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/")
public class MainController {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_KEY = "secretKey";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    /**
     * 执行代码
     * @param executeRequest
     * @return
     */
    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeRequest, HttpServletRequest request
    , HttpServletResponse response) {
        if (executeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        if (!request.getHeader(AUTH_REQUEST_HEADER).equals(AUTH_REQUEST_KEY)) {
            response.setStatus(403);
            return null;
        }
        return javaNativeCodeSandbox.executeCode(executeRequest);
    }
}
