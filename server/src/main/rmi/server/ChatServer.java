package rmi.server;

import rmi.common.Database;
import rmi.common.Server;
import rmi.common.classes.Chat;
import rmi.common.classes.ChatRoom;
import rmi.common.classes.Message;
import rmi.common.classes.User;
import rmi.common.EventHandler;
import rmi.dal.ChatDatabase;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.net.MalformedURLException;

public class ChatServer implements Server {
    private final Clients clients = new Clients();
    private Database chatDao;

    public ChatServer() {
        try {
            chatDao = ChatDatabase.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String username) throws RemoteException, SQLException {
        return chatDao.getUser(username);
    }

    @Override
    public Chat getChatRoom(String chatName) throws RemoteException, SQLException {
        return chatDao.getChatRoom(chatName);
    }

    @Override
    public ArrayList<Chat> getChatsForUser(User user) throws SQLException, RemoteException {
        return chatDao.getChatsForUser(user);
    }

    @Override
    public void addUser(User user) throws SQLException, RemoteException {
        chatDao.addUser(user);
    }

    @Override
    public void addChat(Chat chat) throws SQLException, RemoteException {
        chatDao.addChat(chat);
        EventHandler client = clients.get(((ChatRoom) chat).getOwner());
        if (client != null) {
            client.handleNewChatAdded(chat);
        }
    }

    @Override
    public void removeChat(Chat chat) throws SQLException, RemoteException {
        chatDao.removeChat(chat);
        for (User user : chatDao.getUsersFromChat(chat)) {
            EventHandler client = clients.get(user);
            if (client != null) {
                client.handleChatRemoved(chat);
            }
        }
    }

    @Override
    public void addMessageToChat(Chat chat, Message message) throws RemoteException, SQLException {
        chatDao.addMessageToChat(chat, message);
        for (User user : chatDao.getUsersFromChat(chat)) {
            EventHandler client = clients.get(user);
            if (client != null) {
                client.handleMessageAdded(chat);
            }
        }
    }

    @Override
    public ArrayList<Message> getMessagesForChat(Chat chat) throws RemoteException, SQLException {
        return chatDao.getMessagesForChat(chat);
    }

    @Override
    public ArrayList<Chat> getChats() throws RemoteException, SQLException {
        return chatDao.getChats();
    }

    @Override
    public void banUserFromChat(Chat chat, User user) throws RemoteException, SQLException {
        chatDao.banUserFromChat(chat, user);
        for (User currUser : chatDao.getUsersFromChat(chat)) {
            EventHandler client = clients.get(currUser);
            if (client != null) {
                client.handleUserBannedFromChat(chat, user);
            }
        }
        EventHandler client = clients.get(user);
        client.handleUserBannedFromChat(chat, user);
    }

    @Override
    public void unbanUserFromChat(Chat chat, User user) throws RemoteException, SQLException {
        chatDao.unbanUserFromChat(chat, user);
        for (User currUser : chatDao.getUsersFromChat(chat)) {
            EventHandler client = clients.get(currUser);
            if (client != null) {
                client.handleUserUnBannedFromChat(chat, user);
            }
        }
        EventHandler client = clients.get(user);
        client.handleUserUnBannedFromChat(chat, user);
    }

    @Override
    public ArrayList<User> getUsers() throws RemoteException, SQLException {
        return chatDao.getUsers();
    }

    @Override
    public ArrayList<User> getUsersFromChat(Chat chat) throws RemoteException, SQLException {
        return chatDao.getUsersFromChat(chat);
    }

    @Override
    public ArrayList<User> getBannedUsers(Chat chat) throws RemoteException, SQLException {
        return chatDao.getBannedUsers(chat);
    }

    @Override
    public void addUserToChat(Chat chat, User user) throws RemoteException, SQLException {
        chatDao.addUserToChat(chat, user);
        for (User chatUser : chatDao.getUsersFromChat(chat)) {
            EventHandler client = clients.get(chatUser);
            if (client != null) {
                client.handleUserAddedToChat(chat, user);
            }
        }
    }

    @Override
    public void removeUserFromChat(Chat chat, User user) throws RemoteException, SQLException {
        chatDao.removeUserFromChat(chat, user);
    }

    @Override
    public int getNextChatId() throws RemoteException, SQLException {
        return chatDao.getNextChatId();
    }

    @Override
    public Message searchChatForMessage(Chat chat, String message) throws RemoteException, SQLException {
        return chatDao.searchChatForMessage(chat, message);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, SQLException {
        int registryPort      = Registry.REGISTRY_PORT;
        String serverHostName = "localhost";
        if (args.length > 0) {
            String[] hostAndPort = args[0].split(":");
            if (hostAndPort.length > 0) serverHostName = hostAndPort[0];
            if (hostAndPort.length > 1)
                registryPort = Integer.parseInt(hostAndPort[1]);
        }

        System.setProperty("java.rmi.server.hostname", serverHostName);

        String internalUrl = "rmi://localhost:" + registryPort + "/ChatServer";

        ChatServer chatServer = new ChatServer();
        Remote serviceStub = UnicastRemoteObject.exportObject(chatServer, registryPort);

        LocateRegistry.createRegistry(registryPort);
        Naming.rebind(internalUrl, serviceStub);

        System.out.println("ChatServer started at: " + internalUrl);
    }

    @Override
    public void registerClient(User user, EventHandler client) throws RemoteException {
        clients.registerClient(user, client);
    }

    @Override
    public void unregisterClient(User user) throws RemoteException {
        clients.unregisterClient(user);
    }
}
