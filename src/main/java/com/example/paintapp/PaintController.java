package com.example.paintapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.ToggleSwitch;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Controller that manages FXML objects/functions
 */
public class PaintController {
    /**
     * A dictionary of the files used to save the canvasPanel, created after the first SaveAs is called
     */
    private final HashMap<Integer, File> ImageFile = new HashMap<>();
    /**
     * Array of canvas panels
     */
    public List<CanvasPanel> canvasPanels = new ArrayList<>();
    public ComboBox<Integer> polySides;
    /**
     * The instance of the KeyBinds class that manages KeyBinds for this controller
     */
    private KeyBinds keyBinds;
    /**
     * Creates static logging tool to log all thread information
     */
    static {
        System.setProperty("log4j.configurationFile", "src/main/resources/com/example/paintapp/log4j2.xml");
    }
    public static final Logger loggingTool = LogManager.getLogger(PaintController.class);

    /**
     * ComboBoxes that handle/display user input for Canvas size and line size
     */
    public ComboBox<Integer> cWidth;
    public ComboBox<Integer> cHeight;
    public ComboBox<Integer> lineWidth;
    public double currentLineWidth = 1.0;
    /**
     * ColorPicker menuItem
     * color to maintain current color
     */
    public ColorPicker colorPicker;
    public Color currentColor;
    public ToggleSwitch autoSaveSwitch;


    @FXML
    protected TabPane tabs;

    @FXML
    private AnchorPane root;

    /**
     * All buttons from FXML
     * Boolean to handle if they are clicked
     */
    public ToggleButton pencilButton;
    public ToggleButton eraserButton;
    public ToggleButton rectangleButton;
    public ToggleButton snipButton;
    public Button cutButton;
    public Button copyButton;
    public Button cropButton;
    public Button pasteButton;
    public Button mirrorButton;
    public Button rotateButton;
    public ToggleButton squareButton;
    public ToggleButton ovalButton;
    public ToggleButton polyButton;
    public ToggleButton pointerButton;
    public ToggleButton lineButton;

    /**
     * Timer for autoSaving
     */
    public Label timerLabel;
    private TimerTask saveManager;
    private Timer timer;
    public List<ToggleButton> toggles;


    /**
     * Called when program starts.
     *
     * Starts threading
     * Sets root scene
     * Adds tabs
     * Starts AutoSave
     */
    protected void Start() {
        loggingTool.info("[APP] Started Program Controller!");
        Scene scene = root.getScene();
        keyBinds = new KeyBinds(scene, this);
        keyBinds.SetKeyBinds();
        addTab();
        startAutoSave();
        addTips();
        colorPicker.setValue(Color.BLACK);
        System.out.println("Hello this works");
        try {
            Path path = Path.of(PaintApp.savePath);
            if (!Files.exists(path)) {
                boolean test = new File(PaintApp.savePath).mkdirs();
            }
        } catch (Exception e) {
            loggingTool.error("[APP] Failed to make application directories!");
            e.printStackTrace();
        }
    }

