package sample.multitrack.utils;

import org.opencv.tracking.*;

import static sample.multitrack.constant.TrackerConst.trackerTypes;

public class TrackerUtil {


    public static Tracker createTrackerByName(String trackerName) {
        if (trackerName.equals(trackerTypes.get(0))) {
            return TrackerBoosting.create();
        } else if (trackerName.equals(trackerTypes.get(1))) {
            return TrackerMIL.create();
        } else if (trackerName.equals(trackerTypes.get(2))) {
            return TrackerKCF.create();
        } else if (trackerName.equals(trackerTypes.get(3))) {
            return TrackerTLD.create();
        } else if (trackerName.equals(trackerTypes.get(4))) {
            return TrackerMedianFlow.create();
        } else if (trackerName.equals(trackerTypes.get(5))) {
            return TrackerGOTURN.create();
        } else if (trackerName.equals(trackerTypes.get(6))) {
            return TrackerMOSSE.create();
        } else if (trackerName.equals(trackerTypes.get(7))) {
            return TrackerCSRT.create();
        } else {
            System.out.println("no such tracker...");
        }
        return null;
    }
}
