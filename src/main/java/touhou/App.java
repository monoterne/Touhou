package touhou;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.io.IOException;
import java.util.Set;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Canvas canvas;
    private DrawingThread timer;
    private Stage primaryStage;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Scene menuScene = new Scene(loader.load(), 1024, 768);
            loader.<MainController>getController().setApp(this);
            primaryStage.setScene(menuScene);
            primaryStage.resizableProperty().set(false);
            primaryStage.setTitle("Touhou");
            primaryStage.show();
            primaryStage.setOnCloseRequest(this::exitProgram);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main view.", e);
        }
    }

    public void startGame() {
        if (timer != null) {
            timer.stop();
        }

        // Construct the game window with a canvas.
        Group root = new Group();
        canvas = new Canvas(1024, 768);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 1024, 768);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.resizableProperty().set(false);
        primaryStage.setTitle("Touhou");
        primaryStage.show();
        Label livesLabel = new Label("Lives: 3");
        livesLabel.getStyleClass().add("lives-label");
        livesLabel.setLayoutX(12);
        livesLabel.setLayoutY(12);
        root.getChildren().add(livesLabel);
        scene.setOnKeyPressed(event -> {
            pressedKeys.add(event.getCode());
            updateInput();
            if (event.getCode() == KeyCode.ESCAPE) {
                showExitConfirmation();
            }
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            updateInput();
        });
        timer = new DrawingThread(canvas);
        timer.setLivesLabel(livesLabel);
        timer.start();
    }

    public void showExitConfirmation() {
        try {
            if (timer != null) {
                timer.stop();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game-over.fxml"));
            Scene gameOverScene = new Scene(loader.load(), 1024, 768);
            loader.<GameOverController>getController().setApp(this);
            primaryStage.setScene(gameOverScene);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the game-over view.", e);
        }
    }

    public void continueGame() {
        startGame();
    }

    public void exitGame() {
        primaryStage.close();
    }

    @Override
    public void stop() throws Exception {
        if (timer != null) {
            timer.stop();
        }
        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }

    private void updateInput() {
        timer.setInput(
                axis(KeyCode.RIGHT, KeyCode.D) - axis(KeyCode.LEFT, KeyCode.A),
                axis(KeyCode.DOWN, KeyCode.S) - axis(KeyCode.UP, KeyCode.W));
    }

    private double axis(KeyCode positive, KeyCode alternative) {
        return pressedKeys.contains(positive) || pressedKeys.contains(alternative) ? 1 : 0;
    }
}
