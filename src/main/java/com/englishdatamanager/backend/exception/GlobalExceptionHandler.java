package com.englishdatamanager.backend.exception;

import com.englishdatamanager.backend.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常并返回统一响应。
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.error(exception.getMessage());
    }

    /**
     * 处理参数校验异常并返回首个错误信息。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError() == null
                ? "request validation failed"
                : exception.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.error(message);
    }

    /**
     * 处理未捕获异常并输出统一错误响应。
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception, HttpServletRequest request) {
        log.error("request failed: {}", request.getRequestURI(), exception);
        return ApiResponse.error("request failed: " + request.getRequestURI());
    }
}
