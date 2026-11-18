package touhou;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    private DrawingThread timer;
    private Stage primaryStage;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final GameDataStore gameDataStore =
            new GameDataStore(Path.of(System.getProperty("user.dir"), "touhou.dat"));
    private GameData gameData;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            gameData = gameDataStore.load(Path.of(System.getProperty("user.dir")));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Scene menuScene = new Scene(loader.load(), 1024, 768);
            loader.<MainController>getController().setApp(this);
            primaryStage.setScene(menuScene);
            primaryStage.resizableProperty().set(false);
            primaryStage.setTitle("Touhou");
            primaryStage.setOnCloseRequest(this::exitProgram);
            primaryStage.focusedProperty().addListener((observable, wasFocused, focused) -> {
                if (!focused && timer != null) {
                    pressedKeys.clear();
                    updateInput();
                }
            });
            primaryStage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main view", e);
        }
    }

    public void startGame() {
        if (timer != null) {
            timer.stop();
        }
        pressedKeys.clear();

        Group root = new Group();
        Canvas canvas = new Canvas(1024, 768);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        timer = new DrawingThread(canvas, () -> finishRound("Game over!"), () -> finishRound("Victory!"));
        addHud(root, canvas);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                showExitConfirmation();
                return;
            }
            pressedKeys.add(event.getCode());
            updateInput();
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            updateInput();
        });
        primaryStage.setScene(scene);
        timer.start();
    }

    private void addHud(Group root, Canvas canvas) {
        Label livesLabel = new Label("Lives: " + timer.getPlayerLives());
        livesLabel.getStyleClass().add("lives-label");
        timer.setLivesLabel(livesLabel);
        ProgressBar livesBar = new ProgressBar();
        livesBar.setAccessibleText("Player lives");
        livesBar.getStyleClass().addAll("health-bar", "player-health-bar");
        livesBar.setPrefSize(126, 8);
        livesBar.progressProperty().bind(timer.playerLivesProperty().divide((double) Player.STARTING_LIVES));
        VBox playerHud = new VBox(4, livesLabel, livesBar);
        playerHud.setLayoutX(12);
        playerHud.setLayoutY(12);

        Label bossLabel = new Label();
        bossLabel.getStyleClass().add("boss-health-label");
        bossLabel.textProperty().bind(timer.bossHealthProperty().asString("Boss HP %d / " + Boss.MAX_HEALTH));
        ProgressBar bossBar = new ProgressBar();
        bossBar.setAccessibleText("Boss health");
        bossBar.getStyleClass().addAll("health-bar", "boss-health-bar");
        bossBar.setPrefSize(356, 10);
        bossBar.progressProperty().bind(timer.bossHealthProperty().divide((double) Boss.MAX_HEALTH));
        VBox bossHud = new VBox(5, bossLabel, bossBar);
        bossHud.setAlignment(Pos.CENTER);
        bossHud.layoutXProperty().bind(canvas.widthProperty().subtract(bossHud.widthProperty()).divide(2));
        bossHud.setLayoutY(10);

        Label controlsLabel = new Label("E to shoot");
        controlsLabel.getStyleClass().add("lives-label");
        controlsLabel.layoutXProperty().bind(canvas.widthProperty().subtract(controlsLabel.widthProperty()).subtract(12));
        controlsLabel.setLayoutY(12);
        root.getChildren().addAll(playerHud, bossHud, controlsLabel);
    }

    public void showExitConfirmation() {
        if (timer == null) {
            return;
        }
        Scene gameScene = primaryStage.getScene();
        DrawingThread pausedTimer = timer;
        pausedTimer.stop();
        pressedKeys.clear();
        pausedTimer.setInput(0, 0);
        pausedTimer.setFiring(false);
        showRoundMenu("Paused", "Resume", () -> {
            primaryStage.setScene(gameScene);
            pausedTimer.start();
        });
    }

    private void finishRound(String title) {
        timer = null;
        pressedKeys.clear();
        showRoundMenu(title, "Restart", this::continueGame);
    }

    private void showRoundMenu(String title, String actionText, Runnable action) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game-over.fxml"));
            Scene menuScene = new Scene(loader.load(), 1024, 768);
            GameOverController controller = loader.getController();
            controller.setApp(this);
            controller.configure(title, actionText, action);
            primaryStage.setScene(menuScene);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the round menu", e);
        }
    }

    public void continueGame() {
        startGame();
    }

    public void exitGame() {
        saveGameData();
        primaryStage.close();
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) {
            timer.stop();
        }
        saveGameData();
        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        saveGameData();
        System.exit(0);
    }

    private void saveGameData() {
        if (gameData != null) {
            gameDataStore.save(gameData);
        }
    }

    private void updateInput() {
        timer.setInput(
                axis(KeyCode.RIGHT, KeyCode.D) - axis(KeyCode.LEFT, KeyCode.A),
                axis(KeyCode.DOWN, KeyCode.S) - axis(KeyCode.UP, KeyCode.W));
        timer.setFiring(pressedKeys.contains(KeyCode.E));
    }

    private double axis(KeyCode positive, KeyCode alternative) {
        return pressedKeys.contains(positive) || pressedKeys.contains(alternative) ? 1 : 0;
    }
}
