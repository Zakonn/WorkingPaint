package com.example.paintapp;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;

import java.util.Stack;

public class UndoRedo {


// (stack > array) because stack follows LIFO principle.
    // contains history of undone images/canvas
    private final Stack<Image> undoHistory = new Stack<>();
    private final Stack<Image> redoHistory = new Stack<>();

    public void trackHistory(CanvasPanel panel) {
        Image undoneImage = getCanvasImage(panel.canvas);
        undoHistory.push(undoneImage);
        if (undoHistory.size() >= 20) undoHistory.remove(0);
        redoHistory.clear();
    }

    public void Undo(CanvasPanel panel) {
        // Saves image for future redo/redoHistory
        Image undoneImage = getCanvasImage(panel.canvas);
        redoHistory.push(undoneImage);
        if (redoHistory.size() >= 20) redoHistory.remove(0);

        // changes curr canvas --> prev canvas
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


    // set to static so it will be used by all instances of the class
    // Function returns canvas as an IMAGE type
    public static Image getCanvasImage(Canvas canvas) {
        SnapshotParameters parameters = new SnapshotParameters();
        // (getscale > getsize) scale allows me to resize/compress my image.
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
