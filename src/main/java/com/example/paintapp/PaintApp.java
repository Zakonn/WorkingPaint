package com.example.paintapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PaintApp extends Application
{
    //public final static String imageFolder="/Users/rmur/CS250/PaintApp/src/main/java/com/example/paintapp/images/image.png";
    public final static String imageFolder="C:\\Users\\jrmur\\IdeaProjects\\WorkingPaint\\src\\main\\java\\com\\example\\paintapp\\images";
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader f = new FXMLLoader(PaintApp.class.getResource("PaintApp.fxml"));
        Scene scene = new Scene(f.load(), 1280, 720);
        stage.setTitle("Paint App");
        stage.setScene(scene);
        stage.setMaximized(true);
        PaintController controller = f.getController();
        stage.show();
        controller.addTab();
        controller.colorPicker.setValue(Color.BLACK);
    }
    public static void main(String[] args) {launch();}
}