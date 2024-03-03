package com.jarvlis.jarojcodesandbox;


import com.jarvlis.jarojcodesandbox.model.ExecuteCodeRequest;
import com.jarvlis.jarojcodesandbox.model.ExecuteCodeResponse;

public interface CodeSandBox {
    /**
     * 执行代码
     * @param executeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeRequest);
}
