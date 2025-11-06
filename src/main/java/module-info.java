module di.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens di.wordle to javafx.fxml;
    exports di.wordle;
}