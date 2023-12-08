package com.example.paintapp;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Class that handles creation and configuration of all canvas nodes
 */
public class CanvasPanel {
    /**
     *Panel name
     */
    public final String Name;
    public final SnapshotParameters parameters = new SnapshotParameters();
    /**
     * Instance of the controller class
     */
    private final PaintController controller;
    /**
     * The parent Pane of this Canvas
     */
    private final TabPane parent;
    private final Double CWIDTH = 1024d;
    private final Double CHEIGHT = 1024d;
    private boolean insideCanvas = false;

    public StackPane root = new StackPane();
    public Canvas canvas = new Canvas();
    public ScrollPane scrollPane;
    /**
    * A ghost canvas that will reveal what changes will show what will be drawn before they are on the real canvas.
    */
    public Canvas ghostCanvas = new Canvas();
    private final GraphicsContext ghostGC = ghostCanvas.getGraphicsContext2D();
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    /**
     * Top pane of the canvas nodes.
     */
    public AnchorPane pane = new AnchorPane();
    private Group ScrollContent;

    public UndoRedo undoRedo;

    /**
     * firstTouch point of first mouse click
     * mouseFollow point that follows current mouse position
     */

    private Point2D firstTouch;
    private Point2D mouseFollow;
    /**
     * Image that holds the selected portion of image for crop, copy, paste... ext.
     * each double serves as the corners of selected image
     */
    private Image s;
    private double s1, s2, s3, s4;


    /**
     * Default Constructor
     * @param tabPane           The parent of the class
     * @param name              The name of the canvas panel
     * @param controllerIn      The current controller for this canvas.
     */
    public CanvasPanel(TabPane tabPane, String name, PaintController controllerIn) {
        parent = tabPane;
        controller = controllerIn;
        Name = name;
        Create(name);
    }

    /**
     * clears canvas
     */
    public void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Creates a new panel
     * @param name Name of the CanvasPanel
     */
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
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        root.setPrefWidth(CWIDTH);
        root.setPrefHeight(CHEIGHT);
        root.getChildren().add(canvasPane);
        root.getChildren().add(sCPane);
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

        UpdateSize();

        pane.setOnMouseDragged(this::onMouseDragged);
        pane.setOnMousePressed(this::onMousePressed);
        pane.setOnMouseReleased(this::onMouseReleased);
        pane.setOnMouseMoved(this::onMouseMove);
        pane.setOnMouseEntered(this::mouseEnter);
        pane.setOnMouseExited(this::mouseExit);

