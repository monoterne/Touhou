package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class WorldEntity implements DrawableSimulable, Collisonable {
    protected final World world;
    protected Point2D position;

    protected WorldEntity(World world, Point2D position) {
        this.world = world;
        this.position = position;
    }

    public final void draw(GraphicsContext gc) {
        gc.save();
        drawInternal(gc);
        gc.restore();
    }

    protected abstract void drawInternal(GraphicsContext gc);

    public Point2D getPosition() {
        return position;
    }

    protected void setPosition(Point2D position) {
        this.position = position;
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(position.getX(), position.getY(), 0, 0);
    }

    @Override
    public boolean intersect(Rectangle2D another) {
        return getBoundingBox().intersects(another);
    }

    @Override
    public void hitBy(Collisonable another) {
    }
}
