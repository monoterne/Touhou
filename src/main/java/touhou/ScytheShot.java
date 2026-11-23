package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

final class ScytheShot extends WorldEntity {
    private static final long serialVersionUID = 1L;
    static final int DAMAGE = 50;

    private static final double SPEED = 460;
    private static final double DRAW_SIZE = 56;
    private static final double HIT_RADIUS = 16;
    private static final Image TEXTURE = loadTexture();

    private double previousY;

    ScytheShot(World world, Point2D position) {
        super(world, position);
        previousY = y;
    }

    @Override
    public void simulate(double deltaTime) {
        previousY = y;
        y -= SPEED * deltaTime;
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.setImageSmoothing(false);
        gc.drawImage(TEXTURE, x - DRAW_SIZE / 2, y - DRAW_SIZE / 2,
                DRAW_SIZE, DRAW_SIZE);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        // Cover the space traveled this frame so fast shots cannot skip over enemy bullets.
        return new Rectangle2D(x - HIT_RADIUS, y - HIT_RADIUS,
                HIT_RADIUS * 2, previousY - y + HIT_RADIUS * 2);
    }

    boolean isOffscreen() {
        return y + DRAW_SIZE / 2 < 0;
    }

    private static Image loadTexture() {
        var resource = ScytheShot.class.getResource("scythe-particle.gif");
        if (resource == null) {
            throw new IllegalStateException("Missing scythe projectile texture: scythe-particle.gif");
        }
        return new Image(resource.toExternalForm());
    }
}
