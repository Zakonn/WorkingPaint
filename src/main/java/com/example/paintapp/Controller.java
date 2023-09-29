package com.example.paintapp;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Line;

import javax.imageio.ImageIO;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public Canvas canvas;
    public ToolBar toolBar;
    public Slider lineWidthSlider;
    @FXML
    private MenuBar menuBar;

    // Create a FileChooser Object
    final FileChooser fc = new FileChooser();
    double x = 0, y = 0;

    // Function to handle when About is clicked in MenuBar
    @FXML
    private void handleAboutAction(final ActionEvent event) {
        provideAboutFunctionality();
    }

    // Function to handle when New File is clicked in MenuBar
    //public void handleNewAction(ActionEvent event) {}

    // Opens a file chooser to pick out an image
    public void handleOpenAction() {
        // Set the title of FileChooser
        fc.setTitle("Open an Image");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));

        // Set the selected file or null if no file has been selected
        File file = fc.showOpenDialog(null); // Shows a new file open dialog

        if (file == null)
            return;
        Image tempImg = new Image(file.toURI().toString());
        System.out.println(tempImg);

        canvas.getGraphicsContext2D().clearRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
        );
        System.out.println(tempImg.getHeight());

        canvas.setWidth(tempImg.getWidth());
        canvas.setHeight(tempImg.getHeight());
        canvas.set
        canvas.getGraphicsContext2D().drawImage(tempImg, 0, 0);
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

    public void handleSaveAction() throws IOException {
        File file = new File(Main.imageFolder);
        Image img = getRegion(canvas, canvas.getWidth(), 0, canvas.getHeight(), 0);

        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
    }

    public void handleSaveAsAction(ActionEvent event) throws IOException {
        fc.setTitle("Save As");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
        File f1 = fc.showSaveDialog(null);
        Image img = getRegion(canvas, canvas.getWidth(), 0, canvas.getHeight(), 0);
        if (f1 != null) {
            String name = f1.getName();
            String extension = name.substring(1 + name.lastIndexOf(".")).toLowerCase();
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, f1);
        }


    }


    /*
      Handle action related to input (in this case specifically only responds to
      keyboard event CTRL-A).
      @param event Input event.
     */
    @FXML
    private void handleKeyInput(final InputEvent event) throws IOException {
        if (event instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.S) {
                handleSaveAction();
            }
        }
    }
    /**
     * Perform functionality associated with "About" menu selection or CTRL-A.
     */
    private void provideAboutFunctionality() {
        //System.out.println("You clicked on About!");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        menuBar.setFocusTraversable(true);
    }


    public void handleNewLine(ActionEvent event) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(lineWidthSlider.getValue());

        double x1, x2, y1, y2;
        canvasClick();
        x1 = 20;
        y1 = 20;
        canvasClick();
        x2 = 100;
        y2 = 100;
        gc.strokeLine(x1, y1, x2, y2);
    }
    public void canvasClick() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(lineWidthSlider.getValue());
        canvas.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getX();
            y = mouseEvent.getY();

        });
        canvas.setOnMouseDragEntered(mouseEvent -> {
            x = mouseEvent.getX();
            y = mouseEvent.getY();
            gc.stroke();
        });
        canvas.setOnMouseDragExited(mouseEvent -> {
            x = mouseEvent.getX();
            y = mouseEvent.getY();
        });


    }
}



