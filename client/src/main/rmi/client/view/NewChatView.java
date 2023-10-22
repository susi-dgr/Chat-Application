package rmi.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.client.ChatClient;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class NewChatView {
    private ChatView chats;
    private final Stage primaryStage = new Stage();

    public NewChatView(ChatView chats) {
        this.chats = chats;
    }

    void show() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 400);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        // input fields
        TextField newChatField = new TextField();
        newChatField.setPromptText("ChatRoom Name");
        TextButton newChatButton = new TextButton("button", "Create new ChatRoom");
        newChatButton.setOnAction(e -> {
            try {
                ChatClient.getInstance().createNewChatRoom(newChatField.getText());
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            primaryStage.close();
        });

        TextField joinChatField = new TextField();
        joinChatField.setPromptText("ChatRoom Name");
        TextButton joinChatButton = new TextButton("button", "Join existing ChatRoom");
        joinChatButton.setOnAction(e -> {
            try {
                if (ChatClient.getInstance().tryJoinChat(joinChatField.getText())) {
                    primaryStage.close();
                } else {
                    ErrorPaneView errorPane = new ErrorPaneView("ChatRoom either does not exist, you are already in it or banned from it.");
                    root.setBottom((errorPane.getErrorPane()));
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        TextField privateChatField = new TextField();
        privateChatField.setPromptText("Username");
        TextButton privateChatButton = new TextButton("button", "Start private Chat");
        privateChatButton.setOnAction(e -> {
            try {
                if (ChatClient.getInstance().tryStartPrivateChat(privateChatField.getText())) {
                    primaryStage.close();
                } else {
                    ErrorPaneView errorPane = new ErrorPaneView("User does not exist.");
                    root.setBottom((errorPane.getErrorPane()));
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox centerLayout = new VBox(new Label("Create New Chat"),
                                    newChatField, newChatButton, new Label("or"),
                                    joinChatField, joinChatButton, new Label("or"),
                                    privateChatField, privateChatButton);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setSpacing(10);
        root.setPadding(new Insets(20));
        root.setCenter(centerLayout);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Create New Chat");
        primaryStage.show();
    }
}
