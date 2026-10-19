package touhou;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import java.util.HashSet;
import java.util.Set;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Canvas canvas;
    private AnimationTimer timer;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    @Override
    public void start(Stage primaryStage) {
        try {
            // Construct a main window with a canvas.
            Group root = new Group();
            canvas = new Canvas(1024, 768);
            root.getChildren().add(canvas);
            Scene scene = new Scene(root, 1024, 768);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.resizableProperty().set(false);
            primaryStage.setTitle("Touhou");
            primaryStage.show();
            scene.setOnKeyPressed(event -> {
                pressedKeys.add(event.getCode());
                updateInput();
            });
            scene.setOnKeyReleased(event -> {
                pressedKeys.remove(event.getCode());
                updateInput();
            });
            // Exit program when main window is closed
            primaryStage.setOnCloseRequest(this::exitProgram);
            timer = new DrawingThread(canvas);
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        timer.stop();
        super.stop();
    }

    private void exitProgram(WindowEvent evt) {
        System.exit(0);
    }

    private void updateInput() {
        DrawingThread drawingThread = (DrawingThread) timer;
        drawingThread.setInput(
                axis(KeyCode.RIGHT, KeyCode.D) - axis(KeyCode.LEFT, KeyCode.A),
                axis(KeyCode.DOWN, KeyCode.S) - axis(KeyCode.UP, KeyCode.W));
    }

    private double axis(KeyCode positive, KeyCode alternative) {
        return pressedKeys.contains(positive) || pressedKeys.contains(alternative) ? 1 : 0;
    }
}
