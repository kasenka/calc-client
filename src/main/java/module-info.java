module org.example.calc_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires spring.web;
    requires com.fasterxml.jackson.databind;
    requires static lombok;

    opens org.example.calc_client to javafx.fxml;
    exports org.example.calc_client;
    exports org.example.calc_client.controller;
    opens org.example.calc_client.controller to javafx.fxml;
}