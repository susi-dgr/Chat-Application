package rmi.common.classes;

import java.io.Serializable;

public class PrivateChat extends Chat implements Serializable {
    public PrivateChat(int id, User user1, User user2) {
        super(id);
        this.addUser(user1);
        this.addUser(user2);
    }

    public String getOtherUser(User user) {
        if (this.getUsers().get(0).getUsername().equals(user.getUsername())) {
            return this.getUsers().get(1).getUsername();
        }
        return this.getUsers().get(0).getUsername();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof PrivateChat)) return false;
        return super.equals(obj);
    }
}
