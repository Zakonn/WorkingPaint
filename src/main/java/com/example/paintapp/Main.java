package com.example.paintapp;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application
{
    //public final static String imageFolder="/Users/rmur/CS250/PaintApp/src/main/java/com/example/paintapp/images/image.png";
    public final static String imageFolder="C:\\Users\\jrmur\\IdeaProjects\\WorkingPaint\\src\\main\\java\\com\\example\\paintapp\\images";
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader f = new FXMLLoader(Main.class.getResource("PaintApp.fxml"));
        Scene scene = new Scene(f.load());
        stage.setTitle("Paint App");
        stage.setScene(scene);
        PaintController controller = f.getController();
        stage.show();
        controller.addTab();
    }
    public static void main(String[] args) {launch();}
}