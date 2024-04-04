package pcd.ass01.simengineseq_improved;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationView extends JFrame implements ActionListener, SimulationObserver {

    private final SimulationController controller;
    private final JTextField numStepsField;
    private final JButton startButton;
    private final JButton stopButton;

    public SimulationView(SimulationController controller) {
        super("Simulation Control Panel");

        this.controller = controller;

        setSize(400, 100);
        setResizable(false);

        startButton = new JButton("Start");
        startButton.addActionListener(this);

        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);

        numStepsField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);
        panel.add(numStepsField);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void stopButton() {
        try {
            controller.stopEvent();
        } catch (Exception ex) {
        }
    }

    private void startButton() {
        try {
            int steps = Integer.parseInt(numStepsField.getText());
            if (checkSteps(steps)) {
                controller.startEvent(steps);
            }
        } catch (Exception ex) {
        }
    }

    private boolean checkSteps(int steps) {
        return steps > 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Start":
                this.startButton();
                break;
            case "Stop":
                this.stopButton();
                break;
            default:
        }
    }

    @Override
    public void simulationUpdated(boolean isRunning) {
        startButton.setEnabled(!isRunning);
        stopButton.setEnabled(isRunning);
    }
}
