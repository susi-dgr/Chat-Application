package swe4.rmi.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import swe4.rmi.client.ChatClient;
import swe4.rmi.common.classes.User;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class SignUpView {
    private final Stage primaryStage = new Stage();

    void show() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 400);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setId("sign-up-label");

        // Create input fields for full name, username, and password
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("New Username");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        PasswordField newRepeatPasswordField = new PasswordField();
        newRepeatPasswordField.setPromptText("Repeat Password");

        // Create a sign up submit button
        TextButton signUpSubmitButton = new TextButton("button", "Sign Up");
        signUpSubmitButton.setOnAction(e -> {
            // check if user exists
            try {
                ChatClient.getInstance().initialize();
                if ((ChatClient.getInstance().trySignUp(newUsernameField.getText(), fullNameField.getText(), newPasswordField.getText()))
                        && (newPasswordField.getText().equals(newRepeatPasswordField.getText()))){
                    // set the current user
                    ChatClient.getInstance().setCurrentUser(new User(newUsernameField.getText(), fullNameField.getText(), newPasswordField.getText()));
                    // switch to ChatBPT here
                    ChatView chatBPT = new ChatView();
                    chatBPT.show();
                    primaryStage.close();
                } else {
                    // show error message
                    ErrorPaneView errorPane = new ErrorPaneView("Username already exists or Password doesnt match.");
                    root.setBottom(errorPane.getErrorPane());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        VBox inputFields = new VBox(fullNameField, newUsernameField, newPasswordField, newRepeatPasswordField);
        inputFields.setAlignment(Pos.CENTER);
        inputFields.setSpacing(10);

        HBox buttonBar = new HBox(signUpSubmitButton);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        // Create a message for logging in
        Label logInMessage = new Label("Have an account? Log in.");

        // Create a log in button
        TextButton logInButton = new TextButton("button", "Log In");
        logInButton.setOnAction(e -> {
            // Switch to log in screen here
            LogInView logInPage = new LogInView();
            logInPage.show();
            primaryStage.close();
        });

        HBox logInBar = new HBox(logInMessage, logInButton);
        logInBar.setAlignment(Pos.CENTER);
        logInBar.setSpacing(10);

        VBox centerLayout = new VBox(signUpLabel, inputFields, buttonBar, logInBar);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setSpacing(10);
        root.setPadding(new Insets(20));
        root.setCenter(centerLayout);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Sign Up");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }
}

