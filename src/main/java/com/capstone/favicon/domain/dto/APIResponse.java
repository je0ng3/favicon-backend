package com.capstone.favicon.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIResponse<T> {

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";

    private String status;
    private String message;
    private T data;

    public static <T> APIResponse<T> successAPI(String message, T data) {
        return new APIResponse<>(SUCCESS_STATUS, message, data);
    }

    public static <T> APIResponse<T> errorAPI(String message) {
        return new APIResponse<>(ERROR_STATUS, message, null);
    }

    private APIResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}