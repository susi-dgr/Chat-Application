package swe4.rmi.server;

import swe4.rmi.common.Connection;
import swe4.rmi.common.EventHandler;
import swe4.rmi.common.classes.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Clients implements Connection {
    private final Map<User, EventHandler> clients = new ConcurrentHashMap<>();

    public EventHandler get(User user) {
        return clients.get(user);
    }

    public void registerClient(User user, EventHandler client) {
        clients.put(user, client);
    }

    public void unregisterClient(User user) {
        clients.remove(user);
    }
}
