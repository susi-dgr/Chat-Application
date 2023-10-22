package rmi.client.view;

import javafx.scene.control.Button;

public class TextButton extends Button{
    public TextButton(String id, String text) {
        super(text);
        setId(id);
    }
}
