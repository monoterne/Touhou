package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class World {
    private final double width;
    private final double height;
    private final Player player;
    private final List<EnemyBullet> enemyBullets = new ArrayList<>();
    private double patternTime;
    private double shotAngle;

    public World(double width, double height) {
        this.width = width;
        this.height = height;
        player = new Player(width, height);
    }

    public void draw(GraphicsContext gc) {
        for (EnemyBullet enemyBullet : enemyBullets) {
            enemyBullet.draw(gc);
        }
        player.draw(gc);
    }

    public void simulate(double deltaTime) {
        player.simulate(deltaTime);
        patternTime += deltaTime;
        if (patternTime >= 0.35) {
            patternTime = 0;
            spawnRadialPattern();
        }
        for (Iterator<EnemyBullet> iterator = enemyBullets.iterator(); iterator.hasNext();) {
            EnemyBullet enemyBullet = iterator.next();
            enemyBullet.simulate(deltaTime);
            if (enemyBullet.isOutside(width, height)) {
                iterator.remove();
            }
        }
    }

    public void setPlayerInput(double x, double y) {
        player.setInput(x, y);
    }

    private void spawnRadialPattern() {
        Point2D origin = new Point2D(width / 2, 65);
        for (int i = 0; i < 12; i++) {
            double angle = shotAngle + i * Math.PI * 2 / 12;
            enemyBullets.add(new EnemyBullet(origin,
                    new Point2D(Math.cos(angle) * 85, Math.sin(angle) * 85)));
        }
        shotAngle += Math.PI / 24;
    }
}
