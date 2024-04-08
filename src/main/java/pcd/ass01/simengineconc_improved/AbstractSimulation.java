package pcd.ass01.simengineconc_improved;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Base class for defining concrete simulations
 */
public abstract class AbstractSimulation {

    public static final int BUFFER_SIZE = 5000;
    /* data structure for the simulation */
    private final List<AbstractAgent> agents;
    private AbstractEnvironment env;
    /* simulation listeners */
    private final List<SimulationListener> listeners;
    private final List<SimulationObserver> observers;
    /* data structure for concurrent version */
    private final List<Worker> workerThreads;
    /* data structure for sync */
    private final BoundedBuffer<Task> taskQueue;
    private final CountDownMonitor taskCountDown;
    private final FlagMonitor isRunning;

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

    protected AbstractSimulation() {
        agents = new ArrayList<>();
        listeners = new ArrayList<>();
        workerThreads = new ArrayList<>();
        taskQueue = new SimpleBoundedBuffer<>(BUFFER_SIZE);
        taskCountDown = new SimpleCountDownMonitor();
        observers = new ArrayList<>();
        toBeInSyncWithWallTime = false;
        isRunning = new FlagMonitor();
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
        startWallTime = System.currentTimeMillis();

        /* initialize the env and the agents inside */
        int t = t0;

        env.init();
        agents.forEach(a -> a.init(env));

        this.notifyReset(t, agents, env);
        long timePerStep = 0;
        int nSteps = 0;

        createWorkers();
        updateState(true);
        while ((nSteps < numSteps) && isRunning.getFlag()) {
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

            /* wait until each agent has taken a step */
            try {
                taskCountDown.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            t += dt;
            /* process actions submitted to the environment */
            env.processActions();

            notifyNewStep(t, agents, env);
            notifyObservers(isRunning.getFlag());

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
        listeners.forEach(l -> l.notifyStepDone(t, agents, env));
    }

    public void addSimulationObserver(SimulationObserver observer) {
        this.observers.add(observer);
        notifyObservers(isRunning.getFlag());
    }

    private void updateState(boolean running) {
        isRunning.setFlag(running);
        notifyObservers(isRunning.getFlag());
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

    public FlagMonitor getFlag() {
        return isRunning;
    }
}
