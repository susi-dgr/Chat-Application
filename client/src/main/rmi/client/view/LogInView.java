package rmi.client.view;

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
import rmi.client.ChatClient;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class LogInView {

    private final Stage primaryStage = new Stage();

    void show() {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 400);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        Label logInLabel = new Label("Log In");
        logInLabel.setId("log-in-label");

        // Create input fields for username and password
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setText("max");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setText("max");

        // Create a login button
        TextButton loginButton = new TextButton("button", "Log In");
        loginButton.setOnAction(e -> {
            // check if user exists
            try {
                ChatClient.getInstance().initialize();
                if (ChatClient.getInstance().tryLogIn(usernameField.getText(), passwordField.getText())) {
                    // switch to ChatBPT here
                    ChatView chatBPT = new ChatView();
                    chatBPT.show();
                    primaryStage.close();
                } else {
                    // show error message
                    ErrorPaneView errorPane = new ErrorPaneView("Username or password is incorrect.");
                    root.setBottom((errorPane.getErrorPane()));
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            } catch (NotBoundException ex) {
                throw new RuntimeException(ex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Create a message for signing up
        Label signUpMessage = new Label("Don't have an account? Sign up.");

        // Create a sign up button
        TextButton signUpButton = new TextButton("button", "Sign Up");
        signUpButton.setOnAction(e -> {
            // Switch to sign up screen here
            SignUpView signUpPage = new SignUpView();
            signUpPage.show();
            primaryStage.close();
        });

        HBox signUpBar = new HBox(signUpMessage, signUpButton);
        signUpBar.setAlignment(Pos.CENTER);
        signUpBar.setSpacing(10);

        VBox centerLayout = new VBox(logInLabel, usernameField, passwordField, loginButton, signUpBar);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setSpacing(10);
        root.setPadding(new Insets(20));
        root.setCenter(centerLayout);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Log in");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }
}
