package di.wordle;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ControladorResultado {

    @FXML
    private Resultado info;

    @FXML
    private ImageView imagen;

    /**
     * Este método se llama manualmente tras cargar el FXML para inicializar el controlador.
     */
    public void init() {
        // La imagen empieza invisible para la animación
        if (imagen != null) {
            imagen.setOpacity(0);
            imagen.setScaleX(0.6);
            imagen.setScaleY(0.6);
        }
    }

    public ImageView getImagen() {
        return imagen;
    }

    /**
     * Asigna una imagen nueva y la anima.
     */
    public void mostrarResultado(Image imagenNueva) {
        imagen.setImage(imagenNueva);
        animarImagen();
    }

    /**
     * Animación elegante: Fade-in + Zoom suave + respiro final.
     */
    private void animarImagen() {

        // Estado inicial para animación
        imagen.setOpacity(0);
        imagen.setScaleX(0.6);
        imagen.setScaleY(0.6);

        FadeTransition fade = new FadeTransition(Duration.millis(450), imagen);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(450), imagen);
        zoom.setFromX(0.6);
        zoom.setFromY(0.6);
        zoom.setToX(1.05);
        zoom.setToY(1.05);

        ScaleTransition ajuste = new ScaleTransition(Duration.millis(180), imagen);
        ajuste.setFromX(1.05);
        ajuste.setFromY(1.05);
        ajuste.setToX(1.0);
        ajuste.setToY(1.0);

        SequentialTransition animacion = new SequentialTransition(fade, zoom, ajuste);
        animacion.play();
    }
}
