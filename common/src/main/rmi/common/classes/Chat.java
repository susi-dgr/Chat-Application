package rmi.common.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    private int id;
    private ArrayList<User> users;
    private ArrayList<Message> messages;
    private int maxMessageCount = 50;

    public Chat(int id) {
        this.id = id;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public int getId() { return this.id; }

    public ArrayList<User> getUsers() { return this.users; }


    public void addUser(User user) {
        this.users.add(user);
    }

    public void setMaxMessageCount(int maxMessageCount) {
        this.maxMessageCount = maxMessageCount;
    }

    public int getMaxMessageCount() {
        return this.maxMessageCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Chat)) return false;
        Chat chat = (Chat) obj;
        return this.id == chat.id;
    }
}
