package touhou;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class MainController {
    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    public void startGame(ActionEvent event) {
        app.startGame();
    }

    @FXML
    public void exitGame(ActionEvent event) {
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }
}
