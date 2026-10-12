package touhou;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface GameObject {
    void draw(GraphicsContext gc);

    void simulate(double deltaTime);

    Rectangle2D getBounds();

    void onCollision(GameObject other);
}
