package pcd.ass01.simengineconc_improved;

public class SimulationController {

    private final AbstractSimulation simulation;
    private final FlagMonitor isRunning;

    public SimulationController(AbstractSimulation simulation) {
        this.simulation = simulation;
        this.isRunning = simulation.getFlag();
    }

    private static void log(String msg) {
        System.out.println("[Controller] " + msg);
    }

    public void startEvent(int numSteps) {
        try {
            new Thread(() -> {
                try {
                    if (!isRunning.getFlag()) {
                        log("Starting the simulation...");
                        simulation.run(numSteps);
                        log("Simulation ended");
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
                    if (isRunning.getFlag()) {
                        log("Stopping the simulation...");
                        isRunning.setFlag(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}