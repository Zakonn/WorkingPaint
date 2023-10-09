package com.example.paintapp;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class PaintController {
    public List<CanvasPanel> canvasPanels = new ArrayList<>();
    public MenuBar menuBar;
    public ToolBar toolBar;
    public ColorPicker colorPicker;
    public Color currentColor;
    public ComboBox<Integer> cWidth;
    public ComboBox<Integer> cHeight;
    public ComboBox<Integer> lineWidth =
            new ComboBox<>(FXCollections.observableList(Arrays.asList(1, 2, 4, 8, 10, 12, 14, 18, 24, 30, 36, 48, 60, 72)));
    public double currentLineWidth = 1.0;



    // The node that manages all the canvas panel tabs
    @FXML
    protected TabPane tabs;

    @FXML
    private AnchorPane root;

    public Button pencilButton;
    public boolean pencil = false;
    public Button eraserButton;
    public boolean eraser = false;
    public Button rectangleButton;
    public boolean rectangle = false;
    public Button snipButton;
    public boolean snip = false;
    public Button cutButton;
    public boolean cut = false;
    public Button copyButton;
    public boolean copy = false;
    public Button cropButton;
    public boolean crop = false;
    public Button pasteButton;
    public boolean paste = false;


    public static double clamp(double val, double min, double max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }

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

    public CanvasPanel getCanvas() {
        CanvasPanel panel;
        int curr = tabs.getSelectionModel().getSelectedIndex();
        panel = canvasPanels.get(curr);
        return panel;
    }

    // Function to handle when About is clicked in MenuBar
    @FXML
    private void handleAboutAction(final ActionEvent event) {
        provideAboutFunctionality();
    }

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
    }

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

    public void Save() throws IOException {
        File file = new File(PaintApp.imageFolder);
        CanvasPanel canvas = this.getCanvas();
        Image img = getRegion(canvas.canvas, canvas.canvas.getWidth(), 0, canvas.canvas.getHeight(), 0);

        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
    }

    public void handleSaveAction() throws IOException {
        Save();
    }

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
        }
    }

    @FXML
    private void handleKeyInput(final InputEvent event) throws IOException {
        if (event instanceof KeyEvent) {
            final KeyEvent save = (KeyEvent) event;
            if (save.isControlDown() && save.getCode() == KeyCode.S) {
                handleSaveAction();
            }
        }
    }

    /**
     * Perform functionality associated with "About" menu selection or CTRL-A.
     */
    private void provideAboutFunctionality() {

    }

    public void setKeys(Scene scene) {

    }

    public void updateColor(ActionEvent actionEvent) {
        currentColor = colorPicker.getValue();
    }


    public void handleShapes(ActionEvent actionEvent) {
        drawShapes(getCanvas().canvas.getGraphicsContext2D());
    }

    public void handlecWidth(ActionEvent inputMethodEvent) {
        try {
            String value = cWidth.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            this.getCanvas().setSizeY(numb);
        } catch (Exception ignored) {
        }
    }

    public void handlecHeight(ActionEvent inputMethodEvent) {
        try {
            String value = cHeight.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            this.getCanvas().setSizeY(numb);
        } catch (Exception ignored) {
        }
    }

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
    public void updateLineWidth(ActionEvent actionEvent) {
        currentLineWidth = Double.parseDouble(lineWidth.getEditor().getText());
    }

    public void handleNewAction(ActionEvent actionEvent) {
        addTab();
    }

    public void handleClearAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to clear your current canvas?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Clear Canvas");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            this.getCanvas().clearCanvas();
        }
    }

    public void handleUndoAction(ActionEvent actionEvent) {
        this.getCanvas().undoRedo.Undo(this.getCanvas());
    }

    public void handleRedoAction(ActionEvent actionEvent) {
        this.getCanvas().undoRedo.Redo(this.getCanvas());
    }

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
}




