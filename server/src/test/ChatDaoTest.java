
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import swe4.rmi.common.Database;
import swe4.rmi.common.classes.*;
import swe4.rmi.dal.ChatDatabase;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChatDaoTest {
    Database chatDao;
    User max = new User("Max Mustermann", "max", "max");
    User ela = new User("Ela Mustermann", "ela", "ela");
    ChatRoom chat1 = new ChatRoom(1, "Chat1", max);
    PrivateChat chat2 = new PrivateChat(2, max, ela);

    private void runScript(String scriptPath) throws IOException {
        // Build the PowerShell command
        String[] command = {"powershell.exe", "-ExecutionPolicy", "Bypass", "-File", scriptPath};

        // Create the process builder
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // Start the process
        Process process = processBuilder.start();

        // Wait for the script to complete
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            System.out.println("PowerShell script execution interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    @BeforeEach
    void initialize() throws SQLException, IOException {
        chatDao = new ChatDatabase();
        runScript("..\\scripts\\create-chatbpt-schema.ps1");
        runScript("..\\scripts\\insert-chatbpt-schema.ps1");
    }

    @AfterEach
    void tearDown() throws Exception {
        chatDao.close();
    }

    @Test
    void getUser_ReturnsNull_whenUserDoesNotExist() throws IOException, SQLException {
        assertEquals(null, chatDao.getUser("noUser"));
    }

    @Test
    void getUser_ReturnsCorrectUser() throws IOException, SQLException {
        User userFromDao = chatDao.getUser(max.getUsername());
        assertEquals(max, userFromDao);
    }

    @Test
    void getChatRoom_ReturnsCorrectChat() throws RemoteException, SQLException {
        Chat chatFromDao = chatDao.getChatRoom(chat1.getName());
        assertEquals(chat1, chatFromDao);
    }

    @Test
    void getChatsForUser_ReturnsCorrectChats() throws SQLException, RemoteException {
        assertEquals(2, chatDao.getChatsForUser(max).size());
    }

    @Test
    void addUser_AddsUserToDatabase() throws SQLException, RemoteException {
        User user = new User("Test User", "test", "test");
        chatDao.addUser(user);
        assertEquals(user, chatDao.getUser(user.getUsername()));
    }

    @Test
    void addChat_AddsChatToDatabase_ChatRoom() throws SQLException, RemoteException {
        ChatRoom chat = new ChatRoom(5, "Test Chat", max);
        chatDao.addChat(chat);
        ArrayList<Chat> chats = chatDao.getChats();
        assertEquals(chat.getId(), chats.get(chats.size()-1).getId());
    }

    @Test
    void addChat_AddsChatToDatabase_PrivateChat() throws SQLException, RemoteException {
        Chat chat = new PrivateChat(5, max, ela);
        chatDao.addChat(chat);
        ArrayList<Chat> chats = chatDao.getChats();
        assertEquals(chat.getId(), chats.get(chats.size()-1).getId());
    }

    @Test
    void removeChat_RemovesChatFromDatabase_ChatRoom() throws SQLException, RemoteException {
        chatDao.removeChat(chat1);
        assertEquals(null, chatDao.getChatRoom(chat1.getName()));
    }

    @Test
    void removeChat_RemovesChatFromDatabase_PrivateChat() throws SQLException, RemoteException {
        chatDao.removeChat(chat2);
        ArrayList<Chat> chats = chatDao.getChats();
        Chat foundChat = null;
        for (Chat chat : chats) {
            if (chat.getId() == chat2.getId()) {
                foundChat = chat;
            }
        }
        assertEquals(null, foundChat);
    }

    @Test
    void addMessage_AddsMessageToDatabase() throws SQLException, RemoteException {
        Message message = new Message("Test Message", "max", LocalDateTime.now());
        chatDao.addMessageToChat(chat1, message);
        ArrayList<Message> messages = chatDao.getMessagesForChat(chat1);
        assertEquals("Test Message", messages.get(messages.size()-1).getMessage());
    }

    @Test
    void getMessagesForChat_ReturnsCorrectMessages() throws SQLException, RemoteException {
        assertEquals(5, chatDao.getMessagesForChat(chat1).size());
    }

    @Test
    void getChats_ReturnsCorrectChats() throws SQLException, RemoteException {
        assertEquals(2, chatDao.getChats().size());
    }

    @Test
    void banUserFromChat_BansUserFromChat() throws SQLException, RemoteException {
        chatDao.banUserFromChat(chat1, ela);
        assertEquals(1, chatDao.getBannedUsers(chat1).size());
    }

    @Test
    void unbanUserFromChat_UnbansUserFromChat() throws SQLException, RemoteException {
        chatDao.banUserFromChat(chat1, ela);
        chatDao.unbanUserFromChat(chat1, ela);
        assertEquals(0, chatDao.getBannedUsers(chat1).size());
    }

    @Test
    void getUsers_ReturnsCorrectUsers() throws SQLException, RemoteException {
        assertEquals(2, chatDao.getUsers().size());
    }

    @Test
    void getUsersFromChat_ReturnsCorrectUsers() throws SQLException, RemoteException {
        assertEquals(1, chatDao.getUsersFromChat(chat1).size());
    }

    @Test
    void getBannedUsers_ReturnsCorrectUsers() throws SQLException, RemoteException {
        assertEquals(0, chatDao.getBannedUsers(chat1).size());
    }

    @Test
    void addUserToChat_AddsUserToChat() throws SQLException, RemoteException {
        chatDao.addUserToChat(chat1, ela);
        assertEquals(2, chatDao.getUsersFromChat(chat1).size());
    }

    @Test
    void removeUserFromChat_RemovesUserFromChat() throws SQLException, RemoteException {
        chatDao.removeUserFromChat(chat1, max);
        assertEquals(0, chatDao.getUsersFromChat(chat1).size());
    }

    @Test
    void getNextChatId_ReturnsCorrectId() throws SQLException, RemoteException {
        assertEquals(3, chatDao.getNextChatId());
    }

    @Test
    void searchChatForMessage_ReturnsNull_whenMessageDoesNotExist() throws SQLException, RemoteException {
        assertEquals(null, chatDao.searchChatForMessage(chat1, "Test Message"));
    }

    @Test
    void searchChatForMessage_ReturnsCorrectMessage() throws SQLException, RemoteException {
        Message message = new Message("Test Message", "max", LocalDateTime.now());
        chatDao.addMessageToChat(chat1, message);
        assertEquals(message.getMessage(), chatDao.searchChatForMessage(chat1, "Test Message").getMessage());
    }
}
