package touhou;

import javafx.geometry.Point2D;

public class BulletAnimated extends Bullet {
    public BulletAnimated(World world, Point2D position, Point2D velocity) {
        super(world, position, velocity);
    }

    public void setVelocity(double velocity, double angle) {
        this.velocity = new Point2D(
                Math.cos(Math.toRadians(angle)) * velocity,
                Math.sin(Math.toRadians(angle)) * velocity);
    }

    @Override
    public void hitBy(Collisonable another) {
        super.hitBy(another);
        if (another instanceof Boss) {
            world.reload();
        }
    }
}
