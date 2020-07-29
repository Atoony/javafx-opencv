package sample.multitrack;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.tracking.MultiTracker;
import org.opencv.tracking.Tracker;
import org.opencv.videoio.VideoCapture;
import sample.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sample.multitrack.constant.TrackerConst;
import sample.multitrack.constant.TargetConst;
import sample.multitrack.utils.RectUtil;
import sample.multitrack.utils.TargetUtil;
import sample.multitrack.utils.TrackerUtil;
import sample.multitrack.utils.VideoUtil;

public class MultiTrackController {

    @FXML
    private Button startBtn;
    @FXML
    private ImageView originalFrame;
    @FXML
    private ChoiceBox<String> trackerTypeBox;
    @FXML
    private ChoiceBox<String> targetNumberBox;
    @FXML
    private Label fps;

    MultiTracker multiTracker = MultiTracker.create();
    List<Rect2d> initTrackRectList = new ArrayList<>();


    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that realizes the video capture
    private VideoCapture capture = new VideoCapture();
    // a flag to change the button behavior
    private boolean cameraActive = false;
    // the id of the camera to be used
    private static int cameraId = 0;

    private ObjectProperty<String> fpsValuesProp;
    private Integer frameCount = 0;
    private Double totalFPS = 0.0D;

    private static String videoPath = "D:\\video\\16run\\16run.mp4";

    /**
     * The action triggered by pushing the button on the GUI
     *
     * @param event
     *            the push button event
     */
    @FXML
    protected void start(ActionEvent event) throws Exception {
        // bind a text property with the string containing the current range of
        // fps and average fps
        fpsValuesProp = new SimpleObjectProperty<>();
        this.fps.textProperty().bind(fpsValuesProp);

        if (!this.cameraActive)
        {
            this.frameCount = 0;
            this.totalFPS = 0.0D;
            // start the video capture
            this.capture.open(videoPath);

            // is the video stream available?
            if (this.capture.isOpened())
            {
                this.cameraActive = true;

                // initial tracker
                this.initTrackRectList = TargetUtil.getTargetsByNumber(this.targetNumberBox.getValue());

                Mat firstFrameMat = VideoUtil.getFirstFrame(this.capture);

                for (int i = 0; i < this.initTrackRectList.size(); i++) {
                    Rect2d trackRect = this.initTrackRectList.get(i);
                    Tracker tracker = TrackerUtil.createTrackerByName(this.trackerTypeBox.getValue());
                    this.multiTracker.add(tracker, firstFrameMat, trackRect);
                }

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run()
                    {
                        // effectively grab and process a single frame
                        Mat frame = grabFrame();
                        // convert and show the frame
                        Image imageToShow = Utils.mat2Image(frame);
                        updateImageView(originalFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
                this.startBtn.setText("停止运行");
            }
            else
            {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        }
        else
        {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.startBtn.setText("开始运行");
            // stop the timer
            this.stopAcquisition();

            this.multiTracker = MultiTracker.create();
            this.initTrackRectList = new ArrayList<>();
        }
    }



    @FXML
    protected void typeSelect(ActionEvent event){
        System.out.println(trackerTypeBox.getValue());
    }

    @FXML
    protected void numberSelect(ActionEvent event){
        System.out.println(targetNumberBox.getValue());
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Mat} to show
     */
    private Mat grabFrame()
    {
        ++frameCount;

        // init everything
        Mat frame = new Mat();

        long start = Core.getTickCount();

        // check if the capture is open
        if (this.capture.isOpened())
        {
            try
            {
                // read the current frame
                this.capture.read(frame);
                // if the frame is not empty, process it
                if (!frame.empty()) {
                    // tracking process
                    this.multiTracking(frame);
                }
            }
            catch (Exception e)
            {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }
        }

        double FPS = Core.getTickFrequency() / (Core.getTickCount() - start);
        totalFPS += FPS;
        String valuesToPrint = "当前处理速度："+FPS+" fps"+"  平均处理速度："+(totalFPS / frameCount)+" fps";
        System.out.println(valuesToPrint);
        Utils.onFXThread(this.fpsValuesProp, valuesToPrint);
        return frame;
    }

    private void multiTracking(Mat frame) {
        MatOfRect2d matOfRect2d = new MatOfRect2d();
        matOfRect2d.fromList(initTrackRectList);
        boolean ok = multiTracker.update(frame, new MatOfRect2d(matOfRect2d));
        if (!ok) {
            System.out.println("tracking fail");
        }

        Rect2d[] rect2ds = multiTracker.getObjects().toArray();
        for (int i = 0; i < rect2ds.length; i++) {
            // 绘制跟踪矩形
            Imgproc.rectangle(frame, RectUtil.rect2d2Rect(rect2ds[i]), new Scalar(0, 0, 255));
        }
    }

    /**
     * Stop the acquisition from the camera and release all the resources
     */
    private void stopAcquisition()
    {
        if (this.timer!=null && !this.timer.isShutdown())
        {
            try
            {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened())
        {
            // release the camera
            this.capture.release();
        }
    }

    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view
     *            the {@link ImageView} to update
     * @param image
     *            the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }

    /**
     * On application close, stop the acquisition from the camera
     */
    protected void setClosed()
    {
        this.stopAcquisition();
    }

}
