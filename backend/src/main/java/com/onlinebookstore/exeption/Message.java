package com.onlinebookstore.exeption;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class Message {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private List<String> errors;
    private String textMessage;
}
