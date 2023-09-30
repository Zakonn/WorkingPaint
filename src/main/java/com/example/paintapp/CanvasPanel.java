package com.example.paintapp;

import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.example.paintapp.PaintController.currentColor;
import static com.example.paintapp.PaintController.currentLineWidth;

public class CanvasPanel {

    // Panel name
    public final String Name;
    // Instance of controller class
    private final PaintController controller;
    private final TabPane parent;
    private final Double CWIDTH = 1024d;
    private final Double CHEIGHT = 1024d;

    public StackPane root = new StackPane();
    public Canvas canvas = new Canvas();
    public ScrollPane scrollPane;
    //
    // A ghost canvas that will reveal what changes will show what will be drawn before they are on the real canvas.
    //
    public Canvas ghostCanvas = new Canvas();
    private final GraphicsContext ghostGC = ghostCanvas.getGraphicsContext2D();
    //
    // Top pane of actual canvas
    public AnchorPane pane = new AnchorPane();
    private Group ScrollContent;
    private Point2D mouseClick;
    private Point2D mouseFollow;




    public CanvasPanel(TabPane tPane, String name, PaintController controllerIn) {
        parent = tPane;
        controller = controllerIn;
        Name = name;
        Create(name);
    }

    public void Create(String name) {
        ghostCanvas.setHeight(CHEIGHT);
        ghostCanvas.setWidth(CWIDTH);
        canvas.setHeight(CHEIGHT);
        canvas.setWidth(CWIDTH);
        canvas.getGraphicsContext2D().setImageSmoothing(true);

        AnchorPane canvasPane = new AnchorPane(canvas);
        AnchorPane sCPane = new AnchorPane(ghostCanvas);

        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);

        AnchorPane.setLeftAnchor(ghostCanvas, 0.0);
        AnchorPane.setRightAnchor(ghostCanvas, 0.0);
        AnchorPane.setTopAnchor(ghostCanvas, 0.0);
        AnchorPane.setBottomAnchor(ghostCanvas, 0.0);

        root.setPrefWidth(CWIDTH);
        root.setPrefHeight(CHEIGHT);
        root.getChildren().add(canvasPane);
        root.getChildren().add(sCPane);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        pane.getChildren().add(root);
        pane.setPrefHeight(CHEIGHT);
        pane.setPrefWidth(CWIDTH);
        pane.getStyleClass().add("border");


        ScrollContent = new Group(pane);
        scrollPane = new ScrollPane(ScrollContent);
        scrollPane.setPrefViewportWidth(CWIDTH);
        scrollPane.setPrefViewportHeight(CHEIGHT);
        Tab tab = new Tab(name, scrollPane);
        parent.getTabs().add(tab);
        AnchorPane.setBottomAnchor(scrollPane, (double) 0);
        AnchorPane.setTopAnchor(scrollPane, (double) 0);
        AnchorPane.setRightAnchor(scrollPane, (double) 0);
        AnchorPane.setLeftAnchor(scrollPane, (double) 0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        pane.setOnMouseDragged(this::onMouseDragged);
        pane.setOnMousePressed(this::onMousePressed);


        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        controller.tabs.getSelectionModel().select(tab);

    }
    private void onMousePressed(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Color color = currentColor;
        double width = currentLineWidth;
        gc.setStroke(color);
        gc.setLineWidth(width);
        mouseClick = new Point2D(event.getX(), event.getY());
        if (event.getButton() == MouseButton.PRIMARY) {
            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();

        }
    }
    private void onMouseDragged(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Color color = currentColor;
        double size = currentLineWidth;
        gc.setStroke(color);
        gc.setLineWidth(size);
        mouseFollow = new Point2D(event.getX(), event.getY());
        if (event.getButton() == MouseButton.PRIMARY) {
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
        }
    }

    public void setSizeX(double x) {
        canvas.setWidth(x);
        ghostCanvas.setWidth(x);
        pane.setPrefWidth(x);
        root.setPrefWidth(x);
    }
    public void setSizeY(double y) {
        canvas.setHeight(y);
        ghostCanvas.setHeight(y);
        root.setPrefHeight(y);
        pane.setPrefHeight(y);
    }

}
