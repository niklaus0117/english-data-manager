package com.englishdatamanager.backend.exception;

public class BusinessException extends RuntimeException {

    /**
     * 初始化 BusinessException 实例。
     */
    public BusinessException(String message) {
        super(message);
    }
}
