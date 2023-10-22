package swe4.rmi.client.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import swe4.rmi.client.ChatClient;
import swe4.rmi.common.classes.Chat;

public class SetMaxMessageCountView {
    private Chat chat;
    private final Stage primaryStage = new Stage();

    public SetMaxMessageCountView(Chat chat) {
        this.chat = chat;
    }

    public void show () {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 400);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        // Create input field for count of messages
        TextField messageCountField = new TextField();
        messageCountField.setPromptText("Amount of messages to display");
        TextButton setButton = new TextButton("button", "Set");

        setButton.setOnAction(e -> {
            // check if input is a number
            if (messageCountField.getText().matches("\\d*") &&
                ChatClient.getInstance().trySetMaxMessageCount(messageCountField.getText(), chat)) {
                // switch to ChatBPT here
                primaryStage.close();
            } else {
                // show error message
                ErrorPaneView errorPane = new ErrorPaneView("Please enter a valid number.");
                root.setBottom((errorPane.getErrorPane()));
            }
        });

        HBox maxMessageCountBar = new HBox(messageCountField, setButton);
        maxMessageCountBar.setSpacing(10);
        maxMessageCountBar.setAlignment(Pos.CENTER);

        VBox centerLayout = new VBox(maxMessageCountBar);
        centerLayout.setSpacing(10);
        centerLayout.setAlignment(Pos.CENTER);
        root.setCenter(centerLayout);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Set Max Message Count");
        primaryStage.show();
    }
}
