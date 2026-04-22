module com.champlain.soft.game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.champlain.soft.game to javafx.fxml;
    exports com.champlain.soft.game;
}