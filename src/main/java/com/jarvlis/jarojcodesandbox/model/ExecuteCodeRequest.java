package com.jarvlis.jarojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Author:Jarvlis
 * Date:2024-01-11
 * Time:14:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    List<String> inputLists;

    private String code;

    private String language;
}
