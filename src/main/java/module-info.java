module com.example.gbsproject {
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
    requires annotations;
    requires itextpdf;
    requires javafx.swing;
    requires okhttp3;
    requires org.json;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.gbs.gbsproject to javafx.fxml;
    exports com.gbs.gbsproject;
    exports com.gbs.gbsproject.controller;
    exports com.gbs.gbsproject.model;
    opens com.gbs.gbsproject.controller to javafx.fxml;
    opens com.gbs.gbsproject.model to javafx.base;

}