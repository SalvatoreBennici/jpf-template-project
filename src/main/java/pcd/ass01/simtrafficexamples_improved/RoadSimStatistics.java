package pcd.ass01.simtrafficexamples_improved;

import pcd.ass01.simengineseq_improved.AbstractAgent;
import pcd.ass01.simengineseq_improved.AbstractEnvironment;
import pcd.ass01.simengineseq_improved.SimulationListener;
import pcd.ass01.simtrafficbase_improved.CarAgent;

import java.util.List;

/**
 * Simple class keeping track of some statistics about a traffic simulation
 * - average number of cars
 * - min speed
 * - max speed
 */
public class RoadSimStatistics implements SimulationListener {

	private double averageSpeed;
	private double minSpeed;
	private double maxSpeed;

	public RoadSimStatistics() {
	}

	@Override
	public void notifyInit(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		averageSpeed = 0;
	}

	@Override
	public void notifyStepDone(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		double totalSpeed = 0;
		maxSpeed = -1;
		minSpeed = Double.MAX_VALUE;

		for (AbstractAgent agent : agents) {
			CarAgent car = (CarAgent) agent;
			double currentSpeed = car.getCurrentSpeed();
			totalSpeed += currentSpeed;
			if (currentSpeed > maxSpeed) {
				maxSpeed = currentSpeed;
			}
			if (currentSpeed < minSpeed) {
				minSpeed = currentSpeed;
			}
		}

		if (!agents.isEmpty()) {
			averageSpeed = totalSpeed / agents.size();
		}

		log("Average speed: " + averageSpeed);
		log("Min speed: " + minSpeed);
		log("Max speed: " + maxSpeed);
	}

	public double getAverageSpeed() {
		return averageSpeed;
	}

	public double getMinSpeed() {
		return minSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	private void log(String msg) {
		System.out.println("[STAT] " + msg);
	}
}
