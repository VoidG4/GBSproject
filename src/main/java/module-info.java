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
    requires java.logging;
    requires annotations;
    requires itextpdf;
    requires javafx.swing;

    opens com.gbs.gbsproject to javafx.fxml;
    exports com.gbs.gbsproject;
    exports com.gbs.gbsproject.controller;
    opens com.gbs.gbsproject.controller to javafx.fxml;
}