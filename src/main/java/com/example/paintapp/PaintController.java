package com.example.paintapp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


import java.io.File;
import java.io.IOException;
import java.util.*;


import javax.imageio.ImageIO;

/**
 * Controller that manages FXML objects/functions
 */
public class PaintController {
    /**
     * Array of canvas panels
     */
    public List<CanvasPanel> canvasPanels = new ArrayList<>();
    public MenuBar menuBar;
    public ToolBar toolBar;
    /**
     * Creates static logging tool to log all thread information
     */
    static {
        System.setProperty("log4j.configurationFile", "src/main/resources/com/example/paintapp/log4j2.xml");
    }
    public static final Logger loggingTool = LogManager.getLogger(PaintController.class);

    /**
     * ComboBoxes that handle/display user input
     */
    public ComboBox<Integer> cWidth;
    public ComboBox<Integer> cHeight;
    public ComboBox<Integer> lineWidth =
            new ComboBox<>(FXCollections.observableList(Arrays.asList(1, 2, 4, 8, 10, 12, 14, 18, 24, 30, 36, 48, 60, 72)));
    public double currentLineWidth = 1.0;
    /**
     * ColorPicker menuItem
     * color to maintain current color
     */
    public ColorPicker colorPicker;
    public Color currentColor;



    @FXML
    protected TabPane tabs;

    @FXML
    private AnchorPane root;

    /**
     * All buttons from FXML
     * Boolean to handle if they are clicked
     */
    public Button pencilButton;
    public boolean pencil = false;
    public Button eraserButton;
    public boolean eraser = false;
    public Button rectangleButton;
    public boolean rectangle = false;
    public Button snipButton;
    public boolean snip = false;
    public Button cutButton;
    public Button copyButton;
    public boolean copy = false;
    public Button cropButton;
    public boolean crop = false;
    public Button pasteButton;
    public Button mirrorButton;
    public Button rotateButton;
    public Button autoSaveButton;
    public boolean auto = false;

    /*
    private final Image ipaste = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/paste.png")));
    private final Image ipencil = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/pencil.png")));
    private final Image icopy = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/copy.png")));
    private final Image irotate = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/rotate.png")));
    private final Image ieraser = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/eraser.png")));
    private final Image icrop = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/crop.png")));
    private final Image isnip = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/snip.png")));
    private final Image irect = new Image(Objects.requireNonNull(PaintApp.class.getResourceAsStream("Icons/rectangle.png")));
    */

    /**
     * Timer for autoSaving
     */
    private static final Integer STARTTIME = 15;
    private Timeline timeline;
    private Integer timeSeconds = STARTTIME;
    public Label timerLabel;

/*
    public void addIcons() {
        pencilButton.setGraphic(new ImageView(ipencil));
        pasteButton.setGraphic(new ImageView(ipaste));
        copyButton.setGraphic(new ImageView(icopy));
        rotateButton.setGraphic(new ImageView(irotate));
        eraserButton.setGraphic(new ImageView(ieraser));
        cropButton.setGraphic(new ImageView(icrop));
        snipButton.setGraphic(new ImageView(isnip));
        rectangleButton.setGraphic(new ImageView(irect));

    }

 */

    /**
     * Adds a tab with a fresh canvas
     * Must be called during initialization
     */
    @FXML
    protected void addTab() {
        TextInputDialog dialog = new TextInputDialog("Untitled " + "(" + tabs.getTabs().size() + ")");
        dialog.setHeaderText("Create New Paint Project");
        dialog.setContentText("Enter Project Name");
        Optional<String> response = dialog.showAndWait();
        if (response.isPresent()) {
            String name = response.get();
            canvasPanels.add(new CanvasPanel(tabs, name, this));
        }
    }

    /**
     * Fetches canvas from current tab
     * @return  canvasPanel
     */
    public CanvasPanel getCanvas() {
        CanvasPanel panel;
        int curr = tabs.getSelectionModel().getSelectedIndex();
        panel = canvasPanels.get(curr);
        return panel;
    }

    /**
     * Function to handle when About is clicked in MenuBar
      */
    @FXML
    private void handleAboutAction(final ActionEvent event) {
        provideAboutFunctionality();
    }

