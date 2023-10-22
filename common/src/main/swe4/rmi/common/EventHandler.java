package swe4.rmi.common;

import swe4.rmi.common.classes.Chat;
import swe4.rmi.common.classes.Message;
import swe4.rmi.common.classes.User;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface EventHandler extends Remote, Serializable {
    void handleNewChatAdded(Chat chat) throws RemoteException, SQLException;
    void handleMessageAdded(Chat chat) throws RemoteException, SQLException;
    void handleChatRemoved(Chat chat) throws RemoteException, SQLException;
    void handleUserAddedToChat(Chat chat, User user) throws RemoteException, SQLException;
    void handleUserBannedFromChat(Chat chat, User user) throws SQLException, RemoteException;
    void handleUserUnBannedFromChat(Chat chat, User user) throws SQLException, RemoteException;
}
