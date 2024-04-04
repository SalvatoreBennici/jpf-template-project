package pcd.ass01.simengineseq_improved;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Base class for defining concrete simulations
 */
public abstract class AbstractSimulation {

    /* list of the agents */
    private final List<AbstractAgent> agents;
    /* simulation listeners */
    private final List<SimulationListener> listeners;
    private final List<SimulationObserver> observers;
    private final BoundedBuffer<Task> taskQueue;
    private final List<Worker> workerThreads;
    private final CountDownMonitor taskCountDown;
    /* environment of the simulation */
    private AbstractEnvironment env;
    /* logical time step */
    private int dt;
    /* initial logical time */
    private int t0;
    /* in the case of sync with wall-time */
    private boolean toBeInSyncWithWallTime;
    private int nStepsPerSec;
    /* for time statistics*/
    private long currentWallTime;
    private long startWallTime;
    private long endWallTime;
    private long averageTimePerStep;
    private boolean isRunning;


    protected AbstractSimulation() {
        agents = new ArrayList<>();
        listeners = new ArrayList<>();
        workerThreads = new ArrayList<>();
        taskQueue = new SimpleBoundedBuffer<>(5000);
        taskCountDown = new SimpleCountDownMonitor();
        observers = new ArrayList<>();
        toBeInSyncWithWallTime = false;
        isRunning = false;
    }

    public void stopSimulation() {
        this.isRunning = false;
    }


    private void createWorkers() {
        int numThreads = Runtime.getRuntime().availableProcessors();
        IntStream.range(0, numThreads)
                .mapToObj(i -> new SimpleWorker(taskQueue, taskCountDown))
                .peek(Worker::start)
                .forEach(workerThreads::add);

    }

    /**
     * Method used to configure the simulation, specifying env and agents
     */
    protected abstract void setup();

    /**
     * Method running the simulation for a number of steps,
     * using a sequential approach
     *
     * @param numSteps The number of steps to run the simulation for
     */
    public void run(int numSteps) {
        updateState(true);
        startWallTime = System.currentTimeMillis();

        /* initialize the env and the agents inside */
        int t = t0;

        env.init();
        agents.forEach(a -> a.init(env));

        this.notifyReset(t, agents, env);

        long timePerStep = 0;
        int nSteps = 0;

        createWorkers();

        while ((nSteps < numSteps) && isRunning) {
            currentWallTime = System.currentTimeMillis();

            /* make a step */
            env.step(dt);

            /* clean the submitted actions */
            env.cleanActions();

            /* ask each agent to make a step */
            taskCountDown.set(agents.size());
            for (AbstractAgent a : agents) {
                try {
                    taskQueue.put(() -> a.step(dt));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                taskCountDown.waitUntil(0);
            } catch (InterruptedException e) {
                // Interruption occurred, break out of the loop
                break;
            }

            t += dt;

            /* process actions submitted to the environment */
            env.processActions();

            notifyNewStep(t, agents, env);
            notifyObservers(isRunning);

            nSteps++;
            timePerStep += System.currentTimeMillis() - currentWallTime;

            if (toBeInSyncWithWallTime) {
                syncWithWallTime();
            }
        }
        updateState(false);
        endWallTime = System.currentTimeMillis();
        this.averageTimePerStep = timePerStep / numSteps;
        terminateAllWorkers();
        System.out.println("[SIMULATION] Simulation Ended in: " + (endWallTime - startWallTime) + " ms");
        System.out.println("with an average step of: " + averageTimePerStep + " ms");

    }


    private void terminateAllWorkers() {
        workerThreads.forEach(Worker::interrupt);
    }


    public long getSimulationDuration() {
        return endWallTime - startWallTime;
    }

    public long getAverageTimePerCycle() {
        return averageTimePerStep;
    }

    /* methods for configuring the simulation */

    protected void setupTimings(int t0, int dt) {
        this.dt = dt;
        this.t0 = t0;
    }

    protected void syncWithTime(int nCyclesPerSec) {
        this.toBeInSyncWithWallTime = true;
        this.nStepsPerSec = nCyclesPerSec;
    }

    protected void setupEnvironment(AbstractEnvironment env) {
        this.env = env;
    }

    protected void addAgent(AbstractAgent agent) {
        agents.add(agent);
    }

    /* methods for listeners */

    public void addSimulationListener(SimulationListener l) {
        this.listeners.add(l);
    }

    private void notifyReset(int t0, List<AbstractAgent> agents, AbstractEnvironment env) {
        listeners.forEach(l -> l.notifyInit(t0, agents, env));
    }

    private void notifyNewStep(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
        listeners.forEach(l -> l.notifyStepDone(t0, agents, env));
    }

    public void addSimulationObserver(SimulationObserver observer) {
        this.observers.add(observer);
        notifyObservers(isRunning);
    }

    private void updateState(boolean running) {
        this.isRunning = running;
        notifyObservers(isRunning);
    }

    private void notifyObservers(boolean running) {
        observers.forEach(observer -> observer.simulationUpdated(running));
    }

    /* method to sync with wall time at a specified step rate */

    private void syncWithWallTime() {
        try {
            long newWallTime = System.currentTimeMillis();
            long delay = 1000 / this.nStepsPerSec;
            long wallTimeDT = newWallTime - currentWallTime;
            if (wallTimeDT < delay) {
                Thread.sleep(delay - wallTimeDT);
            }
        } catch (Exception ignored) {
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
