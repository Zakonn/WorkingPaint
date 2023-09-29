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

public class Main extends Application
{
    public final static String imageFolder="/Users/rmur/CS250/PaintApp/src/main/java/com/example/paintapp/images/image.png";

    public static Stage mainStage;
    public static ToolBar toolBar;
    public static MenuBar menuBar;



    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader f = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(f.load());
        stage.setTitle("Paint App");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {launch();}
}