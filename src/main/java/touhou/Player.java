package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends AbstractCircularGameObject {
    private static final double SIZE = 18;
    private static final double SPEED = 260;

    private final double worldWidth;
    private final double worldHeight;
    private double inputX;
    private double inputY;

    public Player(double worldWidth, double worldHeight) {
        super(new Point2D(worldWidth / 2, worldHeight - 55), SIZE / 2);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void setInput(double inputX, double inputY) {
        this.inputX = inputX;
        this.inputY = inputY;
    }

    public void draw(GraphicsContext gc) {
        Point2D position = getPosition();
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
        Point2D position = getPosition().add(direction.multiply(SPEED * deltaTime));
        setPosition(new Point2D(
                clamp(position.getX(), SIZE / 2, worldWidth - SIZE / 2),
                clamp(position.getY(), SIZE / 2, worldHeight - SIZE / 2)));
    }

    public Point2D getPosition() {
        return super.getPosition();
    }

    @Override
    public void onCollision(GameObject other) {
        if (other instanceof EnemyBullet) {
            setPosition(new Point2D(worldWidth / 2, worldHeight - 55));
        }
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.clamp(maximum, minimum, value);
    }
}
