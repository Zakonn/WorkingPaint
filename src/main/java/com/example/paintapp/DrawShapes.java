package com.example.paintapp;

import javafx.scene.canvas.GraphicsContext;
import java.awt.geom.Point2D;
import java.lang.Math.*;

import java.awt.*;

public abstract class DrawShapes {

    public static void DrawRectanlge(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = x2-x1;
        double h = y2-y1;
        javafx.geometry.Point2D point = getCorner(x1,y1,x2,y2);
        gc.strokeRect(point.getX(), point.getY(), w, h);
    }
    private static javafx.geometry.Point2D getCorner(double x1, double y1, double x2, double y2) {
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
