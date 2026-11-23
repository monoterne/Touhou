package touhou;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController {
    private App app;

    @FXML
    private Label titleLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button continueButton;

    @FXML
    private void initialize() {
        saveButton.setOnAction(event -> app.saveAndExit());
        exitButton.setOnAction(event -> app.exitGame());
    }

    public void setApp(App app) {
        this.app = app;
    }

    void configure(String title, String actionText, Runnable action, boolean canSave) {
        saveButton.setVisible(canSave);
        saveButton.setManaged(canSave);
        titleLabel.setText(title);
        continueButton.setText(actionText);
        continueButton.setOnAction(event -> action.run());
    }
}
