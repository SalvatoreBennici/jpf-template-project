package pcd.ass01.simtrafficexamples_improved;

import pcd.ass01.simengineseq_improved.SimulationController;
import pcd.ass01.simengineseq_improved.SimulationView;


/**
 * Main class to create and run a simulation
 */
public class RunTrafficSimulation {

    public static void main(String[] args) {

        // var simulation = new TrafficSimulationSingleRoadTwoCars();
        // var simulation = new TrafficSimulationSingleRoadSeveralCars();
        TrafficSimulationSingleRoadWithTrafficLightTwoCars simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();
        // var simulation = new TrafficSimulationSingleRoadMassiveNumberOfCars(5000);
        simulation.setup();
        simulation.run(100);

        //RoadSimStatistics stat = new RoadSimStatistics();
        //RoadSimView view = new RoadSimView();
        //view.display();

        //simulation.addSimulationListener(stat);
        //simulation.addSimulationListener(view);


       // SimulationController controller = new SimulationController(simulation);
        //SimulationView simulationView = new SimulationView(controller);
       // simulation.addSimulationObserver(simulationView);


       // simulationView.setVisible(true);
    }
}
