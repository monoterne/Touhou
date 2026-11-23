package touhou;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {
    private App app;

    @FXML
    private Button startButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button exitButton;

    @FXML
    private void initialize() {
        startButton.setOnAction(event -> app.startGame());
        loadButton.setOnAction(event -> app.loadGame());
        exitButton.setOnAction(event -> app.exitGame());
    }

    public void setApp(App app, boolean hasSavedGame) {
        this.app = app;
        loadButton.setDisable(!hasSavedGame);
    }
}
