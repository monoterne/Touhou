package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class EnemyBullet extends AbstractCircularGameObject {
    private static final double RADIUS = 5;

    private final Point2D velocity;
    private boolean active = true;

    public EnemyBullet(Point2D position, Point2D velocity) {
        super(position, RADIUS);
        this.velocity = velocity;
    }

    public void draw(GraphicsContext gc) {
        Point2D position = getPosition();
        gc.setFill(Color.ORANGERED);
        gc.setStroke(Color.YELLOW);
        gc.fillOval(position.getX() - RADIUS, position.getY() - RADIUS,
                RADIUS * 2, RADIUS * 2);
        gc.strokeOval(position.getX() - RADIUS, position.getY() - RADIUS,
                RADIUS * 2, RADIUS * 2);
    }

    public void simulate(double deltaTime) {
        setPosition(getPosition().add(velocity.multiply(deltaTime)));
    }

    public boolean isOutside(double width, double height) {
        Point2D position = getPosition();
        return position.getX() < -RADIUS || position.getX() > width + RADIUS
                || position.getY() < -RADIUS || position.getY() > height + RADIUS;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    @Override
    public void onCollision(GameObject other) {
        if (other instanceof Player) {
            deactivate();
        }
    }
}
