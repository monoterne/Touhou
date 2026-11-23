package touhou;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class Boss extends Cannon {
    private static final long serialVersionUID = 1L;
    static final int MAX_HEALTH = 1_000;

    private static final double SPRITE_SIZE = 112;
    private static final double SWEEP_PERIOD_SECONDS = 18;
    private static final double BOB_PERIOD_SECONDS = 3;
    private static final double BOB_HEIGHT = 5;
    private static final Image TEXTURE = loadTexture();
    private static final RadialGradient HALO = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
            CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#a96bff", 0.25)),
            new Stop(0.55, Color.web("#8050dc", 0.15)),
            new Stop(1, Color.TRANSPARENT));
    private static final Color HALO_RING = Color.web("#b986ff", 0.40);

    private final double centerX;
    private final double centerY;
    private final double sweepDistance;
    private int health = MAX_HEALTH;
    private double elapsed;

    public Boss(World world, Point2D position) {
        this(world, position, 0);
    }

    Boss(World world, Point2D position, double sweepDistance) {
        super(world, position);
        this.centerX = position.getX();
        this.centerY = position.getY();
        this.sweepDistance = sweepDistance;
    }

    int getHealth() {
        return health;
    }

    void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    @Override
    public void simulate(double deltaTime) {
        elapsed += deltaTime;
        setPosition(new Point2D(
                centerX + Math.sin(2 * Math.PI * elapsed / SWEEP_PERIOD_SECONDS) * sweepDistance,
                centerY + Math.sin(2 * Math.PI * elapsed / BOB_PERIOD_SECONDS) * BOB_HEIGHT));
    }

    @Override
    protected void drawInternal(GraphicsContext gc) {
        double pulse = 0.86 + 0.14 * Math.cos(2 * Math.PI * elapsed / BOB_PERIOD_SECONDS);
        gc.setGlobalAlpha(pulse);
        gc.setFill(HALO);
        gc.fillOval(x - 100, y - 88, 200, 176);
        gc.setStroke(HALO_RING);
        gc.setLineWidth(1.2);
        double ringRadius = 64 + 4 * pulse;
        gc.strokeOval(x - ringRadius, y - ringRadius, ringRadius * 2, ringRadius * 2);
        gc.setGlobalAlpha(1);
        gc.setImageSmoothing(false);
        gc.drawImage(TEXTURE, x - SPRITE_SIZE / 2, y - SPRITE_SIZE / 2, SPRITE_SIZE, SPRITE_SIZE);
    }

    @Override
    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(x - 30, y - 24, 60, 48);
    }

    private static Image loadTexture() {
        var resource = Boss.class.getResource("boss.png");
        if (resource == null) {
            throw new IllegalStateException("Missing boss texture: boss.png");
        }
        return new Image(resource.toExternalForm());
    }
}
