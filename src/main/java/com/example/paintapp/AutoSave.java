package com.example.paintapp;

import javafx.application.Platform;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Threading of a timer that saves all each time timer is done
 */
public class AutoSave extends TimerTask {

    final int length;

    final PaintController controller;

    public int time;

    /**
     * Constructor for AutoSave class
     *
     * @param n Time between saves
     * @param main the controller that owns the thread
     */
    public AutoSave(int n, PaintController main) {
        length = n;
        controller = main;
        time = length;
    }

    /**
     * The tasks the thread runs. It counts down to 0 and resets.
     * At 0 it will save the files.
     */
    @Override
    public void run() {
        Platform.runLater(() -> {
            String result;
            int S = time % 60;
            int H = time / 60;
            int M = H % 60;
            if (M>0) {
                result = "Next autosave in " + M + " Min";
            } else {
                result = "Next autosave in " + S + " seconds";
            }
            controller.timerLabel.setText(result);
            controller.timerLabel.setVisible(controller.autoSaveSwitch.isSelected());
        });
        if (controller.autoSaveSwitch.isSelected()) time--;

        if (time == 0) {
            controller.Save(controller.getCanvas().getName());
            Platform.runLater(() -> time = length);
        }

    }
}
