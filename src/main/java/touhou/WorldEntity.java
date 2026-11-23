package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

public abstract class WorldEntity implements DrawableSimulable, Collisonable, Serializable {
    private static final long serialVersionUID = 1L;
    protected final World world;
    protected double x;
    protected double y;

    protected WorldEntity(World world, Point2D position) {
        this.world = world;
        this.x = position.getX();
        this.y = position.getY();
    }

    public final void draw(GraphicsContext gc) {
        gc.save();
        drawInternal(gc);
        gc.restore();
    }

    protected abstract void drawInternal(GraphicsContext gc);

    public Point2D getPosition() {
        return new Point2D(x, y);
    }

    protected void setPosition(Point2D position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(x, y, 0, 0);
    }

    @Override
    public boolean intersect(Rectangle2D another) {
        return getBoundingBox().intersects(another);
    }

    @Override
    public void hitBy(Collisonable another) {
    }
}
