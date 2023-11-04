package com.example.paintapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.print.DocFlavor;
import java.io.File;
import java.net.URL;

public class PaintApp extends Application
{
    public final static String imageFolder="/Users/rmur/CS250/PaintApp/src/main/java/com/example/paintapp/images/image.png";
    //public final static String imageFolder="C:\\Users\\jrmur\\IdeaProjects\\WorkingPaint\\src\\main\\java\\com\\example\\paintapp\\images";
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader f = new FXMLLoader(PaintApp.class.getResource("PaintApp.fxml"));
        Scene scene = new Scene(f.load(), 1280, 720);
        stage.setTitle("Paint App");
        stage.setScene(scene);
        PaintController controller = f.getController();
        stage.show();
        controller.addTab();
        //controller.addIcons();
        controller.colorPicker.setValue(Color.BLACK);
        URL mySource = PaintApp.class.getProtectionDomain().getCodeSource().getLocation();
        File rootFolder = new File(mySource.getPath());
        System.setProperty("app.root", rootFolder.getAbsolutePath());

    }
    public static void main(String[] args) {launch();}
}