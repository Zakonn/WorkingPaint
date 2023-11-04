module com.example.paintapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires org.apache.logging.log4j;


    opens com.example.paintapp to javafx.fxml;
    exports com.example.paintapp;
}