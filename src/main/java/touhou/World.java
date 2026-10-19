package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    private static final int OBJECT_COUNT = 40;
    private static final int ENEMY_BULLET_COUNT = 36;
    private static final double SHOOTER_X = 0.5;
    private static final double SHOOTER_Y = 65;

    private final double width;
    private final double height;
    private final WorldEntity[] objects = new WorldEntity[OBJECT_COUNT];
    private final Player player;
    private final Boss boss;
    private double patternTime;
    private double shotAngle;

    public World(double width, double height) {
        this.width = width;
        this.height = height;
        player = new Player(this, width, height);
        boss = new Boss(this, shooterPosition());
        objects[0] = player;
        objects[1] = boss;
        initializeEnemyBullets();
    }

    public void draw(GraphicsContext gc) {
        for (WorldEntity object : objects) {
            if (isActive(object)) {
                object.draw(gc);
            }
        }
    }

    public void simulate(double deltaTime) {
        for (WorldEntity object : objects) {
            if (isActive(object)) {
                object.simulate(deltaTime);
            }
        }

        patternTime += deltaTime;
        if (patternTime >= 0.35) {
            patternTime = 0;
            spawnRadialPattern();
        }

        for (WorldEntity object : objects) {
            if (object instanceof Bullet bullet
                    && bullet.isActive() && bullet.isOutside(width, height)) {
                bullet.deactivate();
            }
        }

        for (int first = 0; first < objects.length; first++) {
            if (!isActive(objects[first])) {
                continue;
            }
            for (int second = first + 1; second < objects.length; second++) {
                if (!isActive(objects[second])) {
                    continue;
                }
                if (objects[first].intersect(objects[second].getBoundingBox())) {
                    objects[first].hitBy(objects[second]);
                    objects[second].hitBy(objects[first]);
                }
            }
        }
    }

    public void setPlayerInput(double x, double y) {
        player.setInput(x, y);
    }

    public void reload() {
        for (WorldEntity object : objects) {
            if (object instanceof BulletAnimated bullet && bullet.isActive()) {
                bullet.setPosition(boss.getPosition());
                bullet.setVelocity(85, boss.getAngle());
                return;
            }
        }
    }

    private void spawnRadialPattern() {
        Point2D origin = boss.getPosition();
        for (int i = 0; i < 12; i++) {
            double angle = shotAngle + i * Math.PI * 2 / 12;
            boss.setAngle(Math.toDegrees(angle));
            addObject(new BulletAnimated(this, origin,
                    new Point2D(Math.cos(angle) * 85, Math.sin(angle) * 85)));
        }
        shotAngle += Math.PI / 24;
    }

    private Point2D shooterPosition() {
        return new Point2D(width * SHOOTER_X, SHOOTER_Y);
    }

    private void initializeEnemyBullets() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < ENEMY_BULLET_COUNT; i++) {
            Point2D position = new Point2D(
                    random.nextDouble(20, width - 20),
                    random.nextDouble(20, height / 2));
            Point2D velocity = new Point2D(
                    random.nextDouble(-70, 70),
                    random.nextDouble(35, 120));
            addObject(new BulletAnimated(this, position, velocity));
        }
    }

    private void addObject(WorldEntity object) {
        for (int i = 2; i < objects.length; i++) {
            if (objects[i] == null || !isActive(objects[i])) {
                objects[i] = object;
                return;
            }
        }
    }

    private static boolean isActive(WorldEntity object) {
        return object != null && (!(object instanceof Bullet bullet) || bullet.isActive());
    }
}
