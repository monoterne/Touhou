package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class Bullet extends WorldEntity {
    protected static final double RADIUS = 10;
    private static final double ORB_RADIUS = 8;
    private static final double GLOW_RADIUS = 16;
    private static final RadialGradient ORB_GLOW = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
            CycleMethod.NO_CYCLE,
            new Stop(0.45, Color.web("#aa70ff", 0.56)),
            new Stop(0.72, Color.web("#9a60f5", 0.20)),
            new Stop(1, Color.TRANSPARENT));
    private static final RadialGradient ORB_CORE = new RadialGradient(225, 0.25, 0.5, 0.5, 0.5, true,
            CycleMethod.NO_CYCLE,
            new Stop(0.28, Color.WHITE),
            new Stop(0.65, Color.web("#e9d5ff")),
            new Stop(1, Color.web("#a96af3")));

    protected Point2D velocity;
    private boolean active = true;

    public Bullet(World world, Point2D position, Point2D velocity) {
        super(world, position);
        this.velocity = velocity;
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        double x = position.getX();
        double y = position.getY();
        gc.setFill(ORB_GLOW);
        gc.fillOval(x - GLOW_RADIUS, y - GLOW_RADIUS, GLOW_RADIUS * 2, GLOW_RADIUS * 2);
        gc.setFill(ORB_CORE);
        gc.fillOval(x - ORB_RADIUS, y - ORB_RADIUS, ORB_RADIUS * 2, ORB_RADIUS * 2);
    }

    @Override
    public void simulate(double deltaTime) {
        setPosition(position.add(velocity.multiply(deltaTime)));
    }

    @Override
    public javafx.geometry.Rectangle2D getBoundingBox() {
        return new javafx.geometry.Rectangle2D(
                position.getX() - RADIUS, position.getY() - RADIUS, RADIUS * 2, RADIUS * 2);
    }

    public boolean isOutside(double width, double height) {
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
    public void hitBy(Collisonable another) {
        if (another instanceof Player) {
            deactivate();
        }
    }
}
