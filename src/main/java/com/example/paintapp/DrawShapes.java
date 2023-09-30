package com.example.paintapp;

import javafx.scene.canvas.GraphicsContext;
import java.awt.geom.Point2D;
import java.lang.Math.*;

import java.awt.*;

public abstract class DrawShapes {

    public static void DrawRectanlge(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = x2-x1;
        double h = y2-y1;
        javafx.geometry.Point2D point = new javafx.geometry.Point2D(x1, y1);
        gc.strokeRect(point.getX(), point.getY(), w, h);
    }
}
