package touhou;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Player extends WorldEntity {
    private static final int STARTING_LIVES = 3;
    private static final double SIZE = 44;
    private static final double SPEED = 260;
    private static final double FRAME_DURATION = 0.12;
    private static final double SPRITE_CELL_SIZE = 64;
    private static final double SPRITE_DRAW_SIZE = 60;
    private static final int FRAME_COUNT = 2;
    private static final int DOWN_ROW = 0;
    private static final int LEFT_ROW = 1;
    private static final int RIGHT_ROW = 2;
    private static final int UP_ROW = 3;
    private static final Image SPRITE_SHEET = loadSpriteSheet();

    private final double worldWidth;
    private final double worldHeight;
    private double inputX;
    private double inputY;
    private double animationTime;
    private int directionRow = DOWN_ROW;
    private int animationFrame;
    private int lives = STARTING_LIVES;

    public Player(World world, double worldWidth, double worldHeight) {
        super(world, new Point2D(worldWidth / 2, worldHeight - 55));
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void setInput(double inputX, double inputY) {
        this.inputX = inputX;
        this.inputY = inputY;
    }

    protected void drawInternal(GraphicsContext gc) {
        double drawX = position.getX() - SPRITE_DRAW_SIZE / 2;
        double drawY = position.getY() - SPRITE_DRAW_SIZE / 2;
        gc.drawImage(
                SPRITE_SHEET,
                animationFrame * SPRITE_CELL_SIZE,
                directionRow * SPRITE_CELL_SIZE,
                SPRITE_CELL_SIZE,
                SPRITE_CELL_SIZE,
                drawX,
                drawY,
                SPRITE_DRAW_SIZE,
                SPRITE_DRAW_SIZE);
    }

    public void simulate(double deltaTime) {
        Point2D direction = new Point2D(inputX, inputY);
        if (direction.magnitude() > 1) {
            direction = direction.normalize();
        }
        updateAnimation(direction, deltaTime);
        Point2D position = getPosition().add(direction.multiply(SPEED * deltaTime));
        setPosition(new Point2D(
                clamp(position.getX(), SIZE / 2, worldWidth - SIZE / 2),
                clamp(position.getY(), SIZE / 2, worldHeight - SIZE / 2)));
    }

    public Point2D getPosition() {
        return position;
    }

    public int getLives() {
        return lives;
    }

    @Override
    public javafx.geometry.Rectangle2D getBoundingBox() {
        return new javafx.geometry.Rectangle2D(
                position.getX() - SIZE / 2, position.getY() - SIZE / 2, SIZE, SIZE);
    }

    @Override
    public void hitBy(Collisonable other) {
        if (other instanceof Bullet && lives > 0) {
            lives--;
            setPosition(new Point2D(worldWidth / 2, worldHeight - 55));
        }
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.clamp(value, minimum, maximum);
    }

    private void updateAnimation(Point2D direction, double deltaTime) {
        if (direction.magnitude() == 0) {
            animationFrame = 0;
            animationTime = 0;
            return;
        }

        if (Math.abs(direction.getX()) > Math.abs(direction.getY())) {
            directionRow = direction.getX() < 0 ? LEFT_ROW : RIGHT_ROW;
        } else {
            directionRow = direction.getY() < 0 ? UP_ROW : DOWN_ROW;
        }

        animationTime += deltaTime;
        if (animationTime >= FRAME_DURATION) {
            animationTime -= FRAME_DURATION;
            animationFrame = (animationFrame + 1) % FRAME_COUNT;
        }
    }

    private static Image loadSpriteSheet() {
        var resource = Player.class.getResource("/touhou/bllm.png");
        if (resource == null) {
            throw new IllegalStateException("Missing player sprite sheet: /touhou/bllm.png");
        }
        return new Image(resource.toExternalForm());
    }
}
