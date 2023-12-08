package com.example.paintapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;


/**
 * Main AppClass
 */
public class PaintApp extends Application
{
    /**
     * Creates the save-path for any saves done in the App
     */
    public static final String savePath = System.getProperty("user.home") + "\\Documents\\PaintApp\\Saved Images";

    /**
     * Starts javafx program
     *
     * @param stage Primary stage
     * @throws Exception App startup Exception
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader f = new FXMLLoader(PaintApp.class.getResource("PaintApp.fxml"));
        Scene scene = new Scene(f.load(), 1280, 720);
        stage.setTitle("Paint App");
        stage.setScene(scene);
        PaintController controller = f.getController();
        stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, controller::OnClose);
        stage.setMaximized(true);
        stage.show();
        URL mySource = PaintApp.class.getProtectionDomain().getCodeSource().getLocation();
        File rootFolder = new File(mySource.getPath());
        System.setProperty("app.root", rootFolder.getAbsolutePath());
        controller.Start();
    }

    /**
     * Launches Application
     *
     * @param args Command line args
     */
    public static void main(String[] args) {launch();}
}