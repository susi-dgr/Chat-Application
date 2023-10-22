package rmi.common;

import rmi.common.classes.Chat;
import rmi.common.classes.Message;
import rmi.common.classes.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;

public interface DatabaseService extends Remote {
    User getUser(String username) throws RemoteException, SQLException;
    Chat getChatRoom(String chatName) throws RemoteException, SQLException;
    ArrayList<Chat> getChatsForUser(User user) throws RemoteException, SQLException;
    void addUser(User user) throws RemoteException, SQLException;
    void addChat(Chat chat) throws RemoteException, SQLException;
    void removeChat(Chat chat) throws RemoteException, SQLException;
    void addMessageToChat(Chat chat, Message message) throws RemoteException, SQLException;
    ArrayList<Message> getMessagesForChat(Chat chat) throws RemoteException, SQLException;
    ArrayList<Chat> getChats() throws RemoteException, SQLException;
    void banUserFromChat(Chat chat, User user) throws RemoteException, SQLException;
    void unbanUserFromChat(Chat chat, User user) throws RemoteException, SQLException;
    ArrayList<User> getUsers() throws RemoteException, SQLException;
    ArrayList<User> getUsersFromChat(Chat chat) throws RemoteException, SQLException;
    ArrayList<User> getBannedUsers(Chat chat) throws RemoteException, SQLException;
    void addUserToChat(Chat chat, User user) throws RemoteException, SQLException;
    void removeUserFromChat(Chat chat, User user) throws RemoteException, SQLException;
    int getNextChatId() throws RemoteException, SQLException;
    Message searchChatForMessage(Chat chat, String message) throws RemoteException, SQLException;
}

