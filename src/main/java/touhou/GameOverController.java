package touhou;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class GameOverController {
    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    public void exitGame(ActionEvent event) {
        app.exitGame();
    }

    @FXML
    public void continueGame(ActionEvent event) {
        app.continueGame();
    }
}
