package rmi.client.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.client.ChatClient;
import rmi.common.classes.Chat;
import rmi.common.classes.User;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class BanOrUnbanUserView {
    private ChatView chats;
    private final Chat chat;
    private final Stage primaryStage = new Stage();

    public BanOrUnbanUserView(ChatView chats, Chat chat) {
        this.chats = chats;
        this.chat = chat;
    }

    public void show() throws RemoteException, SQLException {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 400);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        Label userLabel = new Label("Users in Chat");
        userLabel.setId("userTable");

        ListView<User> userList = new ListView<>();
        userList.setId("userList");
        userList.getItems().addAll(ChatClient.getInstance().getUsersFromChat(chat));

        Label bannedUserLabel = new Label("Banned Users");
        bannedUserLabel.setId("bannedUserTable");

        ListView<User> bannedUserList = new ListView<>();
        bannedUserList.setId("bannedUserList");
        bannedUserList.getItems().addAll(ChatClient.getInstance().getBannedUsers(chat));

        TextButton banButton = new TextButton("button", "Ban");
        banButton.setDisable(true);
        VBox banButtonBox = new VBox();
        banButtonBox.getChildren().add(banButton);
        banButtonBox.setAlignment(Pos.CENTER);

        userList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (banButtonBox.getChildren().size() > 1)
                banButtonBox.getChildren().remove(1);
            root.setBottom(new HBox());
            User user = newValue;
            if (user.getUsername().equals(ChatClient.getInstance().getCurrentUser().getUsername())) {
                banButton.setDisable(true);
                ErrorPaneView errorPane = new ErrorPaneView("You cannot ban yourself.");
                banButtonBox.getChildren().add(errorPane.getErrorPane());
            } else {
                banButton.setDisable(false);
            }
        });

        banButton.setOnAction(e -> {
            User user = userList.getSelectionModel().getSelectedItem();
            if (user != null) {
                try {
                    ChatClient.getInstance().banUserFromChat(user, chat);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                userList.getItems().remove(user);
                bannedUserList.getItems().add(user);
            }
        });

        TextButton unbanButton = new TextButton("button", "Unban");
        unbanButton.setDisable(true);

        bannedUserList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            unbanButton.setDisable(false);
            root.setBottom(new HBox());
        });

        unbanButton.setOnAction(e -> {
            User user = bannedUserList.getSelectionModel().getSelectedItem();
            if (user != null) {
                try {
                    ChatClient.getInstance().unbanUser(user, chat);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                bannedUserList.getItems().remove(user);
                userList.getItems().add(user);
            }
        });

        VBox centerLayout = new VBox(userLabel, userList, banButtonBox, bannedUserLabel, bannedUserList, unbanButton);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setSpacing(10);

        root.setCenter(centerLayout);

        primaryStage.setTitle("Ban or Unban User");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
