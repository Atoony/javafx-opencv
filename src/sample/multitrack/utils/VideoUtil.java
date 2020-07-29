package sample.multitrack.utils;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class VideoUtil {
    public static Mat getFirstFrame(VideoCapture capture) {
        Mat frame = new Mat();
        boolean have = capture.read(frame);
        if (!have) {
            System.out.println("occur error when read the first frame...");
            return null;
        }
        if (!frame.empty()) {
            return frame;
        }
        return null;
    }
}
