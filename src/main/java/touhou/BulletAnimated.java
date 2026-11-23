package touhou;

import javafx.geometry.Point2D;

public class BulletAnimated extends Bullet {
    private static final long serialVersionUID = 1L;
    public BulletAnimated(World world, Point2D position, Point2D velocity) {
        super(world, position, velocity);
    }

    public void setVelocity(double velocity, double angle) {
        velocityX = Math.cos(Math.toRadians(angle)) * velocity;
        velocityY = Math.sin(Math.toRadians(angle)) * velocity;
    }

    @Override
    public void hitBy(Collisonable another) {
        super.hitBy(another);
        if (another instanceof Boss) {
            world.reload();
        }
    }
}
