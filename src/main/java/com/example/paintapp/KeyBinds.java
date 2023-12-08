package com.example.paintapp;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;
import java.util.Set;


/**
 * Handles keyboard input and methods for the PaintController Class
 */
public class KeyBinds {


    /**
     * The scene that is controlled by the controller
     */
    private final Scene scene;

    /**
     * The Contoller that this class handles keybinds for
     */
    private final PaintController controller;

    /**
     * The Instance of the Input Handler for this class that manages the dictionary of pressed keys
     */
    public InputHandler activeKeys;

    /**
     * Constructor for Keys that defines the scene and controller
     *
     * @param input The scene in which to look for key events
     * @param main  The controller class that will "own" this instance of Keys
     */
    public KeyBinds(Scene input, PaintController main) {
        scene = input;
        controller = main;

        activeKeys = new InputHandler();
        scene.setOnKeyPressed(activeKeys);
        scene.setOnKeyReleased(activeKeys);
    }

    /**
     * Handles creation of the event handlers for key events and calls the appropriate method
     */
    public void SetKeyBinds() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if (key.getCode() == KeyCode.R && key.isControlDown()) {
                this.reset();
            }
            if (key.getCode() == KeyCode.N && key.isControlDown()) {
                controller.addTab();
            }
            if (key.getCode() == KeyCode.Z && key.isControlDown()) {
                controller.getCanvas().undoRedo.Undo(controller.getCanvas());
            }
            if (key.getCode() == KeyCode.Y && key.isControlDown()) {
                controller.getCanvas().undoRedo.Redo(controller.getCanvas());
            }

            if (key.getCode() == KeyCode.S && key.isControlDown() && key.isAltDown()) {
                controller.Save(controller.getCanvas().getName());
            }
            if (key.getCode() == KeyCode.C && key.isControlDown() && key.isAltDown()) {
                controller.handleClearAction();
            } else if (key.getCode() == KeyCode.C && key.isControlDown()) {
                controller.getCanvas().sCopy();
            }

            if (key.getCode() == KeyCode.V && key.isControlDown()) {
                controller.getCanvas().sPaste();
            }
            if (key.getCode() == KeyCode.X && key.isControlDown()) {
                controller.getCanvas().sCut();
            }
        });
    }

    /**
     * resets the view of the canvasPanel from the controller class, triggered with CTRL+R
     */
    public void reset() {
        Node node = controller.getCanvas().pane;
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1);
        node.setScaleY(1);
        ScrollPane pane = controller.getCanvas().scrollPane;
        pane.setVvalue(0d);
        pane.setHvalue(0d);
    }


    /**
     * Handles all input from the keyboard and maintains an active list of currently pressed keys
     *
     * @author matde
     */
    static class InputHandler implements EventHandler<KeyEvent> {
        /**
         * A list of all keys currently being pressed down by the user
         */
        final private Set<KeyCode> activeKeys = new HashSet<>();

        /**
         * Handles key events for when keys are pressed or released, will then add or remove them from the list
         *
         * @param event Key event from the Input class
         */
        @Override
        public void handle(KeyEvent event) {
            if (KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
                activeKeys.add(event.getCode());
            } else if (KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
                activeKeys.remove(event.getCode());
            }
        }
    }
}
