package touhou;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DrawingThread extends AnimationTimer {
    private static final Image BACKGROUND = loadBackground();

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Runnable onGameOver;
    private final Runnable onVictory;
    private final ReadOnlyIntegerWrapper playerLives;
    private final ReadOnlyIntegerWrapper bossHealth;
    private Label livesLabel;

    private long lastFrame = 0;
    private final World world;


    public DrawingThread(Canvas canvas) {
        this(canvas, new World(canvas.getWidth(), canvas.getHeight()), () -> {}, () -> {});
    }

    DrawingThread(Canvas canvas, World world, Runnable onGameOver, Runnable onVictory) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.onGameOver = onGameOver;
        this.onVictory = onVictory;
        this.world = world;
        this.playerLives = new ReadOnlyIntegerWrapper(world.getPlayerLives());
        this.bossHealth = new ReadOnlyIntegerWrapper(world.getBossHealth());
    }

    World getWorld() {
        return world;
    }

    public void setInput(double x, double y) {
        world.setPlayerInput(x, y);
    }

    void setFiring(boolean firing) {
        world.setFiring(firing);
    }

    public int getPlayerLives() {
        return world.getPlayerLives();
    }

    ReadOnlyIntegerProperty playerLivesProperty() {
        return playerLives.getReadOnlyProperty();
    }

    ReadOnlyIntegerProperty bossHealthProperty() {
        return bossHealth.getReadOnlyProperty();
    }

    public void setLivesLabel(Label livesLabel) {
        this.livesLabel = livesLabel;
    }

    @Override
    public void stop() {
        super.stop();
        lastFrame = 0;
    }

    /**
     * Draws objects into the canvas. Put you code here.
     */
    @Override
    public void handle(long now) {
        double delta = lastFrame == 0 ? 0 : (now - lastFrame) / 1_000_000_000D;
        lastFrame = now;
        world.simulate(delta);
        int lives = world.getPlayerLives();
        int health = world.getBossHealth();
        playerLives.set(lives);
        bossHealth.set(health);
        if (livesLabel != null) {
            livesLabel.setText("Lives: " + lives);
        }
        if (lives == 0 || health == 0) {
            stop();
            (health == 0 ? onVictory : onGameOver).run();
            return;
        }
        gc.drawImage(BACKGROUND, 0, 0, canvas.getWidth(), canvas.getHeight());
        world.draw(gc);
        drawFps(delta);
    }

    private void drawFps(double delta) {
        int fps = calcFps(delta);
        gc.setFont(new Font("Input", 18));
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("FPS: %2d", fps), 10, canvas.getHeight() - 10);
    }

    private double fpsSum = 0;
    private double fpsCount = 0;
    private int avergeFps = 0;

    private static Image loadBackground() {
        var resource = DrawingThread.class.getResource("bg.png");
        if (resource == null) {
            throw new IllegalStateException("Missing stage background: bg.png");
        }
        return new Image(resource.toExternalForm());
    }

    private int calcFps(double delta) {
        if (delta <= 0) {
            return avergeFps;
        }
        fpsSum += 1 / delta;
        fpsCount += 1;
        if (fpsCount >= 100) {
            avergeFps = (int) (fpsSum / fpsCount);
            fpsSum = 0;
            fpsCount = 0;
        }
        return avergeFps;
    }

}
