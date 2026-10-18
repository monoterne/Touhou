package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    private static final int ENEMY_BULLET_COUNT = 36;
    private static final double SHOOTER_X = 0.5;
    private static final double SHOOTER_Y = 65;
    private static final double SHOOTER_RADIUS = 24;

    private final double width;
    private final double height;
    private final Player player;
    private final EnemyBullet[] enemyBullets = new EnemyBullet[ENEMY_BULLET_COUNT];
    private double patternTime;
    private double shotAngle;

    public World(double width, double height) {
        this.width = width;
        this.height = height;
        player = new Player(width, height);
        initializeEnemyBullets();
    }

    public void draw(GraphicsContext gc) {
        drawShooter(gc);
        for (EnemyBullet enemyBullet : enemyBullets) {
            if (enemyBullet != null && enemyBullet.isActive()) {
                drawObject(enemyBullet, gc);
            }
        }
        drawObject(player, gc);
    }

    public void simulate(double deltaTime) {
        simulateObject(player, deltaTime);
        patternTime += deltaTime;
        if (patternTime >= 0.35) {
            patternTime = 0;
            spawnRadialPattern();
        }
        for (EnemyBullet enemyBullet : enemyBullets) {
            if (enemyBullet == null || !enemyBullet.isActive()) {
                continue;
            }
            simulateObject(enemyBullet, deltaTime);
            if (enemyBullet.isOutside(width, height)) {
                enemyBullet.deactivate();
                continue;
            }

            if (player.getBounds().intersects(enemyBullet.getBounds())) {
                player.onCollision(enemyBullet);
                enemyBullet.onCollision(player);
            }
        }
    }

    private static void drawObject(Drawable object, GraphicsContext gc) {
        object.draw(gc);
    }

    private static void simulateObject(Simulatable object, double deltaTime) {
        object.simulate(deltaTime);
    }

    public void setPlayerInput(double x, double y) {
        player.setInput(x, y);
    }

    private void spawnRadialPattern() {
        Point2D origin = shooterPosition();
        for (int i = 0; i < 12; i++) {
            double angle = shotAngle + i * Math.PI * 2 / 12;
            addEnemyBullet(new EnemyBullet(origin,
                    new Point2D(Math.cos(angle) * 85, Math.sin(angle) * 85)));
        }
        shotAngle += Math.PI / 24;
    }

    private void drawShooter(GraphicsContext gc) {
        Point2D position = shooterPosition();
        double diameter = SHOOTER_RADIUS * 2;

        gc.setFill(Color.MEDIUMPURPLE);
        gc.setStroke(Color.PLUM);
        gc.setLineWidth(2);
        gc.fillOval(position.getX() - SHOOTER_RADIUS,
                position.getY() - SHOOTER_RADIUS, diameter, diameter);
        gc.strokeOval(position.getX() - SHOOTER_RADIUS,
                position.getY() - SHOOTER_RADIUS, diameter, diameter);

        gc.setFill(Color.WHITE);
        gc.fillOval(position.getX() - 11, position.getY() - 7, 8, 11);
        gc.fillOval(position.getX() + 3, position.getY() - 7, 8, 11);
        gc.setFill(Color.DARKSLATEBLUE);
        gc.fillOval(position.getX() - 8, position.getY() - 3, 3, 5);
        gc.fillOval(position.getX() + 6, position.getY() - 3, 3, 5);
    }

    private Point2D shooterPosition() {
        return new Point2D(width * SHOOTER_X, SHOOTER_Y);
    }

    private void initializeEnemyBullets() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < enemyBullets.length; i++) {
            Point2D position = new Point2D(
                    random.nextDouble(20, width - 20),
                    random.nextDouble(20, height / 2));
            Point2D velocity = new Point2D(
                    random.nextDouble(-70, 70),
                    random.nextDouble(35, 120));
            enemyBullets[i] = new EnemyBullet(position, velocity);
        }
    }

    private void addEnemyBullet(EnemyBullet enemyBullet) {
        for (int i = 0; i < enemyBullets.length; i++) {
            if (enemyBullets[i] == null || !enemyBullets[i].isActive()) {
                enemyBullets[i] = enemyBullet;
                return;
            }
        }
    }
}
