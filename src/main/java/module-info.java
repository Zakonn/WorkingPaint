module com.example.paintapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.paintapp to javafx.fxml;
    exports com.example.paintapp;
}