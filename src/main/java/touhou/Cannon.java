package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Cannon extends WorldEntity {
    private double angle;

    public Cannon(World world, Point2D position) {
        super(world, position);
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
    }

    @Override
    public void simulate(double deltaTime) {
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
