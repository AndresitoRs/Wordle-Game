
module di.wordle {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.desktop;
        requires javafx.graphics;
        requires javafx.base;
        requires com.google.gson;

        opens di.wordle to javafx.fxml, com.google.gson;
        exports di.wordle;
}
