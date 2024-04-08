package pcd.ass01.simengineconc_improved;

public class FlagMonitor {
    private boolean flag;

    FlagMonitor() {
        flag = false;
    }

    public synchronized boolean getFlag() {
        return flag;
    }

    public synchronized void setFlag(boolean flag) {
        this.flag = flag;
    }


}
