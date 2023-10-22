package rmi.common;

import rmi.common.classes.User;

import java.rmi.RemoteException;

public interface Connection {
    void registerClient(User user, EventHandler client) throws RemoteException;
    void unregisterClient(User user) throws RemoteException;
}
