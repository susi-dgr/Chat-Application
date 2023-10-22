package rmi.client.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class StartView {
    private final Stage primaryStage = new Stage();

    public void show() {
        Label title = new Label("Welcome to ChatBPT!");
        title.setId("title");

        TextButton login = new TextButton("big-button", "Log In");
        login.setOnAction(e -> {
            // switch to LogIn here
            LogInView logInPage = new LogInView();
            logInPage.show();
            primaryStage.close();
        });

        TextButton signUp = new TextButton("big-button", "Sign Up");
        signUp.setOnAction(e -> {
            // switch to SignUp here
            SignUpView signUpPage = new SignUpView();
            signUpPage.show();
            primaryStage.close();
        });

        Label or = new Label("or");
        or.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        HBox buttonBox = new HBox(login, or, signUp);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        VBox centerLayout = new VBox(title, buttonBox);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setSpacing(10);

        // Create root pane
        BorderPane root = new BorderPane();
        root.setCenter(centerLayout);

        // Create scene and show stage
        Scene scene = new Scene(root, 800, 600);

        // Load style sheet
        scene.getStylesheets().add(getClass().getResource("css/chatBPT.css").toExternalForm());

        primaryStage.setTitle("ChatBPT");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
