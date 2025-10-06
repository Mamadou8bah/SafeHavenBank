package com.mamadou.safehavenbank.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class APIResponse <T>{

    private boolean success;
    private String message;
    private T data;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public APIResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    private static <T> APIResponse<T> success(String message,T data) {
        return new APIResponse<>(true,message,data);
    }
    private static <T> APIResponse<T> success(String message) {
        return new APIResponse<>(true,message,null);
    }
    private static <T> APIResponse<T> error(String message,T data) {
        return new APIResponse<>(false,message,data);
    }
    private static <T> APIResponse<T> error(String message) {
        return new APIResponse<>(false,message,null);
    }
}
