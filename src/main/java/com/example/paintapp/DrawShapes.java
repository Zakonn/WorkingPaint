package com.example.paintapp;

import javafx.scene.canvas.GraphicsContext;
import java.awt.geom.Point2D;
import java.lang.Math.*;

import java.awt.*;

import static java.lang.Math.*;

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
    public static void DrawRectangle(double x1, double y1, double x2, double y2, GraphicsContext gc) {

        double w = x2-x1;
        double h = y2-y1;
        javafx.geometry.Point2D point = getCorner(x1,y1,x2,y2);
        gc.strokeRect(point.getX(), point.getY(), w, h);
    }
    /**
     * Draws a Square in the desired direction based on the cursor location
     *
     * @param x1   start x cord
     * @param y1   start y cord
     * @param x2   mouse x cord
     * @param y2   mouse y cord
     * @param gc   the graphics content to draw the shape on
     */
    public static void DrawSquare(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        if (w > h) h = w;
        else w = h;
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeRect(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //draw up & right
                gc.strokeRect(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeRect(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeRect(x2, y2, w, h);
            }
        }
    }
    /**
     * Draws an oval in the desired direction based on the cursor location
     *
     * @param x1   start x cord
     * @param y1   start y cord
     * @param x2   mouse x cord
     * @param y2   mouse y cord
     * @param gc   the graphics content to draw the shape on
     */
    public static void DrawOval(double x1, double y1, double x2, double y2, GraphicsContext gc) {
        double w = abs(x2 - x1);
        double h = abs(y2 - y1);
        {
            if (x2 >= x1 && y2 >= y1) {         //draw down & right
                gc.strokeOval(x1, y1, w, h);
            } else if (x2 >= x1 && y1 >= y2) {  //draw up & right
                gc.strokeOval(x1, y2, w, h);
            } else if (x1 >= x2 && y2 >= y1) {  //draw down & left
                gc.strokeOval(x2, y1, w, h);
            } else {                            //draw up & left
                gc.strokeOval(x2, y2, w, h);
            }
        }
    }
    /**
     * Draws a Polygon in the desired direction based on the cursor location
     *
     * @param x1    start x cord
     * @param y1    start y cord
     * @param x2    mouse x cord
     * @param y2    mouse y cord
     * @param sides number of sides polygon should have
     * @param gc    the graphics content to draw the shape on
     */
    public static void DrawPoly(double x1, double y1, double x2, double y2, int sides, GraphicsContext gc) {
        javafx.geometry.Point2D center = new javafx.geometry.Point2D((x1 + x2) / 2, (y1 + y2) / 2);
        double dist = Point2D.distance(x1, y1, x2, y2) / 2;
        double start = Math.atan2(y2 - y1, x2 - x1);
        double[] xPoints = new double[sides];
        double[] yPoints = new double[sides];

        for (int i = 0; i <= sides - 1; i++) {
            xPoints[i] = center.getX() + (dist * cos((2 * PI * i) / sides + start));
            yPoints[i] = center.getY() + (dist * sin((2 * PI * i) / sides + start));
        }
        gc.strokePolygon(xPoints, yPoints, sides);

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
