package com.jarvlis.jarojcodesandbox.model;

/**
 * Author:Jarvlis
 * Date:2023-10-03
 * Time:11:32
 */

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间(KB)
     */
    private Long time;
}
