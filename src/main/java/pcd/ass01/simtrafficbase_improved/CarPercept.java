package pcd.ass01.simtrafficbase_improved;

import pcd.ass01.simengineconc_improved.Percept;

import java.util.Optional;

/**
 * Percept for Car Agents
 * <p>
 * - position on the road
 * - nearest car, if present (distance)
 * - nearest semaphore, if present (distance)
 */
public class CarPercept implements Percept {
    private final double roadPos;
    private final Optional<CarAgentInfo> nearestCarInFront;
    private final Optional<TrafficLightInfo> nearestSem;

    public CarPercept(double roadPos, Optional<CarAgentInfo> nearestCarInFront, Optional<TrafficLightInfo> nearestSem) {
        this.roadPos = roadPos;
        this.nearestCarInFront = nearestCarInFront;
        this.nearestSem = nearestSem;
    }

    public double getRoadPos() {
        return roadPos;
    }

    public Optional<CarAgentInfo> getNearestCarInFront() {
        return nearestCarInFront;
    }

    public Optional<TrafficLightInfo> getNearestSem() {
        return nearestSem;
    }

    @Override
    public String toString() {
        return "CarPercept{" +
                "roadPos=" + roadPos +
                ", nearestCarInFront=" + nearestCarInFront +
                ", nearestSem=" + nearestSem +
                '}';
    }
}
