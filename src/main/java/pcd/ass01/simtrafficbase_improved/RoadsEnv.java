package pcd.ass01.simtrafficbase_improved;

import pcd.ass01.simengineseq_improved.AbstractEnvironment;
import pcd.ass01.simengineseq_improved.Action;
import pcd.ass01.simengineseq_improved.Percept;

import java.util.*;
import java.util.stream.Collectors;

public class RoadsEnv extends AbstractEnvironment {

	private static final int MIN_DIST_ALLOWED = 5;
	private static final int CAR_DETECTION_RANGE = 30;
	private static final int SEM_DETECTION_RANGE = 30;

	private List<Road> roads;
	private List<TrafficLight> trafficLights;
	private Map<String, CarAgentInfo> registeredCars;

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
		roads.add(r);
		return r;
	}

	public TrafficLight createTrafficLight(P2d pos, TrafficLight.TrafficLightState initialState, int greenDuration, int yellowDuration, int redDuration) {
		TrafficLight tl = new TrafficLight(pos, initialState, greenDuration, yellowDuration, redDuration);
		trafficLights.add(tl);
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
		return registeredCars.values()
				.stream()
				.filter(carInfo -> carInfo.getRoad() == road)
				.filter(carInfo -> {
					double dist = carInfo.getPos() - carPos;
					return dist > 0 && dist <= range;
				})
				.min(Comparator.comparingDouble(c -> c.getPos() - carPos));
	}

	private Optional<TrafficLightInfo> getNearestSemaphoreInFront(Road road, double carPos, double range) {
		return road.getTrafficLights()
				.stream()
				.filter(tl -> tl.getRoadPos() > carPos)
				.min(Comparator.comparingDouble(tl -> tl.getRoadPos() - carPos));
	}

	@Override
	public void processActions() {
		for (Action act : submittedActions) {
			if (act.getClass().equals(MoveForward.class)) {
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
		return new ArrayList<>(registeredCars.values());
	}

	public List<Road> getRoads() {
		return new ArrayList<>(roads);
	}

	public List<TrafficLight> getTrafficLights() {
		return new ArrayList<>(trafficLights);
	}
}
