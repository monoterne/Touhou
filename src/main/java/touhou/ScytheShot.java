package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

final class ScytheShot extends WorldEntity {
    static final int DAMAGE = 50;

    private static final double SPEED = 460;
    private static final double DRAW_SIZE = 56;
    private static final double HIT_RADIUS = 16;
    private static final Image TEXTURE = loadTexture();

    private double previousY;

    ScytheShot(World world, Point2D position) {
        super(world, position);
        previousY = position.getY();
    }

    @Override
    public void simulate(double deltaTime) {
        previousY = position.getY();
        setPosition(position.add(0, -SPEED * deltaTime));
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        gc.drawImage(TEXTURE, position.getX() - DRAW_SIZE / 2, position.getY() - DRAW_SIZE / 2,
                DRAW_SIZE, DRAW_SIZE);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        // Cover the space traveled this frame so fast shots cannot skip over enemy bullets.
        return new Rectangle2D(position.getX() - HIT_RADIUS, position.getY() - HIT_RADIUS,
                HIT_RADIUS * 2, previousY - position.getY() + HIT_RADIUS * 2);
    }

    boolean isOffscreen() {
        return position.getY() + DRAW_SIZE / 2 < 0;
    }

    private static Image loadTexture() {
        var resource = ScytheShot.class.getResource("scythe-particle.gif");
        if (resource == null) {
            throw new IllegalStateException("Missing scythe projectile texture: scythe-particle.gif");
        }
        return new Image(resource.toExternalForm());
    }
}
