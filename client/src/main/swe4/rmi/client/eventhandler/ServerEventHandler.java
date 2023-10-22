package swe4.rmi.client.eventhandler;

import swe4.rmi.client.ChatClient;
import swe4.rmi.common.EventHandler;
import swe4.rmi.common.Server;
import swe4.rmi.common.classes.Chat;
import swe4.rmi.common.classes.Message;
import swe4.rmi.common.classes.User;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class ServerEventHandler implements EventHandler {
    @Override
    public void handleNewChatAdded(Chat chat) throws RemoteException, SQLException {
        ChatClient.getInstance().handleNewChatAdded(chat);
    }

    @Override
    public void handleMessageAdded(Chat chat) throws RemoteException, SQLException {
        ChatClient.getInstance().handleMessageAdded(chat);
    }

    @Override
    public void handleChatRemoved(Chat chat) throws RemoteException, SQLException {
        ChatClient.getInstance().handleChatRemoved(chat);
    }

    @Override
    public void handleUserAddedToChat(Chat chat, User user) throws RemoteException, SQLException {
        ChatClient.getInstance().handleUserAddedToChat(chat, user);
    }

    @Override
    public void handleUserBannedFromChat(Chat chat, User user) throws SQLException, RemoteException {
        ChatClient.getInstance().handleUserBannedFromChat(chat, user);
    }

    @Override
    public void handleUserUnBannedFromChat(Chat chat, User user) throws SQLException, RemoteException {
        ChatClient.getInstance().handleUserUnBannedFromChat(chat, user);
    }
}
