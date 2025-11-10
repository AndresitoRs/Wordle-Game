module di.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;



    opens di.wordle to javafx.fxml;
    exports di.wordle;
}