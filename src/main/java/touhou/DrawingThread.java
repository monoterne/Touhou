package touhou;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class DrawingThread extends AnimationTimer {

    private final Canvas canvas;
    private final GraphicsContext gc;

    private long lastFrame = 0;
    private final World world;


    public DrawingThread(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.world = new World(canvas.getWidth(), canvas.getHeight());
    }

    public void setInput(double x, double y) {
        world.setPlayerInput(x, y);
    }

    /**
     * Draws objects into the canvas. Put you code here.
     */
    @Override
    public void handle(long now) {
        double delta = lastFrame == 0 ? 0 : (now - lastFrame) / 1_000_000_000D;
        lastFrame = now;
        drawFps(delta);
        world.simulate(delta);
        world.draw(gc);
    }

    private void drawFps(double delta) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.rgb(10, 10, 28));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        int fps = calcFps(delta);
        gc.setFont(new Font("Input", 18));
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("FPS: %2d", fps), 10, canvas.getHeight() - 10);
    }

    private double fpsSum = 0;
    private double fpsCount = 0;
    private int avergeFps = 0;

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
