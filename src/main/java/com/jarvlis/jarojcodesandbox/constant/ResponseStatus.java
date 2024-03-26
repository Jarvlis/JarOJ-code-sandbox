package com.jarvlis.jarojcodesandbox.constant;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    SUCCESS(2, "SUCCESS"),
    ERROR(3, "ERROR"),
    TIMEOUT(4, "TIMEOUT"),
    EXECUTE_ERROR(5, "EXECUTE_ERROR"),
    COMPILE_ERROR(6, "COMPILE_ERROR");

    private final int value;

    /**
     * 信息
     */
    private final String message;


    ResponseStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }
}
