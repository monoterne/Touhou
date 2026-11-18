package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class World {
    private static final int OBJECT_COUNT = 40;
    private static final int ENEMY_BULLET_COUNT = 36;
    private static final double SHOOTER_X = 0.5;
    private static final double SHOOTER_Y = 112;
    private static final double BOSS_EMISSION_RADIUS = 52;
    private static final double PLAYER_SHOT_INTERVAL = 0.22;

    private final double width;
    private final double height;
    private final List<WorldEntity> objects =
            new ArrayList<>(Collections.nCopies(OBJECT_COUNT, null));
    private final List<ScytheShot> playerShots = new ArrayList<>();
    private final Player player;
    private final Boss boss;
    private double patternTime;
    private double shotAngle;
    private boolean firing;
    private double shotCooldown;

    public World(double width, double height) {
        this.width = width;
        this.height = height;
        player = new Player(this, width, height);
        boss = new Boss(this, shooterPosition(), width * 0.24);
        objects.set(0, player);
        objects.set(1, boss);
        initializeEnemyBullets();
    }

    public void draw(GraphicsContext gc) {
        objects.stream()
                .filter(World::isActive)
                .sorted(Comparator.comparingDouble(
                        object -> object.getPosition().getY()))
                .forEach(object -> object.draw(gc));
        for (ScytheShot shot : playerShots) {
            shot.draw(gc);
        }
    }

    public void simulate(double deltaTime) {
        if (player.getLives() == 0 || boss.getHealth() == 0) {
            return;
        }
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

        advancePlayerShots(deltaTime);
        if (boss.getHealth() == 0) {
            return;
        }
        for (WorldEntity object : objects) {
            if (object instanceof Bullet bullet
                    && bullet.isActive() && bullet.isOutside(width, height)) {
                bullet.deactivate();
            }
        }
        resolveCollisions();
    }

    public void setPlayerInput(double x, double y) {
        player.setInput(x, y);
    }

    void setFiring(boolean firing) {
        this.firing = firing;
        if (firing) {
            firePlayerShot();
        }
    }

    public int getPlayerLives() {
        return player.getLives();
    }

    int getBossHealth() {
        return boss.getHealth();
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

    private void resolveCollisions() {
        for (int first = 0; first < objects.size(); first++) {
            WorldEntity firstEntity = objects.get(first);
            if (!isActive(firstEntity)) {
                continue;
            }
            for (int second = first + 1; second < objects.size(); second++) {
                WorldEntity secondEntity = objects.get(second);
                if (isActive(secondEntity) && firstEntity.intersect(secondEntity.getBoundingBox())) {
                    firstEntity.hitBy(secondEntity);
                    secondEntity.hitBy(firstEntity);
                }
            }
        }
    }

    private void advancePlayerShots(double deltaTime) {
        Rectangle2D bossBounds = boss.getBoundingBox();
        Iterator<ScytheShot> shots = playerShots.iterator();
        while (shots.hasNext()) {
            ScytheShot shot = shots.next();
            shot.simulate(deltaTime);
            Rectangle2D shotPath = shot.getBoundingBox();
            for (WorldEntity object : objects) {
                if (object instanceof Bullet bullet && bullet.isActive() && bullet.intersect(shotPath)) {
                    bullet.deactivate();
                }
            }

            if (shotPath.intersects(bossBounds)) {
                shots.remove();
                boss.takeDamage(ScytheShot.DAMAGE);
                if (boss.getHealth() == 0) {
                    return;
                }
            } else if (shot.isOffscreen()) {
                shots.remove();
            }
        }

        shotCooldown = Math.max(0, shotCooldown - deltaTime);
        if (firing) {
            firePlayerShot();
        }
    }

    private void firePlayerShot() {
        if (shotCooldown > 0 || player.getLives() == 0 || boss.getHealth() == 0) {
            return;
        }
        playerShots.add(new ScytheShot(this, player.getPosition().add(0, -28)));
        shotCooldown = PLAYER_SHOT_INTERVAL;
    }

    private void spawnRadialPattern() {
        Point2D origin = boss.getPosition();
        for (int i = 0; i < 12; i++) {
            double angle = shotAngle + i * Math.PI * 2 / 12;
            boss.setAngle(Math.toDegrees(angle));
            Point2D direction = new Point2D(Math.cos(angle), Math.sin(angle));
            addObject(new BulletAnimated(this, origin.add(direction.multiply(BOSS_EMISSION_RADIUS)),
                    direction.multiply(85)));
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
        for (int i = 2; i < objects.size(); i++) {
            if (objects.get(i) == null || !isActive(objects.get(i))) {
                objects.set(i, object);
                return;
            }
        }
    }

    private static boolean isActive(WorldEntity object) {
        return object != null && (!(object instanceof Bullet bullet) || bullet.isActive());
    }
}
