module com.example.labone {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires twilio;

    opens com.example.labone to javafx.fxml;
    exports com.example.labone;
}