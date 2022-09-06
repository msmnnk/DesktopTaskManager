module java.main.javafx.task_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    requires mysql.connector.java;
    requires java.sql;
    requires tornadofx.controls;

    opens smnnk to javafx.fxml;
    exports smnnk;
    exports smnnk.controller;
    opens smnnk.controller to javafx.fxml;
    exports smnnk.model;
    opens smnnk.model to javafx.fxml;
}