package com.example.paintapp;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;

import java.util.Stack;

/**
 * Handles undo and redo for changes in a Canvas
 */
public class UndoRedo {

    /**
     * Stack of actions possible to undo
     */
    private final Stack<Image> undoHistory = new Stack<>();
    /**
     * Stack of actions possible to redo
     */
    private final Stack<Image> redoHistory = new Stack<>();

    /**
     * Called when needing to track a change in the canvas and pushes it onto undoHistory stack
     *
     * @param panel Canvas panel currently in
     */
    public void trackHistory(CanvasPanel panel) {
        Image undoneImage = getCanvasImage(panel.canvas);
        undoHistory.push(undoneImage);
        if (undoHistory.size() >= 20) undoHistory.remove(0);
        redoHistory.clear();
    }

    /**
     * Changes canvas to previous canvas
     *
     * @param panel current canvas
     */
    public void Undo(CanvasPanel panel) {
        Image undoneImage = getCanvasImage(panel.canvas);
        redoHistory.push(undoneImage);
        if (redoHistory.size() >= 20) redoHistory.remove(0);

        panel.canvas.getGraphicsContext2D().setEffect(null);
        panel.canvas.getGraphicsContext2D().clearRect(0,0, panel.canvas.getWidth(), panel.canvas.getHeight());
        Image prev;
        if (undoHistory.size() == 1) {
            prev = undoHistory.peek();
        } else {
            prev = undoHistory.pop();
        }

        double y = panel.root.getScaleY();
        double x = panel.root.getScaleX();
        panel.root.setScaleY(1);
        panel.root.setScaleX(1);
        panel.setSizeY(prev.getHeight());
        panel.setSizeX(prev.getWidth());
        panel.UpdateSize();
        panel.canvas.getGraphicsContext2D().drawImage(prev, 0, 0);
        panel.root.setScaleX(x);
        panel.root.setScaleY(y);
    }

    /**
     * Function that returns to a canvas state after undo function was called
     *
     * @param panel current canvas
     */
    public void Redo(CanvasPanel panel) {
        // changes curr canvas --> prev canvas
        panel.canvas.getGraphicsContext2D().setEffect(null);
        panel.canvas.getGraphicsContext2D().clearRect(0,0, panel.canvas.getWidth(), panel.canvas.getHeight());
        Image prev = redoHistory.pop();

        double y = panel.root.getScaleY();
        double x = panel.root.getScaleX();
        panel.root.setScaleY(1);
        panel.root.setScaleX(1);
        panel.setSizeY(prev.getHeight());
        panel.setSizeX(prev.getWidth());
        panel.UpdateSize();
        panel.canvas.getGraphicsContext2D().drawImage(prev, 0, 0);
        panel.root.setScaleX(x);
        panel.root.setScaleY(y);
        undoHistory.push(prev);
    }

    /**
     * Function that prepares and returns the current canvas to be saved for undo
     *
     * @param canvas canvas currently in
     * @return Unscaled javafx image of the canvas
     */
    public static Image getCanvasImage(Canvas canvas) {
        SnapshotParameters parameters = new SnapshotParameters();
        double y = canvas.getScaleY();
        double x = canvas.getScaleX();
        canvas.setScaleX(1);
        canvas.setScaleY(1);
        Image image = canvas.snapshot(parameters,null);
        canvas.setScaleX(x);
        canvas.setScaleY(y);
        return image;
    }

}
