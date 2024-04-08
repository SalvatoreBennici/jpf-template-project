package pcd.ass01.simtrafficbase_improved;

import pcd.ass01.simengineconc_improved.Action;

/**
 * Car agent move forward action
 */
public class MoveForward implements Action {
    private final String agentId;
    private final double distance;

    public MoveForward(String agentId, double distance) {
        this.agentId = agentId;
        this.distance = distance;
    }

    public String getAgentId() {
        return agentId;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "MoveForward(agentId=" + agentId + ", distance=" + distance + ")";
    }
}
