package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet extends WorldEntity {
    protected static final double RADIUS = 10;
    private static final double DRAW_WIDTH = 18;
    private static final double DRAW_HEIGHT = 26;
    private static final Image TEXTURE = loadTexture();

    protected Point2D velocity;
    private boolean active = true;

    public Bullet(World world, Point2D position, Point2D velocity) {
        super(world, position);
        this.velocity = velocity;
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.drawImage(TEXTURE, position.getX() - DRAW_WIDTH / 2,
                position.getY() - DRAW_HEIGHT / 2, DRAW_WIDTH, DRAW_HEIGHT);
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

    private static Image loadTexture() {
        var resource = Bullet.class.getResource("/touhou/fireball-transparent-small.gif");
        if (resource == null) {
            throw new IllegalStateException(
                    "Missing enemy bullet texture: /touhou/fireball-transparent-small.gif");
        }
        return new Image(resource.toExternalForm());
    }
}
