package touhou;

import javafx.event.ActionEvent;

public class GameOverController {
    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    public void exitGame(ActionEvent event) {
        app.exitGame();
    }

    public void continueGame(ActionEvent event) {
        app.continueGame();
    }
}
