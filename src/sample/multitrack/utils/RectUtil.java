package sample.multitrack.utils;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;

public class RectUtil {
    public static Rect rect2d2Rect(Rect2d rect2d) {
        return new Rect(((Double) rect2d.x).intValue(),
                ((Double) rect2d.y).intValue(),
                ((Double) rect2d.width).intValue(),
                ((Double) rect2d.height).intValue());
    }

    public static Point getCenterPoint(Rect2d trackRect) {
        return new Point((trackRect.x + trackRect.width / 2),
                (trackRect.y + trackRect.height / 2));
    }
}
