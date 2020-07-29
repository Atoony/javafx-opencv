package sample.multitrack.utils;

import org.opencv.core.Rect2d;

import java.util.ArrayList;
import java.util.List;

import static sample.multitrack.constant.TargetConst.targetList;

public class TargetUtil {


    public static List<Rect2d> getTargetsByNumber(Integer targetNumber) throws Exception {
        List<Rect2d> result = new ArrayList<>();
        if (targetNumber < 1 || targetNumber > 6) {
            throw new Exception("目标数量超过限制范围，请选择 1-6 中的数字");
        }
        for (int i = 0; i < targetNumber; i++) {
            result.add(targetList.get(i));
        }
        return result;
    }

    public static List<Rect2d> getTargetsByNumber(String targetNumber) throws Exception {
        List<Rect2d> result = new ArrayList<>();
        Integer number = Integer.valueOf(targetNumber);
        if (number < 1 || number > 6) {
            throw new Exception("目标数量超过限制范围，请选择 1-6 中的数字");
        }
        for (int i = 0; i < number; i++) {
            result.add(targetList.get(i));
        }
        return result;
    }
}
