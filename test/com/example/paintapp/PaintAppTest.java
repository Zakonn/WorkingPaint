package com.example.paintapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class PaintAppTest {
    Stage stage2;
    PaintController controller;

    @Start
    public void start(Stage stage) throws IOException {
        FXMLLoader f = new FXMLLoader(PaintApp.class.getResource("PaintApp.fxml"));
        Scene scene = new Scene(f.load());
        stage.setTitle("Paint App");
        stage2 = stage;
        stage.setScene(scene);
        controller = f.getController();
        stage.show();
        controller.addTab();
        controller.colorPicker.setValue(Color.BLACK);
    }

    @Test
    public void isStageNamed(FxRobot robot){
        assertEquals(stage2.getTitle(), "Paint App");
    }

    @Test
    public void testDefaultTabCreated(FxRobot robot){
        assertEquals(controller.tabs.getTabs().size(), 1);
    }
    @Test
    public void isColorBlack(FxRobot robot){
        assertEquals(controller.colorPicker.getValue(), Color.BLACK);
    }
}