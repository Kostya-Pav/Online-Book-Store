package com.onlinebookstore.exeption;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Message {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private List<String> errors;
    private String textMessage;

    public Message(LocalDateTime timestamp, HttpStatus status, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = errors;
    }

    public Message(LocalDateTime timestamp, HttpStatus status, String textMessage) {
        this.timestamp = timestamp;
        this.status = status;
        this.textMessage = textMessage;
    }
}
