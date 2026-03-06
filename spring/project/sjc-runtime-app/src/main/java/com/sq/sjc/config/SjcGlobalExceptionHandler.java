package com.sq.sjc.config;

import com.sq.system.common.exception.BizException;
import com.sq.system.common.result.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.sq.sjc")
public class SjcGlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseResult<?> handleBiz(BizException e) {
        return ResponseResult.of(e.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult<?> handleAny(Exception e) {
        return ResponseResult.fail(e.getMessage());
    }
}
