package com.example.app01.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse<T> {
    private int code;
    private String message;
    private String httpStatus;
    private T result;
}
