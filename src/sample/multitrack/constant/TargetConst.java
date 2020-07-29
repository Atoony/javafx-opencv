package sample.multitrack.constant;

import org.opencv.core.Rect2d;

import java.util.Arrays;
import java.util.List;

public class TargetConst {
    public static final List<Rect2d> targetList = Arrays.asList(
            new Rect2d(318, 81, 26, 60),
            new Rect2d(213, 159, 36, 88),
            new Rect2d(463, 57, 30, 54),
            new Rect2d(380, 28, 23, 57),
            new Rect2d(344, 29, 23, 51),
            new Rect2d(386, 152, 23, 75));
}
