package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player implements GameObject {
    private static final double SIZE = 18;
    private static final double SPEED = 260;

    private final double worldWidth;
    private final double worldHeight;
    private Point2D position;
    private double inputX;
    private double inputY;

    public Player(double worldWidth, double worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        position = new Point2D(worldWidth / 2, worldHeight - 55);
    }

    public void setInput(double inputX, double inputY) {
        this.inputX = inputX;
        this.inputY = inputY;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.CYAN);
        gc.setStroke(Color.WHITE);
        gc.fillOval(position.getX() - SIZE / 2, position.getY() - SIZE / 2, SIZE, SIZE);
        gc.strokeOval(position.getX() - SIZE / 2, position.getY() - SIZE / 2, SIZE, SIZE);
        gc.setFill(Color.WHITE);
        gc.fillOval(position.getX() - 2, position.getY() - 2, 4, 4);
    }

    public void simulate(double deltaTime) {
        Point2D direction = new Point2D(inputX, inputY);
        if (direction.magnitude() > 1) {
            direction = direction.normalize();
        }
        position = position.add(direction.multiply(SPEED * deltaTime));
        position = new Point2D(
                clamp(position.getX(), SIZE / 2, worldWidth - SIZE / 2),
                clamp(position.getY(), SIZE / 2, worldHeight - SIZE / 2));
    }

    public Point2D getPosition() {
        return position;
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D(
                position.getX() - SIZE / 2,
                position.getY() - SIZE / 2,
                SIZE,
                SIZE);
    }

    @Override
    public void onCollision(GameObject other) {
        if (other instanceof EnemyBullet) {
            position = new Point2D(worldWidth / 2, worldHeight - 55);
        }
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.clamp(maximum, minimum, value);
    }
}
