package pcd.ass01.simtrafficexamples_improved;


public class RunTrafficSimulationJPF {

    public static void main(String[] args) {
        TrafficSimulationSingleRoadTwoCars simulation = new TrafficSimulationSingleRoadTwoCars();
//        TrafficSimulationSingleRoadSeveralCars simulation = new TrafficSimulationSingleRoadSeveralCars();
//        TrafficSimulationSingleRoadWithTrafficLightTwoCars simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();
//        TrafficSimulationWithCrossRoads simulation = new TrafficSimulationWithCrossRoads();
//        TrafficSimulationSingleRoadMassiveNumberOfCars simulation = new TrafficSimulationSingleRoadMassiveNumberOfCars(5000);

        simulation.setup();
        simulation.run(2);
    }
}
