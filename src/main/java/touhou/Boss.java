package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Boss extends Cannon {
    private static final double WIDTH = 60;
    private static final double HEIGHT = 47;
    private static final Image TEXTURE = loadTexture();

    public Boss(World world, Point2D position) {
        super(world, position);
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        gc.drawImage(TEXTURE, position.getX() - WIDTH / 2,
                position.getY() - HEIGHT / 2, WIDTH, HEIGHT);
    }

    @Override
    public javafx.geometry.Rectangle2D getBoundingBox() {
        return new javafx.geometry.Rectangle2D(
                position.getX() - WIDTH / 2, position.getY() - HEIGHT / 2, WIDTH, HEIGHT);
    }

    private static Image loadTexture() {
        var resource = Boss.class.getResource("/touhou/ufo-small.gif");
        if (resource == null) {
            throw new IllegalStateException("Missing boss texture: /touhou/ufo-small.gif");
        }
        return new Image(resource.toExternalForm());
    }
}
