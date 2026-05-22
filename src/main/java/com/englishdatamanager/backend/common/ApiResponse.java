package com.englishdatamanager.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    /**
     * 构造成功响应。
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 构造成功响应。
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "success", null);
    }

    /**
     * 构造业务错误响应。
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 写入或构造未授权响应。
     */
    public static ApiResponse<Void> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }
}
