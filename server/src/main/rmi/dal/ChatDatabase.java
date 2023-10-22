package rmi.dal;

import rmi.common.Database;
import rmi.common.classes.*;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;

public class ChatDatabase implements Database {
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost/ChatBPTDb?autoReconnect=true&useSSL=false";
    private static final String USER_NAME         = "root";
    private static final String PASSWORD          = null;
    private Connection connection;

    public ChatDatabase() throws SQLException {
        connection = DriverManager.getConnection(CONNECTION_STRING, USER_NAME, PASSWORD);
    }

    public static Database getInstance() throws SQLException {
        return new ChatDatabase();
    }

    @Override
    public User getUser(String username) throws SQLException {
        try (var stmt = connection.prepareStatement("select * from User where username = ?")) {
            stmt.setString(1, username);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("fullName"), rs.getString("username"), rs.getString("password"));
            }
        }
        return null;
    }

    @Override
    public Chat getChatRoom(String chatName) throws SQLException {
        try (var stmt = connection.prepareStatement("select * from ChatRoom where name = ?")) {
            stmt.setString(1, chatName);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new ChatRoom(rs.getInt("id"), rs.getString("name"), getUser(rs.getString("owner")));
            }
        }
        return null;
    }

    @Override
    public ArrayList<Chat> getChatsForUser(User user) throws SQLException {
        ArrayList<Chat> chats = new ArrayList<>();
        try (var stmt = connection.prepareStatement("select chatId from ChatUser where username = ?")) {
            stmt.setString(1, user.getUsername());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int chatId = rs.getInt("chatId");
                    try (var stmt2 = connection.prepareStatement("select * from ChatRoom where id = ?")) {
                        stmt2.setInt(1, chatId);
                        try (ResultSet rs2 = stmt2.executeQuery()) {
                            if (rs2.next()) {
                                chats.add(new ChatRoom(chatId, rs2.getString("name"), getUser(rs2.getString("owner"))));
                            }
                        }
                    }
                    try (var stmt2 = connection.prepareStatement("select * from PrivateChat where id = ?")) {
                        stmt2.setInt(1, chatId);
                        try (ResultSet rs2 = stmt2.executeQuery()) {
                            if (rs2.next()) {
                                chats.add(new PrivateChat(chatId, getUser(rs2.getString("user1")), getUser(rs2.getString("user2"))));
                            }
                        }
                    }
                }
                return chats;
            }
        }
    }

    @Override
    public void addUser(User user) throws SQLException {
        try (var stmt = connection.prepareStatement("insert into User (fullName, username, password) values (?, ?, ?)")) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
        }
    }

    @Override
    public void addChat(Chat chat) throws SQLException {
        try (var stmt = connection.prepareStatement("insert into Chat (id) values (?)")) {
            stmt.setInt(1, chat.getId());
            stmt.executeUpdate();
        }
        if (chat instanceof ChatRoom) {
            try (var stmt = connection.prepareStatement("insert into ChatRoom (id, name, owner) values (?, ?, ?)")) {
                stmt.setInt(1, chat.getId());
                ChatRoom chatRoom = (ChatRoom) chat;
                stmt.setString(2, chatRoom.getName());
                stmt.setString(3, chatRoom.getOwner().getUsername());
                stmt.executeUpdate();
            }
        } else {
            try (var stmt = connection.prepareStatement("insert into PrivateChat (id, user1, user2) values (?, ?, ?)")) {
                stmt.setInt(1, chat.getId());
                stmt.setString(2, chat.getUsers().get(0).getUsername());
                stmt.setString(3, chat.getUsers().get(1).getUsername());
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void removeChat(Chat chat) throws SQLException {
        try (var stmt = connection.prepareStatement("delete from ChatUser where chatId = ?")) {
            stmt.setInt(1, chat.getId());
            stmt.executeUpdate();
        }
        if (chat instanceof ChatRoom) {
            try (var stmt = connection.prepareStatement("delete from ChatRoom where id = ?")) {
                stmt.setInt(1, chat.getId());
                stmt.executeUpdate();
            }
        } else {
            try (var stmt = connection.prepareStatement("delete from PrivateChat where id = ?")) {
                stmt.setInt(1, chat.getId());
                stmt.executeUpdate();
            }
        }
        try (var stmt = connection.prepareStatement("delete from Message where chatId = ?")) {
            stmt.setInt(1, chat.getId());
            stmt.executeUpdate();
        }
        try (var stmt = connection.prepareStatement("delete from Chat where id = ?")) {
            stmt.setInt(1, chat.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void addMessageToChat(Chat chat, Message message) throws SQLException {
        try (var stmt = connection.prepareStatement("insert into Message (chatId, username, message, date) values (?, ?, ?, ?)")) {
            stmt.setInt(1, chat.getId());
            stmt.setString(2, message.getUsername());
            stmt.setString(3, message.getMessage());
            long timestamp = message.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            stmt.setTimestamp(4, new Timestamp(timestamp));
            stmt.executeUpdate();
        }
    }

    @Override
    public ArrayList<Message> getMessagesForChat(Chat chat) throws SQLException {
        try (var stmt = connection.prepareStatement("select * from Message where chatId = ?")) {
            stmt.setInt(1, chat.getId());
            var rs = stmt.executeQuery();
            var messages = new ArrayList<Message>();
            while (rs.next()) {
                messages.add(new Message(rs.getString("message"), rs.getString("username"), rs.getTimestamp("date").toLocalDateTime()));
            }
            return messages;
        }
    }

    @Override
    public ArrayList<Chat> getChats() throws SQLException {
        try (var stmt = connection.prepareStatement("select * from Chat")) {
            var rs = stmt.executeQuery();
            var chats = new ArrayList<Chat>();
            while (rs.next()) {
                chats.add(new Chat(rs.getInt("id")));
            }
            return chats;
        }
    }

    @Override
    public void banUserFromChat(Chat chat, User user) throws SQLException {
        try (var stmt = connection.prepareStatement("insert into BannedUser (username, chatId) values (?, ?)")) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, chat.getId());
            stmt.executeUpdate();
        }
        removeUserFromChat(chat, user);
    }

    @Override
    public void unbanUserFromChat(Chat chat, User user) throws SQLException {
        try (var stmt = connection.prepareStatement("delete from BannedUser where username = ? and chatId = ?")) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, chat.getId());
            stmt.executeUpdate();
        }
        addUserToChat(chat, user);
    }

    @Override
    public ArrayList<User> getUsers() throws SQLException {
        try (var stmt = connection.prepareStatement("select * from User")) {
            var rs = stmt.executeQuery();
            var users = new ArrayList<User>();
            while (rs.next()) {
                users.add(new User(rs.getString("fullName"), rs.getString("username"), rs.getString("password")));
            }
            return users;
        }
    }

    @Override
    public ArrayList<User> getUsersFromChat(Chat chat) throws SQLException {
        try (var stmt = connection.prepareStatement("select * from ChatUser where chatId = ?")) {
            stmt.setInt(1, chat.getId());
            var rs = stmt.executeQuery();
            try (var stmt2 = connection.prepareStatement("select * from User where username = ?")) {
                var users = new ArrayList<User>();
                while (rs.next()) {
                    stmt2.setString(1, rs.getString("username"));
                    var rs2 = stmt2.executeQuery();
                    if (rs2.next())
                        users.add(new User(rs2.getString("fullName"), rs2.getString("username"), rs2.getString("password")));
                }
                return users;
            }
        }
    }

    @Override
    public ArrayList<User> getBannedUsers(Chat chat) throws SQLException {
        try (var stmt = connection.prepareStatement("select * from BannedUser where chatId = ?")) {
            stmt.setInt(1, chat.getId());
            var rs = stmt.executeQuery();
            try (var stmt2 = connection.prepareStatement("select * from User where username = ?")) {
                var users = new ArrayList<User>();
                while (rs.next()) {
                    stmt2.setString(1, rs.getString("username"));
                    var rs2 = stmt2.executeQuery();
                    if (rs2.next())
                        users.add(new User(rs2.getString("fullName"), rs2.getString("username"), rs2.getString("password")));
                }
                return users;
            }
        }
    }

    @Override
    public void addUserToChat(Chat chat, User user) throws SQLException {
        try (var stmt = connection.prepareStatement("insert into ChatUser (username, chatId) values (?, ?)")) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, chat.getId());
            stmt.executeUpdate();
        }
    }

    public void removeUserFromChat(Chat chat, User user) throws SQLException {
        try (var stmt = connection.prepareStatement("delete from ChatUser where username = ? and chatId = ?")) {
            stmt.setString(1, user.getUsername());
            stmt.setInt(2, chat.getId());
            stmt.executeUpdate();
        }
        System.out.println("User " + user.getUsername() + " removed from chat " + chat.getId());
    }

    @Override
    public int getNextChatId() throws SQLException {
        try (var stmt = connection.prepareStatement("select max(id) from Chat")) {
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) + 1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public Message searchChatForMessage(Chat chat, String message) throws SQLException {
        try (var stmt = connection.prepareStatement("select * from Message where chatId = ? and message like ? order by date desc")) {
            stmt.setInt(1, chat.getId());
            stmt.setString(2, "%" + message + "%");
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new Message(rs.getString("message"), rs.getString("username"), rs.getTimestamp("date").toLocalDateTime());
            }
        }
        return null;
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) connection.close();
        connection = null;
    }
}