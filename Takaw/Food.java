import java.awt.*;
import java.util.UUID;

public class Food implements Constants {
    private String id;
    private int type;
    private Color colour;
    private int x;
    private int y;

    public Food(String id, int type, int x, int y) {
        this.id = id;
        this.type = type;
        if (type == GOOD) {
            colour = Color.GREEN;
        } else if (type == BAD) {
            colour = Color.RED;
        }
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public Color getColour() {
        return colour;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
