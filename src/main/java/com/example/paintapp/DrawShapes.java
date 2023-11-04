package com.example.paintapp;

import javafx.scene.canvas.GraphicsContext;
import java.awt.geom.Point2D;
import java.lang.Math.*;

import java.awt.*;

/**
 * Class to handle the creation of shapes
 */
public abstract class DrawShapes {

    /**
     * Creates Rectangle shape
     * @param x1        x position of mouse click
     * @param y1        y position of mouse click
     * @param x2        x position of mouse release
     * @param y2        y position of mouse release
     * @param gc        fetches current graphics context
     */
    public static void DrawRectanlge(double x1, double y1, double x2, double y2, GraphicsContext gc) {

        double w = x2-x1;
        double h = y2-y1;
        javafx.geometry.Point2D point = getCorner(x1,y1,x2,y2);
        gc.strokeRect(point.getX(), point.getY(), w, h);
    }

    /**
     * Handles where the corner of selected area starts
     * @param x1        x position of mouse click
     * @param y1        y position of mouse click
     * @param x2        x position of mouse release
     * @param y2        y position of mouse release
     * @return
     */
    public static javafx.geometry.Point2D getCorner(double x1, double y1, double x2, double y2) {
        if (x2 >= x1 && y2 >= y1) {
            return new javafx.geometry.Point2D(x1, y1);
        } else if (x2 >= x1) {
            return new javafx.geometry.Point2D(x1, y2);
        } else if (y2 >= y1) {
            return new javafx.geometry.Point2D(x2, y1);
        } else {
            return new javafx.geometry.Point2D(x2, y2);
        }
    }
}