    /**
     * Asks user to save before closing
     *
     * @param event When application is closed
     */
    protected void OnClose(WindowEvent event) {
        boolean needSave = ImageFile.size() != tabs.getTabs().size();
        if (needSave) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            ButtonType save = new ButtonType("SAVE & QUIT");
            alert.getButtonTypes().remove(ButtonType.OK);
            alert.getButtonTypes().add(ButtonType.CANCEL);
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(save);
            alert.setHeaderText("You haven't saved in a while...");
            alert.setTitle("Quit application");
            alert.setContentText("Close without saving?");
            alert.initOwner(root.getScene().getWindow());
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isPresent()) {
                if (res.get().equals(ButtonType.CANCEL))
                    event.consume();
                if (res.get().equals(ButtonType.YES)) {
                    Platform.exit();
                    System.exit(0);
                }
                if (res.get().equals(save)) {
                    saveAll();
                    Platform.exit();
                    System.exit(0);
                }
            }
        }
        else {
            Platform.exit();
            System.exit(0);
        }
    }


    /**
     * Adds tips for each button
     * Also places tools in an arrayList
     */
    public void addTips() {
        pointerButton.setTooltip(new Tooltip("Pointer"));
        pencilButton.setTooltip(new Tooltip("Pencil"));
        eraserButton.setTooltip(new Tooltip("Eraser"));
        lineButton.setTooltip(new Tooltip("Line"));
        rectangleButton.setTooltip(new Tooltip("Draw Rectangle"));
        snipButton.setTooltip(new Tooltip("Snip a selection"));
        squareButton.setTooltip(new Tooltip("Square"));
        ovalButton.setTooltip(new Tooltip("Oval"));
        polyButton.setTooltip(new Tooltip("Poly"));
        cutButton.setTooltip(new Tooltip("Cut"));
        copyButton.setTooltip(new Tooltip("Copy"));
        cropButton.setTooltip(new Tooltip("Crop"));
        pasteButton.setTooltip(new Tooltip("Paste"));
        mirrorButton.setTooltip(new Tooltip("Mirror"));
        rotateButton.setTooltip(new Tooltip("Rotate"));
        autoSaveSwitch.setTooltip(new Tooltip("Switch auto-save ON or OFF"));

        toggles = new ArrayList<>() {{
            add(pointerButton);
            add(pencilButton);
            add(eraserButton);
            add(lineButton);
            add(rectangleButton);
            add(snipButton);
            add(squareButton);
            add(ovalButton);
            add(polyButton);
        }};
    }

    /**
     * @param pos index of tool selected
     * @return enum of selected type tool
     */
    private tools convTool(Integer pos) {
        switch (pos + 1) {
            case 1 -> {
                return tools.Pointer;
            }
            case 2 -> {
                return tools.Pencil;
            }
            case 3 -> {
                return tools.Eraser;
            }
            case 4 -> {
                return tools.Line;
            }
            case 5 -> {
                return tools.Rectangle;
            }
            case 6 -> {
                return tools.Snip;
            }
            case 7 -> {
                return tools.Square;
            }
            case 8 -> {
                return tools.Oval;
            }
            case 9-> {
                return tools.Polygon;
            }
            default -> {
                return tools.Nothing;
            }
        }
    }
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
     * @return selected toggled tool
     */
    public tools getToggle() {
        for (int i = 0; i <= toggles.size() - 1; i++) {
            if (toggles.get(i).isSelected()) {
                return convTool(i);
            }
        }
        return tools.Nothing;
    }


    /**
     * Function to handle when About is clicked in MenuBar
      */
    @FXML
    private void handleAboutAction(final ActionEvent event) {
        provideAboutFunctionality();
    }

    /**
     * Function to close app when clicked in the MenuBar
     */
    @FXML
    protected void handleButtonClose() {
        root.getScene().getWindow().fireEvent(new WindowEvent(root.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
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
    public void Save(String name) {
        String fullName = PaintApp.savePath + "\\" + name + ".png";
        File file = new File(fullName);
        CanvasPanel canvas = this.getCanvas();
        Image img = getRegion(canvas.canvas, canvas.canvas.getWidth(), 0, canvas.canvas.getHeight(), 0);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
        } catch (IOException e) {
            loggingTool.error("[APP] Encountered IOException when trying to save {} as a file", name);
            throw new RuntimeException(e);
        }
        Notifications.create()
                .title("Save Image")
                .text("Successfully Saved!")
                .darkStyle()
                .hideAfter(new Duration(4000))
                .owner(root.getScene().getWindow())
                .show();
    }

    /**
     * Saves all panels open as seperate images
     */
    protected void saveAll() {
        for (int i = 0; i <= tabs.getTabs().size() - 1; i++) {
            if (ImageFile.containsKey(i))
                Save(canvasPanels.get(i).Name);
            else {
                String fullName = PaintApp.savePath + "\\" + canvasPanels.get(i).Name + ".png";
                ImageFile.putIfAbsent(i, new File(fullName));
                Save(canvasPanels.get(i).Name);
            }
        }
    }

    /**
     * Sets Save to save button
     */
    @FXML
    public void handleSaveAction(ActionEvent event) throws IOException {
        Save(getCanvas().Name);
    }

    /**
     * Handles saving current canvas as an image to user created file.
     */
    public void handleSaveAsAction(ActionEvent event) throws IOException {

        FileChooser fc = new FileChooser();
        CanvasPanel canvas = this.getCanvas();
        fc.setTitle("Save As");
        fc.setInitialDirectory(new File(PaintApp.savePath));
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
     * Function that begins Autosave thread
     */
    public void startAutoSave() {
        try {
            if (timer == null) timer = new Timer();
            if (saveManager == null) saveManager = new AutoSave(300, this);
            timer.scheduleAtFixedRate(saveManager, 0, 1000);
            loggingTool.info("[APP] Started AutoSave Thread");
        } catch (Exception e) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            saveManager = new AutoSave(300, this);
            timer.scheduleAtFixedRate(saveManager, 0, 1000);
            loggingTool.info("[APP] Restarted AutoSave Thread");
        }
    }

    /**
     * Perform functionality associated with "About" menu selection
     */
    private void provideAboutFunctionality() {

    }

    /**
     * Updates the current color from colorPicker when a new color is picked.
     */
    public void updateColor(ActionEvent actionEvent) {
        currentColor = colorPicker.getValue();
    }

    /**
     * Handles width change when user changes width of canvas
     */
    public void handlecWidth(ActionEvent inputMethodEvent) {
        try {
            String value = cWidth.getEditor().getText();
            double numb = Double.parseDouble(value);
            numb = clamp(numb, 0d, 2000d);
            this.getCanvas().setSizeX(numb);
        } catch (Exception ignored) {
            loggingTool.error("[APP] Canvas Width input was Invalid");
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
            loggingTool.error("[APP] Canvas Height input was Invalid!");
        }
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
    @FXML
    public void handleClearAction() {
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
        if (getToggle() == tools.Snip) {
            getCanvas().sCrop();
        } else {
            snipButton.setSelected(true);
        }
    }

    public void handleMirror(ActionEvent actionEvent) { getCanvas().sMirror(); }

    public void handleRotate(ActionEvent actionEvent) { getCanvas().sRotate(); }

    /**
     * Helper function that adjust value to be constrained between min/max
     * @param val   The double being evaluated.
     * @param min   The minimum possible value.
     * @param max   The maximum possible value.
     * @return      Value between min/max
     */
    public static double clamp(double val, double min, double max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }

    /**
     * Helper function that adjust value to be constrained between min/max
     * @param val   The integer being evaluated.
     * @param min   The minimum possible value.
     * @param max   The maximum possible value.
     * @return      Value between min/max
     */
    public static int clamp(int val, int min, int max) {
        if (val > max) val = max;
        else if (val < min) val = min;
        return val;
    }

}





