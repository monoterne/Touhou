module touhou {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    opens touhou to javafx.fxml;
    exports touhou;
}