        parameters.setFill(Color.TRANSPARENT);
        undoRedo = new UndoRedo();
        gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        controller.tabs.getSelectionModel().select(tab);
    }
    private void mouseEnter(MouseEvent event) {
        insideCanvas = true;
    }
    private void mouseExit(MouseEvent event) {
        insideCanvas = false;
    }

    /**
     * Handles tools that require a mouse press.
     * @param event
     */
    private void onMousePressed(MouseEvent event) {
        firstTouch = new Point2D(event.getX(), event.getY());
        if (event.getButton() == MouseButton.PRIMARY) {
            gc.setLineWidth(controller.currentLineWidth);
            gc.setStroke(controller.currentColor);
            ghostGC.setStroke(Color.LIGHTBLUE);
            switch (controller.getToggle()) {
                case Pencil, Eraser -> {

                    undoRedo.trackHistory(this);
                    gc.beginPath();
                    gc.moveTo(event.getX(), event.getY());
                    gc.stroke();
                    PaintController.loggingTool.info("[{}] Using {} tool", this.Name, controller.getToggle().toString());
                }
                case Line, Rectangle, Square, Oval, Polygon -> {
                    undoRedo.trackHistory(this);
                    PaintController.loggingTool.info("[{}] Using {} tool", this.Name, controller.getToggle().toString());
                }
            }
        }
    }

    /**
     * Handles tools that require a function when mouse is released.
     * @param event
     */
    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            gc.setLineWidth(controller.currentLineWidth);
            gc.setStroke(controller.currentColor);
            ghostGC.setStroke(Color.LIGHTBLUE);
            switch (controller.getToggle()) {
                case Line -> {
                    gc.strokeLine(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY());
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                }
                case Rectangle -> {
                    DrawShapes.DrawRectangle(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), gc);
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                }
                case Square -> {
                    DrawShapes.DrawSquare(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), gc);
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                }
                case Oval -> {
                    DrawShapes.DrawOval(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), gc);
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                }
                case Polygon -> {
                    int sides = 6;
                    try {
                        sides = PaintController.clamp(Integer.parseInt(controller.polySides.getEditor().getText()), 3, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DrawShapes.DrawPoly(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), sides, gc);
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                }
                case Snip -> {
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    DrawShapes.DrawRectangle(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), ghostGC);
                    s = getSubImage(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), this.canvas);
                    if (insideCanvas) {
                        s1 = firstTouch.getX();
                        s2 = firstTouch.getY();
                        s3 = event.getX();
                        s4 = event.getY();
                    }
                }

                default -> {
                }
            }
        }
    }

    /**
     * Handles tools that require a function when mouse is dragged across the canvas.
     * @param event
     */
    private void onMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            gc.setLineWidth(controller.currentLineWidth);
            gc.setStroke(controller.currentColor);
            ghostGC.setStroke(Color.LIGHTBLUE);
            switch (controller.getToggle()) {
                case Pointer -> {

                }
                case Pencil -> {

                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
                case Eraser -> {
                    double size = Double.parseDouble(controller.lineWidth.getEditor().getText()) * 2;
                    gc.clearRect(event.getX() - size / 2, event.getY() - size / 2, size, size);
                }
                case Line -> {
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    ghostGC.strokeLine(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY());
                }
                case Rectangle -> {
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    DrawShapes.DrawRectangle(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), ghostGC);
                }
                case Square -> {
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    DrawShapes.DrawSquare(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), ghostGC);
                }

                case Oval -> {
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    DrawShapes.DrawOval(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), ghostGC);
                }
                case Polygon -> {
                    int sides = 6;
                    try {
                        sides = controller.clamp(Integer.parseInt(controller.polySides.getEditor().getText()), 3, 1000);
                    } catch (Exception e) {
                        PaintController.loggingTool.error("[{}] PolySides Input was invalid!", this.Name);
                        e.printStackTrace();
                    }
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    DrawShapes.DrawPoly(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), sides, ghostGC);
                }
                case Snip -> {
                    ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                    DrawShapes.DrawRectangle(firstTouch.getX(), firstTouch.getY(), event.getX(), event.getY(), ghostGC);
                }
                default -> {
                }
            }
        }
    }

    /**
     * Updates the mouse position to mouseFollow
     * @param event
     */
    private void onMouseMove(MouseEvent event) {
        mouseFollow = new Point2D(event.getX(), event.getY());
    }

    /**
     * Sets the width of every node that builds the canvas
     * @param x
     */

    public void setSizeX(double x) {
        canvas.setWidth(x);
        ghostCanvas.setWidth(x);
        pane.setPrefWidth(x);
        root.setPrefWidth(x);
    }

    /**
     * Sets the height of every node that builds the canvas
     * @param y
     */
    public void setSizeY(double y) {
        canvas.setHeight(y);
        ghostCanvas.setHeight(y);
        root.setPrefHeight(y);
        pane.setPrefHeight(y);
    }

    /**
     * @return name of canvas
     */

    public String getName() {
        return Name;
    }

    /**
     * handles copy function after something is snipped
     */
    public void sCopy() {
        if (controller.getToggle() == tools.Snip) {
            ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(s);
            clipboard.setContent(content);
        }
    }

    /**
     * handles cut function after something is snipped
     */
    public void sCut() {
        if (controller.getToggle() == tools.Snip) {
            ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
            undoRedo.trackHistory(this);
            double x1 = s1;
            double y1 = s2;
            double x2 = s3;
            double y2 = s4;
            double w = Math.abs(x2 - x1);
            double h = Math.abs(y2 - y1);
            {
                if (x2 >= x1 && y2 >= y1) {
                    gc.clearRect(x1, y1, w, h);
                } else if (x2 >= x1) {
                    gc.clearRect(x1, y2, w, h);
                } else gc.clearRect(x2, Math.min(y2, y1), w, h);
            }
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(s);
            clipboard.setContent(content);
        }
    }
    /**
     * handles paste function after something is snipped
     */
    public void sPaste() {
        if (controller.getToggle() == tools.Snip) {
            if (s != null) {
                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                undoRedo.trackHistory(this);
                Point2D point = new Point2D(mouseFollow.getX() - (s.getWidth() / 2), mouseFollow.getY() - (s.getHeight() / 2));
                if (insideCanvas) this.canvas.getGraphicsContext2D().drawImage(s, point.getX(), point.getY());
                else this.canvas.getGraphicsContext2D().drawImage(s, point.getX(), point.getY());
            }
        }
    }
    /**
     * handles crop function after something is snipped
     */
    public void sCrop() {
        if (controller.getToggle() == tools.Snip) {
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

    /**
     * handles mirror function after something is snipped
     */
    public void sMirror() {
        if (controller.getToggle() == tools.Snip) {
            if (s != null) {
                undoRedo.trackHistory(this);
                BufferedImage img = SwingFXUtils.fromFXImage(s, null);
                AffineTransform tx = new AffineTransform();
                AffineTransform tx2 = new AffineTransform();

                tx = AffineTransform.getScaleInstance(-1, 1);
                tx2 = AffineTransform.getScaleInstance(1, -1);

                tx.translate(-img.getWidth(null), 0);
                tx2.translate(0, -img.getHeight(null));


                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                AffineTransformOp op2 = new AffineTransformOp(tx2, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                img = op.filter(img, null);
                img = op2.filter(img, null);

                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());

                double x1 = s1;
                double y1 = s2;
                double x2 = s3;
                double y2 = s4;
                double w = Math.abs(x2 - x1);
                double h = Math.abs(y2 - y1);
                {
                    if (x2 >= x1 && y2 >= y1) {
                        gc.clearRect(x1, y1, w, h);
                    } else if (x2 >= x1) {
                        gc.clearRect(x1, y2, w, h);
                    } else gc.clearRect(x2, Math.min(y2, y1), w, h);
                }
                Point2D point = DrawShapes.getCorner(s1, s2, s3, s4);
                this.canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(img, null),
                        point.getX(), point.getY());
            }
        }
    }

    /**
     * handles rotate function after something is snipped
     */
    public void sRotate() {
        if (controller.getToggle() == tools.Snip) {
            if (s != null) {
                undoRedo.trackHistory(this);
                BufferedImage img = SwingFXUtils.fromFXImage(s, null);
                AffineTransformOp op;

                AffineTransform transform = new AffineTransform();
                transform.rotate(Math.PI / 2, img.getWidth() / 2d, img.getHeight() / 2d);
                double offset = (img.getWidth() - img.getHeight()) / 2d;
                transform.translate(offset, offset);

                op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

                img = op.filter(img, null);

                ghostGC.clearRect(0, 0, ghostCanvas.getWidth(), ghostCanvas.getHeight());
                double x1 = s1;
                double y1 = s2;
                double x2 = s3;
                double y2 = s4;
                double w = Math.abs(x2 - x1);
                double h = Math.abs(y2 - y1);
                {
                    if (x2 >= x1 && y2 >= y1) {
                        gc.clearRect(x1, y1, w, h);
                    } else if (x2 >= x1) {
                        gc.clearRect(x1, y2, w, h);
                    } else gc.clearRect(x2, Math.min(y2, y1), w, h);
                }
                Point2D point = DrawShapes.getCorner(s1, s2, s3, s4);
                double x = img.getWidth();
                double y = img.getHeight();
                if (x > this.canvas.getWidth()) this.setSizeX(x);
                if (y > this.canvas.getHeight()) this.setSizeY(y);
                this.UpdateSize();
                this.canvas.getGraphicsContext2D().drawImage(SwingFXUtils.toFXImage(img, null),
                        PaintController.clamp(point.getX() - x / 2, 0, this.canvas.getWidth()),
                        PaintController.clamp(point.getY() - y / 2, 0, this.canvas.getHeight()));
                s = SwingFXUtils.toFXImage(img, null);
            }
        }
    }
    /**
     * Updates the size of the canvas based off what is typed by the user.
     */
    public void UpdateSize() {
        controller.cHeight.getEditor().setText(String.valueOf(canvas.getHeight()));
        controller.cWidth.getEditor().setText(String.valueOf(canvas.getWidth()));
    }

    /**
     * Creates an image out of a snipped(selected) area
     *
     * @param x1        x position of mouse click
     * @param y1        y position of mouse click
     * @param x2        x position of mouse release
     * @param y2        y position of mouse release
     * @param node      Node to fetch image from
     * @return          subimage
     */
    private Image getSubImage(double x1, double y1, double x2, double y2, Node node) {
        int w = (int) Math.abs(x1 - x2);
        int h = (int) Math.abs(y1 - y2);
        if (w <= 0 || h <= 0) return null;
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage wImage = new WritableImage(w, h);
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
        return node.snapshot(parameters, wImage);
    }
}