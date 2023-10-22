package rmi.common.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private String fullName;
    private String password;
    private ArrayList<String> systemMessages;

    public User(String fullName, String username, String password) {
        this.username = username;
        this.fullName = fullName;
        this.password = password;
        this.systemMessages = new ArrayList<>();
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public ArrayList<String> getSystemMessages() {
        return this.systemMessages;
    }

    public void addSystemMessage(String message) {
        this.systemMessages.add(message);
    }

    @Override
    public String toString() {
        return this.fullName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof User)) return false;
        User user = (User) obj;
        return this.username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }
}
