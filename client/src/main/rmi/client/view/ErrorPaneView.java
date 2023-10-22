package rmi.client.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ErrorPaneView {
    private VBox errorPane;

    public ErrorPaneView(String message) {
        Label errorLabel = new Label(message);
        errorLabel.getStyleClass().add("error");
        errorLabel.setAlignment(Pos.CENTER);

        errorPane = new VBox(errorLabel);
        errorPane.setAlignment(Pos.CENTER);
    }
    public Pane getErrorPane() {
        return errorPane;
    }
}
