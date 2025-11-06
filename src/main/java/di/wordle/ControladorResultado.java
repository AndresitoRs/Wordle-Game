package di.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ControladorResultado {

    @FXML
    Resultado info;
    @FXML
    ImageView imagen;

    public ImageView getImagen(){
        return imagen;
    }
}
