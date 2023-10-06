package com.example.paintapp;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


public class CanvasPanel {

    // Panel name
    public final String Name;
    // Instance of controller class
    private final PaintController controller;
    private final TabPane parent;
    private final Double CWIDTH = 1024d;
    private final Double CHEIGHT = 1024d;
    private boolean insideCanvas = false;

    public StackPane root = new StackPane();
    public Canvas canvas = new Canvas();
    public ScrollPane scrollPane;
    //
    // A ghost canvas that will reveal what changes will show what will be drawn before they are on the real canvas.
    //
    public Canvas ghostCanvas = new Canvas();
    private final GraphicsContext ghostGC = ghostCanvas.getGraphicsContext2D();
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    //
    // Top pane of actual canvas
    public AnchorPane pane = new AnchorPane();
    private Group ScrollContent;

    public UndoRedo undoRedo;

    private Point2D mouseClick;
    private Point2D mouseFollow;
    private Image s;
    private double s1, s2, s3, s4;


    public CanvasPanel(TabPane tPane, String name, PaintController controllerIn) {
        parent = tPane;
        controller = controllerIn;
        Name = name;
        Create(name);
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void Create(String name) {
        ghostCanvas.setHeight(CHEIGHT);
        ghostCanvas.setWidth(CWIDTH);
        canvas.setHeight(CHEIGHT);
        canvas.setWidth(CWIDTH);
        gc.setImageSmoothing(true);

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
        UpdateSize();
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
        pane.setOnMousePressed(this::onMouseEnter);
        pane.setOnMousePressed(this::onMouseExit);


        undoRedo = new UndoRedo();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        controller.tabs.getSelectionModel().select(tab);

    }

    private void onMouseEnter(MouseEvent event) {
        insideCanvas = true;
    }

    private void onMouseExit(MouseEvent event) {
        insideCanvas = false;
    }

    private void onMousePressed(MouseEvent event) {
        if (controller.pencil) {
            mouseClick = new Point2D(event.getX(), event.getY());
            if (event.getButton() == MouseButton.PRIMARY) {
                undoRedo.trackHistory(this);
                gc.beginPath();
                gc.moveTo(event.getX(), event.getY());
                gc.stroke();
            }
        }
    }

    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (controller.snip) {
                double x1, y1, x2, y2;
                x1 = mouseClick.getX();
                y1 = mouseClick.getY();
                x2 = event.getX();
                y2 = event.getY();
                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                double w = x2 - x1;
                double h = y2 - y1;
                if (x2 >= x1 && y2 >= y1) {                     //draw down & right
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x1, y1);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                } else if (x2 >= x1) {                          //drawing up & right
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x1, y2);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                } else if (y2 >= y1) {                          //draw down & left
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x2, y1);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                } else {                                        //draw up & left
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x2, y2);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                }
                int w2 = (int) Math.abs(x1 - x2);
                int h2 = (int) Math.abs(y1 - y2);
                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                WritableImage wImage = new WritableImage(w2, h2);
                Rectangle2D rect;
                if (x2 >= x1 && y2 >= y1) {                 //draw down & right
                    rect = new Rectangle2D(x1, y1, w, h);
                } else if (x2 >= x1) {                      //drawing up & right
                    rect = new Rectangle2D(x1, y2, w, h);
                } else if (y2 >= y1) {                      //draw down & left
                    rect = new Rectangle2D(x2, y1, w, h);
                } else {                                    //draw up & left
                    rect = new Rectangle2D(x2, y2, w, h);
                }
                parameters.setViewport(rect);
                if (insideCanvas) {
                    s1 = mouseClick.getX();
                    s2 = mouseClick.getY();
                    s3 = event.getX();
                    s4 = event.getY();
                }
            }
        }
    }

    private void onMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (controller.pencil) {
                GraphicsContext gc2 = canvas.getGraphicsContext2D();
                Color color = controller.currentColor;
                double size = controller.currentLineWidth;
                gc2.setStroke(color);
                gc2.setLineWidth(size);
                mouseFollow = new Point2D(event.getX(), event.getY());
                if (event.getButton() == MouseButton.PRIMARY) {
                    gc2.lineTo(event.getX(), event.getY());
                    gc2.stroke();
                }
            }
            else if(controller.snip) {
                double x1, y1, x2, y2;
                x1 = mouseClick.getX();
                y1 = mouseClick.getY();
                x2 = event.getX();
                y2 = event.getY();
                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                double w = x2 - x1;
                double h = y2 - y1;
                if (x2 >= x1 && y2 >= y1) {                     //draw down & right
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x1, y1);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                } else if (x2 >= x1) {                          //drawing up & right
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x1, y2);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                } else if (y2 >= y1) {                          //draw down & left
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x2, y1);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                } else {                                        //draw up & left
                    javafx.geometry.Point2D point = new javafx.geometry.Point2D(x2, y2);
                    gc.strokeRect(point.getX(), point.getY(), w, h);
                }
            } else if (controller.eraser) {
                double size = controller.currentLineWidth;
                gc.clearRect(event.getX() - size / 2, event.getY() - size / 2, size, size);
            }
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

    public String getName() {
        return Name;
    }


    public void sCopy() {
        if (controller.copy) {
            ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(s);
            clipboard.setContent(content);
        }
    }
    public void sCut() {
        if (controller.cut) {
            ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
            undoRedo.trackHistory(this);
            double x1 = s1;
            double y1 = s2;
            double x2 = s3;
            double y2 = s4;
            double w = Math.abs(x2 - x1);
            double h = Math.abs(y2 - y1);
            {
                if (x2 >= x1 && y2 >= y1) {         //draw down & right
                    ghostGC.clearRect(x1, y1, w, h);
                } else //draw down & left
                    //draw up & left
                    if (x2 >= x1) {  //drawing up & right
                        ghostGC.clearRect(x1, y2, w, h);
                    } else ghostGC.clearRect(x2, Math.min(y2, y1), w, h);
            }

            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(s);
            clipboard.setContent(content);
        }
    }
    public void sPaste() {
        if (controller.paste) {
            if (s != null) {
                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                undoRedo.trackHistory(this);
                Point2D point = new Point2D(mouseFollow.getX() - (s.getWidth() / 2), mouseFollow.getY() - (s.getHeight() / 2));
                if (insideCanvas) this.canvas.getGraphicsContext2D().drawImage(s, point.getX(), point.getY());
                else this.canvas.getGraphicsContext2D().drawImage(s, point.getX(), point.getY());
            }
        }
    }
    public void sCrop() {
        if (controller.crop) {
            if (s != null) {
                undoRedo.trackHistory(this);
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                double x = s.getWidth();
                double y = s.getHeight();
                this.setSizeX(x);
                this.setSizeY(y);
                this.UpdateSize();
                this.canvas.getGraphicsContext2D().drawImage(s, 0, 0);
                s = null;

            }
        }
    }
    public void UpdateSize() {
        controller.cHeight.getEditor().setText(String.valueOf(canvas.getHeight()));
        controller.cWidth.getEditor().setText(String.valueOf(canvas.getWidth()));
    }
}