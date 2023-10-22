package rmi.common;

import rmi.common.classes.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ConnectionService extends Remote {
    void registerClient(User user, EventHandler client) throws RemoteException;
    void unregisterClient(User user) throws RemoteException;
}
