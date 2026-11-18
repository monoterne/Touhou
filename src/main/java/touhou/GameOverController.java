package touhou;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController {
    private App app;

    @FXML
    private Label titleLabel;

    @FXML
    private Button exitButton;

    @FXML
    private Button continueButton;

    @FXML
    private void initialize() {
        exitButton.setOnAction(event -> app.exitGame());
        continueButton.setOnAction(event -> app.continueGame());
    }

    public void setApp(App app) {
        this.app = app;
    }

    void configure(String title, String actionText, Runnable action) {
        titleLabel.setText(title);
        continueButton.setText(actionText);
        continueButton.setOnAction(event -> action.run());
    }
}
