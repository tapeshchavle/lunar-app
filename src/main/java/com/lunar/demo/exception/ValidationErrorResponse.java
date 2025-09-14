package com.lunar.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    
    private Map<String, String> validationErrors;
    
    public ValidationErrorResponse(LocalDateTime timestamp, int status, String error, 
                                 String message, String path, Map<String, String> validationErrors) {
        super(timestamp, status, error, message, path);
        this.validationErrors = validationErrors;
    }
    
    public static ValidationErrorResponseBuilder builder() {
        return new ValidationErrorResponseBuilder();
    }
    
    public static class ValidationErrorResponseBuilder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;
        
        public ValidationErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public ValidationErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }
        
        public ValidationErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }
        
        public ValidationErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }
        
        public ValidationErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }
        
        public ValidationErrorResponseBuilder validationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }
        
        public ValidationErrorResponse build() {
            return new ValidationErrorResponse(timestamp, status, error, message, path, validationErrors);
        }
    }
}
