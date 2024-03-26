package com.jarvlis.jarojcodesandbox.exception;
import com.jarvlis.jarojcodesandbox.constant.ErrorCode;
import com.jarvlis.jarojcodesandbox.constant.ResponseStatus;
import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author <a href="https://github.com/Jarvlis">Jarvlis</a>

 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    @Getter
    private final int status;

    private final String message;

    public BusinessException(ResponseStatus status, String message) {
        this.status = status.getValue();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
