package rmi.client.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import rmi.client.ChatClient;
import rmi.common.classes.Chat;
import rmi.common.classes.ChatRoom;
import rmi.common.classes.Message;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChatView {
    private static final ChatView instance = new ChatView();
    private final Stage primaryStage = new Stage();
    private BorderPane rootPane;
    private Chat currChat;
    private Message scrollToMessage;

    public static ChatView getInstance() {
        return instance;
    }

    public void refresh() throws RemoteException, SQLException {
        // Create chat list UI
        Pane chatListPane = createChatListPane();

        // Create chat UI
        Pane chatPane = createChatPane();

        Pane createSystemMessagePane = createSystemMessagePane();

        // Create main UI
        rootPane = new BorderPane();
        rootPane.setLeft(chatListPane);
        rootPane.setCenter(chatPane);
        rootPane.setRight(createSystemMessagePane);
    }

    private ScrollPane createChatList() throws RemoteException, SQLException {
        ScrollPane chatScrollList = new ScrollPane();
        VBox chatBox = new VBox();
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10, 10, 10, 10));
        chatBox.setAlignment(Pos.TOP_CENTER);
        chatScrollList.setContent(chatBox);
        chatScrollList.setFitToWidth(true);
        chatScrollList.setFitToHeight(true);
        chatScrollList.setPrefHeight(1000);
        chatScrollList.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollList.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // chat buttons
        ArrayList<Chat> chatList = ChatClient.getInstance().getChatList();
        for (Chat chat : chatList) {
            Button chatButton = new Button(ChatClient.getInstance().getChatName(chat));
            chatButton.setId("button");
            chatButton.setPrefWidth(200);
            chatButton.setPrefHeight(50);
            chatButton.setOnAction(e -> {
                currChat = chat;
                Pane chatPane;
                try {
                    chatPane = createChatPane();
                } catch (RemoteException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
                rootPane.setCenter(chatPane);
            });
            chatBox.getChildren().add(chatButton);
        }
        return chatScrollList;
    }

    private BorderPane createMessageList() throws RemoteException, SQLException {
        BorderPane messageListPane = new BorderPane();
        ListView<Message> messageList = new ListView<>();
        if (currChat == null) {
            Label noChatLabel = new Label("No chat selected");
            noChatLabel.setAlignment(Pos.CENTER);
            messageListPane.setCenter(noChatLabel);
        } else if (currChat != null && ChatClient.getInstance().getMessageList(currChat) == null) {
            System.out.println(ChatClient.getInstance().getMessageList(currChat));
            Label noMessagesLabel = new Label("No messages found in this chat");
            noMessagesLabel.setAlignment(Pos.CENTER);
            messageListPane.setCenter(noMessagesLabel);
        } else {
            messageList = ChatClient.getInstance().getMessageList(currChat);
            messageList.scrollTo(messageList.getItems().size() - 1);

            ListView<Message> finalMessageList = messageList;
            messageList.setCellFactory(param -> new ListCell<Message>() {
                @Override
                protected void updateItem(Message message, boolean empty) {
                    super.updateItem(message, empty);
                    if (empty || message == null) {
                        setText(null);
                    } else {
                        HBox messageBox = new HBox();
                        messageBox.setSpacing(10);
                        messageBox.setPadding(new Insets(10));

                        VBox messageContent = new VBox();
                        messageContent.setSpacing(5);
                        messageContent.setPadding(new Insets(5, 10, 5, 10));

                        Label sender = new Label(message.getUsername());
                        sender.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

                        Label messageText = new Label(message.getMessage());
                        messageText.setStyle("-fx-font-size: 12px; -fx-wrap-text: true;");

                        Label timestamp = new Label(message.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        timestamp.setStyle("-fx-font-size: 12px;");

                        messageContent.getChildren().addAll(sender, messageText, timestamp);

                        if (message.getUsername().equals(ChatClient.getInstance().getCurrentUser().getUsername())) {
                            messageBox.setAlignment(Pos.CENTER_RIGHT);
                            messageContent.setAlignment(Pos.CENTER_RIGHT);
                            messageBox.setStyle("-fx-background-color: #90d8ef;");
                            messageText.setStyle("-fx-font-size: 12px; -fx-wrap-text: true; -fx-text-fill: black;");
                        } else {
                            messageBox.setAlignment(Pos.CENTER_LEFT);
                            messageContent.setAlignment(Pos.CENTER_LEFT);
                            messageBox.setStyle("-fx-background-color: #F3F3F3;");
                            messageText.setStyle("-fx-font-size: 12px; -fx-wrap-text: true; -fx-text-fill: black;");
                        }

                        messageBox.getChildren().add(messageContent);
                        setGraphic(messageBox);

                        messageBox.maxWidthProperty().bind(finalMessageList.widthProperty().subtract(20));
                    }
                }
            });
            messageListPane.setCenter(messageList);
        }
        messageListPane.setPrefHeight(1000);

        // Scroll to a specific message if provided
        if (scrollToMessage != null) {
            int index = messageList.getItems().indexOf(scrollToMessage);
            if (index >= 0) {
                messageList.scrollTo(index);
                messageList.getSelectionModel().select(index);
            }
        }

        return messageListPane;
    }

    private ListView<String> createSystemMessageList() {
        ListView<String> systemMessageList = new ListView<>();
        systemMessageList.setItems(ChatClient.getInstance().getSystemMessages());
        systemMessageList.setId("system-message-list");

        return systemMessageList;
    }


    private Pane createChatListPane() throws RemoteException, SQLException {
        // Create chatList UI
        VBox chatListPane = new VBox();
        chatListPane.setPadding(new Insets(10));
        chatListPane.setSpacing(10);
        chatListPane.setStyle("-fx-background-color: #F3F3F3;");

        Label titleLabel = new Label("ChatBPT");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // add chatroom button
        TextButton addChatButton = new TextButton("button", "+");
        addChatButton.setOnAction(e -> {
                    // switch to CreateNewChat here
                    NewChatView createNewChat = new NewChatView(this);
                    createNewChat.show();
                }
        );

        BorderPane chatListTitlePane = new BorderPane();
        chatListTitlePane.setLeft(new Label("Chats"));
        chatListTitlePane.setRight(addChatButton);
        chatListTitlePane.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox userPane = new HBox();
        userPane.setAlignment(Pos.CENTER_LEFT);
        userPane.setSpacing(10);

        Label usernameLabel = new Label(ChatClient.getInstance().getCurrentUser().toString());
        usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        userPane.getChildren().addAll(usernameLabel);

        ScrollPane chatListScrollPane = createChatList();

        chatListPane.getChildren().addAll(titleLabel, userPane, chatListTitlePane, chatListScrollPane);

        return chatListPane;
    }

    private Pane createChatPane() throws RemoteException, SQLException {
        // Create chat UI
        VBox chatPane = new VBox();
        chatPane.setPadding(new Insets(10, 10, 10, 10));
        chatPane.setSpacing(10);
        chatPane.setStyle("-fx-background-color: #F9F9F9;");

        BorderPane chatTitlePane = new BorderPane();
        if (currChat != null) {
            Label chatTitleLabel = new Label(ChatClient.getInstance().getChatName(currChat));
            chatTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            chatTitlePane.setLeft(chatTitleLabel);
        }
        chatTitlePane.setPadding(new Insets(10));
        chatTitlePane.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0;");

        // Create search bar
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search for message");
        searchBar.setAlignment(Pos.CENTER);
        searchBar.setOnAction(e -> {
            if (currChat == null)
                return;
            Message result = null;
            try {
                result = ChatClient.getInstance().searchChatForMessage(currChat, searchBar.getText());
            } catch (SQLException | RemoteException ex) {
                throw new RuntimeException(ex);
            }
            if (result != null) {
                scrollToMessage = result;
                try {
                    refresh();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        searchBar.setVisible(currChat != null);

        // Create drop down menu
        MenuButton menuButton = new MenuButton("Chat Settings");
        menuButton.setId("button");

        // Create menu items
        MenuItem addUser = new MenuItem("Add User");
        addUser.setOnAction(e -> {
                    // switch to AddUser here
                    AddUserView addUserWindow = new AddUserView(this, currChat);
                    addUserWindow.show();
                }
        );

        MenuItem removeChat = new MenuItem("Remove Chat");
        removeChat.setOnAction(e -> {
            // remove chat from ChatBPT
            try {
                ChatClient.getInstance().removeChat(currChat);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            currChat = null;
        });

        MenuItem banOrUnbanUser = new MenuItem("Ban or Unban User");
        banOrUnbanUser.setOnAction(e -> {
            // switch to BanUser here
            BanOrUnbanUserView banUserWindow = new BanOrUnbanUserView(this, currChat);
            try {
                banUserWindow.show();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // show only if user is owner of chat
        if (currChat != null
                && currChat instanceof ChatRoom
                && ChatClient.getInstance().getCurrentUser().getUsername().equals(((ChatRoom) currChat).getOwner().getUsername())) {
            addUser.setVisible(true);
            removeChat.setVisible(true);
            banOrUnbanUser.setVisible(true);
        } else {
            addUser.setVisible(false);
            removeChat.setVisible(false);
            banOrUnbanUser.setVisible(false);
        }

        MenuItem setMaxMessageCount = new MenuItem("Set Max Message Count");
        setMaxMessageCount.setOnAction(e -> {
                    if (currChat == null)
                        return;
                    // switch to SetMaxMessageCount here
                    SetMaxMessageCountView setMaxMessageCountWindow = new SetMaxMessageCountView(currChat);
                    setMaxMessageCountWindow.show();
                }
        );

        // show only if a chat is selected
        menuButton.setVisible(currChat != null);

        menuButton.getItems().addAll(addUser, removeChat, banOrUnbanUser, setMaxMessageCount);

        HBox.setHgrow(chatTitlePane, Priority.ALWAYS);

        HBox menuSearchPane = new HBox();
        menuSearchPane.setAlignment(Pos.CENTER_RIGHT);
        menuSearchPane.setSpacing(10);
        menuSearchPane.getChildren().addAll(searchBar, menuButton);

        chatTitlePane.setCenter(menuSearchPane);

        BorderPane messagePane = createMessageList();

        HBox messageInputPane = new HBox();
        messageInputPane.setPadding(new Insets(10));
        messageInputPane.setSpacing(10);
        messageInputPane.setStyle("-fx-background-color: white;");
        messageInputPane.setAlignment(Pos.BOTTOM_CENTER);

        TextField messageField = new TextField();
        messageField.setPromptText("Write a message");
        messageField.setPrefWidth(500);

        TextButton sendButton = new TextButton("button", "Send");
        sendButton.setOnAction(event -> {
            Message message = new Message(messageField.getText(), ChatClient.getInstance().getCurrentUser().getUsername(), LocalDateTime.now());
            try {
                ChatClient.getInstance().addMessageToChat(currChat, message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            messageField.clear();
        });

        messageInputPane.getChildren().addAll(messageField, sendButton);

        messageInputPane.setVisible(currChat != null);

        chatPane.getChildren().addAll(chatTitlePane, messagePane, messageInputPane);

        return chatPane;
    }

    public Pane createSystemMessagePane() throws RemoteException {
        // Create system message pane
        VBox createSystemMessagePane = new VBox();
        createSystemMessagePane.setPadding(new Insets(10));
        createSystemMessagePane.setSpacing(10);
        createSystemMessagePane.setStyle("-fx-background-color: #F3F3F3;");
        createSystemMessagePane.setPrefWidth(240);

        // Create system message list
        ChatClient.getInstance().setSystemMessageList(createSystemMessageList());
        createSystemMessagePane.getChildren().add(ChatClient.getInstance().getSystemMessageList());

        // Log Out
        HBox logOutPane = new HBox();
        logOutPane.setId("log-out-pane");
        logOutPane.setAlignment(Pos.BOTTOM_RIGHT);

        Button logOutButton = new Button("Log Out");
        logOutButton.setId("button");
        logOutButton.setOnAction(e -> {
            //get to the start page
            try {
                ChatClient.getInstance().close();
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            ChatClient.getInstance().setCurrentUser(null);
            ChatClient.getInstance().setSystemMessageList(null);
            try {
                ChatClient.getInstance().start(new Stage());
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            } catch (NotBoundException ex) {
                throw new RuntimeException(ex);
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
            primaryStage.close();
        });
        logOutPane.getChildren().add(logOutButton);
        logOutPane.setPadding(new Insets(10));

        BorderPane rightPane = new BorderPane();
        rightPane.setTop(createSystemMessagePane);
        rightPane.setBottom(logOutPane);

        return rightPane;
    }

    public void show() throws RemoteException, SQLException {
        // Create chat list UI
        Pane chatListPane = createChatListPane();

        // Create chat UI
        Pane chatPane = createChatPane();

        Pane createSystemMessagePane = createSystemMessagePane();

        // Create main UI
        rootPane = new BorderPane();
        rootPane.setLeft(chatListPane);
        rootPane.setCenter(chatPane);
        rootPane.setRight(createSystemMessagePane);

        // Create scene and show stage
        Scene scene = new Scene(rootPane, 1200, 600);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        primaryStage.setTitle("ChatBPT");
        primaryStage.setScene(scene);
        primaryStage.show();
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
}