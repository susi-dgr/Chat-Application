package rmi.common.classes;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String username;
    private String message;
    private LocalDateTime date;

    public Message(String message, String username, LocalDateTime date) {
        this.username = username;
        this.message = message;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() { return message; }
    public LocalDateTime getDate() { return date; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Message)) return false;
        Message message = (Message) obj;
        return this.username.equals(message.username) && this.message.equals(message.message) && this.date.equals(message.date);
    }
}
