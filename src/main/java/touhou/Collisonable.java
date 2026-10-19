package touhou;

import javafx.geometry.Rectangle2D;

public interface Collisonable {
    Rectangle2D getBoundingBox();

    boolean intersect(Rectangle2D another);

    void hitBy(Collisonable another);
}
