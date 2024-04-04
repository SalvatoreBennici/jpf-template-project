package pcd.ass01.simtrafficbase_improved;

/**
 * Information about a traffic light
 */
public class TrafficLightInfo {
    private final TrafficLight sem;
    private final Road road;
    private final double roadPos;

    public TrafficLightInfo(TrafficLight sem, Road road, double roadPos) {
        this.sem = sem;
        this.road = road;
        this.roadPos = roadPos;
    }

    public TrafficLight getSem() {
        return sem;
    }

    public Road getRoad() {
        return road;
    }

    public double getRoadPos() {
        return roadPos;
    }

    @Override
    public String toString() {
        return "TrafficLightInfo{" +
                "sem=" + sem +
                ", road=" + road +
                ", roadPos=" + roadPos +
                '}';
    }
}
