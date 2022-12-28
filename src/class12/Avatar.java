package class12;

import java.awt.Point;

import javafx.scene.image.Image;

public abstract class Avatar {
    public final Image image;

    // x‚Æy‚ğ‚ÂPointƒNƒ‰ƒX (https://docs.oracle.com/javase/jp/8/docs/api/)
    protected Point position;

    public Avatar(Point p, String avatarImage) {
        position = p;
        String dirName = (getClass().getResource("./")).toString();
        image = new Image(dirName+avatarImage);
    }

    public int getPositionX() {
        return position.x;
    }

    public int getPositionY() {
        return position.y;
    }
}
