package com.jarvlis.jarojcodesandbox;

import com.jarvlis.jarojcodesandbox.model.ExecuteCodeRequest;
import com.jarvlis.jarojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * Java 原生代码实现(直接复用模板方法)
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeRequest) {
        return super.executeCode(executeRequest);
    }
}
