package rmi.common.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoom extends Chat implements Serializable {
    private User owner;
    private String name;
    private ArrayList<User> bannedUsers;
    public ChatRoom(int id, String name, User owner) {
        super(id);
        super.addUser(owner);
        this.name = name;
        this.owner = owner;
        this.bannedUsers = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public User getOwner() { return this.owner; }

    public ArrayList<User> getBannedUsers() { return this.bannedUsers; }

    public void banUser(User user) {
        this.bannedUsers.add(user);
    }

    public void unbanUser(User user) {
        this.bannedUsers.remove(user);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof ChatRoom)) return false;
        return super.equals(obj);
    }
}
