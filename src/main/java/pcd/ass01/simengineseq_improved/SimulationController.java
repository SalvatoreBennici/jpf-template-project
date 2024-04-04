package pcd.ass01.simengineseq_improved;

public class SimulationController {

    private final AbstractSimulation simulation;

    public SimulationController(AbstractSimulation simulation) {
        this.simulation = simulation;
    }

    public void startEvent(int numSteps) {
        try {
            new Thread(() -> {
                try {
                    if (!simulation.isRunning()) {
                        log("[Controller] Starting the simulation...");
                        simulation.run(numSteps);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopEvent() {
        try {
            new Thread(() -> {
                try {
                    if (simulation.isRunning()) {
                        log("Stopping the simulation...");
                        simulation.stopSimulation();
                        log("Stopping simulation done.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void log(String msg) {
        System.out.println("[ Controller ] " + msg);
    }
}