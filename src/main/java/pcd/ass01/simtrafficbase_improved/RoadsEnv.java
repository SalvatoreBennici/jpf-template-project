package pcd.ass01.simtrafficbase_improved;

import pcd.ass01.simengineconc_improved.AbstractEnvironment;
import pcd.ass01.simengineconc_improved.Action;
import pcd.ass01.simengineconc_improved.Percept;

import java.util.*;
import java.util.stream.Collectors;

public class RoadsEnv extends AbstractEnvironment {

    private static final int MIN_DIST_ALLOWED = 5;
    private static final int CAR_DETECTION_RANGE = 30;
    private static final int SEM_DETECTION_RANGE = 30;

    /* list of roads */
    private final List<Road> roads;

    /* traffic lights */
    private final List<TrafficLight> trafficLights;

    /* cars situated in the environment */
    private final HashMap<String, CarAgentInfo> registeredCars;


    public RoadsEnv() {
        super("traffic-env");
        registeredCars = new HashMap<>();
        trafficLights = new ArrayList<>();
        roads = new ArrayList<>();
    }

    @Override
    public void init() {
        trafficLights.forEach(TrafficLight::init);
    }

    @Override
    public void step(int dt) {
        trafficLights.forEach(tl -> tl.step(dt));
    }

    public void registerNewCar(CarAgent car, Road road, double pos) {
        registeredCars.put(car.getId(), new CarAgentInfo(car, road, pos));
    }

    public Road createRoad(P2d p0, P2d p1) {
        Road r = new Road(p0, p1);
        this.roads.add(r);
        return r;
    }

    public TrafficLight createTrafficLight(P2d pos, TrafficLight.TrafficLightState initialState, int greenDuration, int yellowDuration, int redDuration) {
        TrafficLight tl = new TrafficLight(pos, initialState, greenDuration, yellowDuration, redDuration);
        this.trafficLights.add(tl);
        return tl;
    }

    @Override
    public Percept getCurrentPercepts(String agentId) {

        CarAgentInfo carInfo = registeredCars.get(agentId);
        double pos = carInfo.getPos();
        Road road = carInfo.getRoad();
        Optional<CarAgentInfo> nearestCar = getNearestCarInFront(road, pos, CAR_DETECTION_RANGE);
        Optional<TrafficLightInfo> nearestSem = getNearestSemaphoreInFront(road, pos, SEM_DETECTION_RANGE);

        return new CarPercept(pos, nearestCar, nearestSem);
    }

    private Optional<CarAgentInfo> getNearestCarInFront(Road road, double carPos, double range) {
        CarAgentInfo nearestCar = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, CarAgentInfo> entry : registeredCars.entrySet()) {
            CarAgentInfo carInfo = entry.getValue();
            if (carInfo.getRoad() != road) {
                continue;
            }
            double dist = carInfo.getPos() - carPos;

            if (dist > 0 && dist <= range && dist < minDistance) {
                nearestCar = carInfo;
                minDistance = dist;
            }
        }
        return Optional.ofNullable(nearestCar);
    }


    private Optional<TrafficLightInfo> getNearestSemaphoreInFront(Road road, double carPos, double range) {
        List<TrafficLightInfo> trafficLights = road.getTrafficLights();
        Optional<TrafficLightInfo> nearestTrafficLight = Optional.empty();
        double minDistance = Double.POSITIVE_INFINITY;

        for (TrafficLightInfo tl : trafficLights) {
            if (tl.getRoadPos() > carPos) {
                double distance = tl.getRoadPos() - carPos;
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestTrafficLight = Optional.of(tl);
                }
            }
        }

        return nearestTrafficLight;
    }


    @Override
    public void processActions() {
        for (Action act : submittedActions) {
            if (act instanceof MoveForward) {
                MoveForward mv = (MoveForward) act;
                CarAgentInfo info = registeredCars.get(mv.getAgentId());
                Road road = info.getRoad();
                Optional<CarAgentInfo> nearestCar = getNearestCarInFront(road, info.getPos(), CAR_DETECTION_RANGE);

                if (nearestCar.isPresent()) {
                    double dist = nearestCar.get().getPos() - info.getPos();
                    if (dist > mv.getDistance() + MIN_DIST_ALLOWED) {
                        info.updatePos(info.getPos() + mv.getDistance());
                    }
                } else {
                    info.updatePos(info.getPos() + mv.getDistance());
                }

                if (info.getPos() > road.getLen()) {
                    info.updatePos(0);
                }
            }
        }
    }


    public List<CarAgentInfo> getAgentInfo() {
        return this.registeredCars.entrySet().stream().map(el -> el.getValue()).collect(Collectors.toList());
    }

    public List<Road> getRoads() {
        return roads;
    }

    public List<TrafficLight> getTrafficLights() {
        return trafficLights;
    }
}
