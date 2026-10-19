package touhou;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class MainController {
    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    public void startGame(ActionEvent event) {
        app.startGame();
    }

    public void exitGame(ActionEvent event) {
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }
}
