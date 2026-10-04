package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyBullet {
    private static final double RADIUS = 5;

    private Point2D position;
    private final Point2D velocity;

    public EnemyBullet(Point2D position, Point2D velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.ORANGERED);
        gc.setStroke(Color.YELLOW);
        gc.fillOval(position.getX() - RADIUS, position.getY() - RADIUS,
                RADIUS * 2, RADIUS * 2);
        gc.strokeOval(position.getX() - RADIUS, position.getY() - RADIUS,
                RADIUS * 2, RADIUS * 2);
    }

    public void simulate(double deltaTime) {
        position = position.add(velocity.multiply(deltaTime));
    }

    public boolean isOutside(double width, double height) {
        return position.getX() < -RADIUS || position.getX() > width + RADIUS
                || position.getY() < -RADIUS || position.getY() > height + RADIUS;
    }
}
