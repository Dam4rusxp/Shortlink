package de.damarus.shortlink.front;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class QuickwindowFX {

    @FXML
    private TextField txtLink;

    @FXML
    private void initialize() {
        txtLink.setText("hallo!");
    }
}
