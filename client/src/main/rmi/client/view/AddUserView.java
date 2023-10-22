package rmi.client.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rmi.client.ChatClient;
import rmi.common.classes.Chat;

import java.rmi.RemoteException;
import java.sql.SQLException;

public class AddUserView {
    private Chat chat;
    private ChatView chats;
    private final Stage primaryStage = new Stage();

    public AddUserView(ChatView chats, Chat chat) {
        this.chats = chats;
        this.chat = chat;

    }

    public void show () {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 400);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        // Create input field for username
        javafx.scene.control.TextField usernameField = new TextField();
        usernameField.setPromptText("Username to add");
        TextButton banButton = new TextButton("button", "Add");
        banButton.setOnAction(e -> {
            // check if user exists
            try {
                if (ChatClient.getInstance().tryAddUser(usernameField.getText(), chat)) {
                    primaryStage.close();
                } else {
                    // show error message
                    ErrorPaneView errorPane = new ErrorPaneView("User not found or already in chat.");
                    root.setBottom((errorPane.getErrorPane()));
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        HBox banUserBar = new HBox(usernameField, banButton);
        banUserBar.setSpacing(10);
        banUserBar.setAlignment(Pos.CENTER);

        VBox banUserBox = new VBox(banUserBar);
        banUserBox.setSpacing(10);
        banUserBox.setAlignment(Pos.CENTER);
        root.setCenter(banUserBox);

        primaryStage.setTitle("Add User");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
