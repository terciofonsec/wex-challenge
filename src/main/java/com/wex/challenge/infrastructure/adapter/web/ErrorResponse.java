package com.wex.challenge.infrastructure.adapter.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Detail detail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Detail {
        private String field;
        private String message;
    }
}