    /**
     * Opens a new image file and displays onto current CanvasPanel
     */
    public void handleOpenAction() {
        FileChooser fc = new FileChooser();
        // Set the title of FileChooser
        fc.setTitle("Open an Image");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("images", "*.png", "*.jpg", "*.gif", "*.bmp"));
        File imageFile = fc.showOpenDialog(root.getScene().getWindow());
        if (imageFile == null)
            return;
        Image tempImg = new Image(imageFile.toURI().toString());
        CanvasPanel canvas = this.getCanvas();

        canvas.setSizeX(tempImg.getWidth());
        canvas.setSizeY(tempImg.getHeight());
        canvas.canvas.getGraphicsContext2D().drawImage(tempImg, 0, 0);
        loggingTool.info("[{}] Opened an image File: {}", this.getCanvas().Name, imageFile.getName());
    }

    /**
     * Helper function that creates an image out of current canvas
     * @param canvas    current canvas
     * @param x1    x-coordinates
     * @param x2    x2-coordinates
     * @param y1    y-coordinates
     * @param y2    y2-coordinates
     * @return      image
     */
    public Image getRegion(Canvas canvas, double x1, double x2, double y1, double y2) {
        double width = Math.abs(x1 - x2);
        double height = Math.abs(y1 - y2);

        SnapshotParameters sp = new SnapshotParameters();
        WritableImage imgShown = new WritableImage((int) width, (int) height);

        sp.setViewport(new Rectangle2D(
                (Math.min(x1, x2)),
                (Math.min(y1, y2)),
                width,
                height
        ));
        canvas.snapshot(sp, imgShown);
        return imgShown;
    }

    /**
     * Saves image to default imageFolder
     */
    public void Save() throws IOException {
        File file = new File(PaintApp.imageFolder);
        CanvasPanel canvas = this.getCanvas();
        Image img = getRegion(canvas.canvas, canvas.canvas.getWidth(), 0, canvas.canvas.getHeight(), 0);


        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
        Notifications.create()
                .title("Save Image")
                .text("Successfully Saved!")
                .darkStyle()
                .hideAfter(new Duration(4000))
                .owner(root.getScene().getWindow())
                .show();
    }

    /**
     * Sets Save to save button
     */
    public void handleSaveAction() throws IOException {
        Save();
    }

    /**
     * Handles saving current canvas as an image to user created file.
     */
    public void handleSaveAsAction(ActionEvent event) throws IOException {

        FileChooser fc = new FileChooser();
        CanvasPanel canvas = this.getCanvas();
        fc.setTitle("Save As");
        fc.setInitialDirectory(new File(System.getProperty("user.home") + "\\Documents\\PaintApp\\Saved Images"));
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpg"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"));
        File f1 = fc.showSaveDialog(null);
        Image img = getRegion(canvas.canvas, canvas.canvas.getWidth(), 0, canvas.canvas.getHeight(), 0);
        if (f1 != null) {
            String name = f1.getName();
            String extension = name.substring(1 + name.lastIndexOf(".")).toLowerCase();
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, f1);
            Notifications.create()
                    .title("Save Image")
                    .text("Successfully Saved!")
                    .darkStyle()
                    .hideAfter(new Duration(4000))
                    .owner(root.getScene().getWindow())
                    .show();
        }
    }

    /**
     * Handles userKeyInput to save
     */
    @FXML
    private void handleKeyInput(final InputEvent event) throws IOException {
        if (event instanceof KeyEvent save) {
            if (save.isControlDown() && save.getCode() == KeyCode.S) {
                handleSaveAction();
            }
        }
    }

    /**
     * Perform functionality associated with "About" menu selection
     */
    private void provideAboutFunctionality() {

    }

    public void setKeys(Scene scene) {

    }

    /**
     * Updates the current color from colorPicker when a new color is picked.
     */
    public void updateColor(ActionEvent actionEvent) {
        currentColor = colorPicker.getValue();
    }

    /**
     * Draws shapes when shape button is pressed
     */
    public void handleShapes(ActionEvent actionEvent) {
        drawShapes(getCanvas().canvas.getGraphicsContext2D());
    }

    /**
     * Handles width change when user changes width of canvas
     */
    public void handlecWidth(ActionEvent inputMethodEvent) {
        try {
            String value = cWidth.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            this.getCanvas().setSizeY(numb);
        } catch (Exception ignored) {
        }
    }
    /**
     * Handles height change when user changes width of canvas
     */
    public void handlecHeight(ActionEvent inputMethodEvent) {
        try {
            String value = cHeight.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            this.getCanvas().setSizeY(numb);
        } catch (Exception ignored) {
        }
    }

    /**
     * Function that draws a set of shapes
     * @param gc    current graphics context
     */
    public void drawShapes(GraphicsContext gc) {
        Color color = currentColor;
        double width = Double.parseDouble(this.lineWidth.getEditor().getText());
        gc.setLineWidth(width);
        gc.setStroke(color);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    /**
     * Updates the current line width for whenever a new line width is changed by user
     */
    public void updateLineWidth(ActionEvent actionEvent) {
        currentLineWidth = Double.parseDouble(lineWidth.getEditor().getText());
    }
    /**
     * Adds a new tab when the NEW menuItem is pressed
     */
    public void handleNewAction(ActionEvent actionEvent) {
        addTab();
    }

    /**
     * Clears canvas when CLEAR menuItem is pressed.
     */
    public void handleClearAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to clear your current canvas?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Clear Canvas");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            this.getCanvas().clearCanvas();
        }
    }

    /**
     * handles undo button when clicked
     */
    public void handleUndoAction(ActionEvent actionEvent) {
        this.getCanvas().undoRedo.Undo(this.getCanvas());
    }
    /**
     * handles redo button when clicked
     */
    public void handleRedoAction(ActionEvent actionEvent) {
        this.getCanvas().undoRedo.Redo(this.getCanvas());
    }
    /**
     * handles pencil button when clicked
     * changes the color of the button clicked.
     */
    public void handlePencil(ActionEvent actionEvent) {
        if (!pencil) {
            pencil = true;
            pencilButton.setTextFill(Color.LIGHTGREEN);
        }
        else {
            pencil = false;
            pencilButton.setTextFill(Color.BLACK);
        }
    }

    public void handleEraser(ActionEvent actionEvent) {
        if (!eraser) {
            eraser = true;
            eraserButton.setTextFill(Color.LIGHTGREEN);
        }
        else {
            eraser = false;
            eraserButton.setTextFill(Color.BLACK);
        }
    }

    public void handleSnip(ActionEvent actionEvent) {
        if (!snip) {
            snip = true;
            snipButton.setTextFill(Color.LIGHTGREEN);
        }
        else {
            snip = false;
            snipButton.setTextFill(Color.BLACK);
        }
    }
    public void handleCut(ActionEvent actionEvent) {
        getCanvas().sCut();
    }

    public void handleCopy(ActionEvent actionEvent) {
        getCanvas().sCopy();
    }

    public void handleCrop(ActionEvent actionEvent) {
        getCanvas().sCrop();
    }

    public void handlePaste(ActionEvent actionEvent) {
        getCanvas().sPaste();
    }

    public void handleMirror(ActionEvent actionEvent) { getCanvas().sMirror(); }

    public void handleRotate(ActionEvent actionEvent) { getCanvas().sRotate(); }

    public void handleRect(ActionEvent actionEvent) {
        if (!rectangle) {
            rectangle = true;
            rectangleButton.setTextFill(Color.LIGHTGREEN);
        }
        else {
            rectangle = false;
            rectangleButton.setTextFill(Color.BLACK);
        }
    }
    /**
     * Helper function that adjust value to be constrained between min/max
     * @param val   The integer being evaluated.
     * @param min   The minimum possible value.
     * @param max   The maximum possible value.
     * @return      Value between min/max
     */
    public static double clamp(double val, double min, double max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }

    public void handleAutoSaveButton(ActionEvent actionEvent) {
        if (!auto) {
            auto = true;
            DropShadow shadow = new DropShadow();
            autoSaveButton.setEffect(shadow);

            timerLabel.setText("AutoSave: " + timeSeconds.toString());
            timerLabel.setTextFill(Color.RED);

            timeSeconds = STARTTIME;

            //update timer label
            timerLabel.setText("AutoSave: " + timeSeconds);
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            timeSeconds--;
                            // update timerLabel
                            timerLabel.setText("AutoSave: " + timeSeconds);
                            if (timeSeconds <= 0) {
                                timeSeconds = STARTTIME;
                                timeline.playFromStart();
                                try {
                                    Save();
                                    Notifications.create().title("AutoSave").text("Saved File Successfully!").hideAfter(new Duration(3000)).owner(root.getScene().getWindow()).show();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }));
            timeline.playFromStart();
        }
        else {
            auto = false;
            autoSaveButton.setEffect(null);
            timeline.stop();
            timeSeconds = STARTTIME;
            timerLabel.setText("AutoSave: " + timeSeconds);
        }
    }



}





