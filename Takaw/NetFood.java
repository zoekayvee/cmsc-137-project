/*
* Food
*/

import java.awt.*;
import java.util.Random;
import java.util.UUID;

public class NetFood implements Constants {
    private String id;
    private int type;
    private Color colour;
    private int x;
    private int y;
    private int points;

    Random random = new Random();

    public NetFood(int type) {
        this.id = UUID.randomUUID().toString();;
        this.type = type;
        if (type == GOOD) {
            colour = Color.GREEN;
            points = 10;
        } else if (type == BAD) {
            colour = Color.RED;
            points = -10;
        }
        this.x = random.nextInt(640+1);
        this.y = random.nextInt(480+1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String toString(){
        // Format: "FOOD <id> <type> <x> <y>"
        String retval="";
        retval+="FOOD ";
        retval+=id+" ";
        retval+=type+" ";
        retval+=x+" ";
        retval+=y;
        return retval;
    }
}
