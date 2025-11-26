module di.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.base;
    requires com.google.gson;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;


    opens di.wordle to javafx.fxml, com.google.gson;
    exports di.wordle;
}
