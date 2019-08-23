package com.example.testdemo.controllerAdvice;

import com.example.testdemo.response.AppResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class AppControllerAdvice implements ResponseBodyAdvice {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({Exception.class})
    public AppResponse<Exception> returnNormal(Exception e) {
        AppResponse<Exception> objectAppResponse = new AppResponse<>(e);
        objectAppResponse.setCode(-1);
        objectAppResponse.setMsg(e.getMessage());
        return objectAppResponse;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        return new AppResponse<>(body);
    }
}
