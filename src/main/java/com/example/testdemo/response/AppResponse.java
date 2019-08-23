package com.example.testdemo.response;

import lombok.Data;

@Data
public class AppResponse<T> {
    private Integer code;
    private T data;
    private String msg;

    public AppResponse(T data) {
        this.code = 200;
        this.msg = "ok";
        this.data = data;
    }
}
