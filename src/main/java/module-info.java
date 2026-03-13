module com.taskmanagement {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.taskmanagement to javafx.fxml;
    opens com.taskmanagement.controller to javafx.fxml;
    opens com.taskmanagement.model to javafx.fxml;
    opens com.taskmanagement.service to javafx.fxml;
    opens com.taskmanagement.repository to javafx.fxml;
    opens com.taskmanagement.utils to javafx.fxml;
    
    exports com.taskmanagement;
    exports com.taskmanagement.controller;
    exports com.taskmanagement.model;
    exports com.taskmanagement.service;
}
