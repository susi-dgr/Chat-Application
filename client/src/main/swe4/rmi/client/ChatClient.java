package swe4.rmi.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import swe4.rmi.client.view.StartView;
import swe4.rmi.client.eventhandler.ServerEventHandler;
import swe4.rmi.common.ConnectionService;
import swe4.rmi.common.DatabaseService;
import swe4.rmi.common.EventHandler;
import swe4.rmi.common.classes.Chat;
import swe4.rmi.common.classes.ChatRoom;
import swe4.rmi.common.classes.Message;
import swe4.rmi.common.classes.PrivateChat;
import swe4.rmi.common.classes.User;

import swe4.rmi.client.view.ChatView;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatClient extends Application {
    private static final ChatClient instance = new ChatClient();
    public static ChatClient getInstance() { return instance; }
    private User currentUser;
    private ArrayList<Chat> chatList;
    private ListView<Message> messageList = new ListView<>();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private ListView<String> systemMessageList = new ListView<>();
    private final ObservableList<String> systemMessages = FXCollections.observableArrayList();
    private DatabaseService database;
    private ConnectionService connection;
    private EventHandler eventHandler;
    private ChatView chatView;

    public void initialize() throws MalformedURLException, NotBoundException, RemoteException  {
        int registryPort = Registry.REGISTRY_PORT;

        String internalUrl = "rmi://localhost:" + registryPort + "/ChatServer";
        database = (DatabaseService) Naming.lookup(internalUrl);
        connection = (ConnectionService) Naming.lookup(internalUrl);

        chatView = ChatView.getInstance();
        eventHandler = new ServerEventHandler();
        UnicastRemoteObject.exportObject(eventHandler, 0);

        System.out.println("ChatClient started at: " + internalUrl);
    }

    @Override
    public void start(Stage primaryStage) throws MalformedURLException, NotBoundException, RemoteException  {
        StartView startPage = new StartView();
        startPage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                ChatClient.getInstance().close();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
    }
    public void close() throws RemoteException {
        connection.unregisterClient(currentUser);
        System.out.println("ChatClient closed");
    }

    public void setChatList(ArrayList<Chat> chatList) {
        this.chatList = chatList;
    }

    public void setMessageList(ListView<Message> messageList) {
        this.messageList = messageList;
    }

    public void setSystemMessageList(ListView<String> systemMessageList) {
        this.systemMessageList = systemMessageList;
    }

    public ArrayList<Chat> getChatList() throws SQLException, RemoteException {
        ArrayList<Chat> chats = database.getChatsForUser(currentUser);
        if (chats != null) {
            this.chatList = chats;
            return chatList;
        }
        return null;
    }

    public ListView<Message> getMessageList(Chat chat) throws SQLException, RemoteException {
        ArrayList<Message> messages = database.getMessagesForChat(chat);
        messageList = new ListView<>();
        this.messages.clear();
        for (Message message : messages) {
            addMessage(message);
        }
        return messageList;
    }

    public ObservableList<Message> getMessages() {
        return messages;
    }

    private void addMessage(Message message) {
        messages.add(message);
        if (messageList != null) {
            messageList.setItems(messages);
        }
    }

    public ListView<String> getSystemMessageList() {
        return systemMessageList;
    }

    public ObservableList<String> getSystemMessages() {
        return systemMessages;
    }

    public boolean tryLogIn(String username, String password) throws RemoteException, SQLException {
        User user = database.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            connection.registerClient(currentUser, eventHandler);
            return true;
        }
        return false;
    }

    public boolean trySignUp(String username, String fullName, String password) throws RemoteException, SQLException {
        User user = database.getUser(username);
        if (user == null) {
            user = new User(username, fullName, password);
            database.addUser(user);
            currentUser = user;
            connection.registerClient(currentUser, eventHandler);
            return true;
        }
        return false;
    }

    public boolean tryJoinChat(String chatName) throws RemoteException, SQLException {
        System.out.println("tryJoinChat");
        Chat chat = database.getChatRoom(chatName);
        if (chat != null) {
            if (database.getUsersFromChat(chat).contains(currentUser)) {
                return false;
            }
            if (chat instanceof ChatRoom) {
                if (database.getBannedUsers(chat).contains(currentUser)) {
                    return false;
                }
            }
            System.out.println("tryJoinChat: " + chatName);
            database.addUserToChat(chat, currentUser);
            return true;
        }
        return false;
    }

    public boolean tryStartPrivateChat(String username) throws RemoteException, SQLException {
        User user = database.getUser(username);
        if (user != null) {
            PrivateChat chat = new PrivateChat(database.getNextChatId(), currentUser, user);
            database.addChat(chat);
            database.addUserToChat(chat, currentUser);
            database.addUserToChat(chat, user);
            return true;
        }
        return false;
    }

    public ArrayList<User> getUsersFromChat(Chat chat) throws RemoteException, SQLException {
        return database.getUsersFromChat(chat);
    }

    public ArrayList<User> getBannedUsers(Chat chat) throws RemoteException, SQLException {
        return database.getBannedUsers(chat);
    }

    public void banUserFromChat(User user, Chat chat) throws RemoteException, SQLException {
        database.banUserFromChat(chat, user);
    }

    public void unbanUser(User user, Chat chat) throws RemoteException, SQLException {
        database.unbanUserFromChat(chat, user);
    }

    public boolean tryAddUser(String username, Chat chat) throws RemoteException, SQLException {
        User user = database.getUser(username);
        if (user != null && !chat.getUsers().contains(user) && !getBannedUsers(chat).contains(user)) {
            database.addUserToChat(chat, user);
            return true;
        }
        return false;
    }

    public boolean trySetMaxMessageCount(String maxMessageCount, Chat chat) {
        int maxMessageCountInteger = Integer.parseInt(maxMessageCount);
        if (maxMessageCountInteger >= 0) {
            chat.setMaxMessageCount(maxMessageCountInteger);
            return true;
        }
        return false;
    }

    public void createNewChatRoom(String chatName) throws RemoteException, SQLException {
        ChatRoom chat = new ChatRoom(database.getNextChatId(), chatName, currentUser);
        database.addChat(chat);
        database.addUserToChat(chat, currentUser);
    }

    public void removeChat(Chat chat) throws RemoteException, SQLException {
        database.removeChat(chat);
    }

    public Message searchChatForMessage(Chat chat, String message) throws SQLException, RemoteException {
        return database.searchChatForMessage(chat, message);
    }

    public ArrayList<Chat> getChatsForUser(User user) throws RemoteException, SQLException {
        return database.getChatsForUser(user);
    }

    public String getChatName(Chat chat) {
        if (chat instanceof PrivateChat) {
            return ((PrivateChat) chat).getOtherUser(currentUser);
        }
        return ((ChatRoom) chat).getName();
    }

    public ArrayList<Message> getMessagesForChat(Chat chat) throws RemoteException, SQLException {
        return database.getMessagesForChat(chat);
    }

    public void addMessageToChat(Chat chat, Message message) throws RemoteException, SQLException {
        database.addMessageToChat(chat, message);
    }

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) { currentUser = user; }

    public void handleNewChatAdded(Chat chat) throws RemoteException, SQLException {
        Platform.runLater(() -> systemMessages.add("New chat '" + getChatName(chat) + "' created"));
        chatView.refresh();
    }

    public void handleMessageAdded(Chat chat) throws RemoteException, SQLException {
        Platform.runLater(() -> systemMessages.add("New message in '" + getChatName(chat) + "'"));
        Platform.runLater(() -> {
            try {
                getMessageList(chat);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        chatView.refresh();
    }

    public void handleChatRemoved(Chat chat) throws RemoteException, SQLException {
        Platform.runLater(() -> systemMessages.add("Chat '" + getChatName(chat) + "' removed"));
        chatView.refresh();
    }

    public void handleUserAddedToChat(Chat chat, User user) throws RemoteException, SQLException {
        if (currentUser.equals(user)) {
            Platform.runLater(() -> systemMessages.add("You have been added to '" + getChatName(chat) + "'"));
        } else {
            Platform.runLater(() -> systemMessages.add("'" + user.getUsername() + "' added to chat '" + getChatName(chat) + "'"));
        }
        chatView.refresh();
    }

    public void handleUserBannedFromChat(Chat chat, User user) throws SQLException, RemoteException {
        if (currentUser.equals(user)) {
            Platform.runLater(() -> systemMessages.add("You have been banned from '" + getChatName(chat) + "'"));
        } else {
            Platform.runLater(() -> systemMessages.add("'" + user.getUsername() + "' banned from '" + getChatName(chat) + "'"));
        }
        chatView.refresh();
    }

    public void handleUserUnBannedFromChat(Chat chat, User user) throws SQLException, RemoteException {
        if (currentUser.equals(user)) {
            Platform.runLater(() -> systemMessages.add("You have been unbanned from '" + getChatName(chat) + "'"));
        } else {
            Platform.runLater(() -> systemMessages.add("'" + user.getUsername() + "' unbanned from '" + getChatName(chat) + "'"));
        }
        chatView.refresh();
    }
}
