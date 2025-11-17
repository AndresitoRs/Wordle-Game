package di.wordle;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControladorResultado {

    @FXML
    private Resultado info;

    @FXML
    private ImageView imagen;

    @FXML
    private StackPane contenedor;

    private Random aleatorio = new Random();

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

    public void mostrarResultado(Image imagenNueva) {
        imagen.setImage(imagenNueva);
        animarImagen();
    }

    private void animarImagen() {

        // Estado inicial para animación
        imagen.setOpacity(0);
        imagen.setScaleX(0.5);
        imagen.setScaleY(0.5);
        imagen.setRotate(0);


        FadeTransition fade = new FadeTransition(Duration.millis(600), imagen);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(600), imagen);
        zoomIn.setFromX(0.5);
        zoomIn.setFromY(0.5);
        zoomIn.setToX(1.2);
        zoomIn.setToY(1.2);

        ScaleTransition zoomBack = new ScaleTransition(Duration.millis(300), imagen);
        zoomBack.setFromX(1.2);
        zoomBack.setFromY(1.2);
        zoomBack.setToX(0.95);
        zoomBack.setToY(0.95);

        ScaleTransition zoomSettle = new ScaleTransition(Duration.millis(200), imagen);
        zoomSettle.setFromX(0.95);
        zoomSettle.setFromY(0.95);
        zoomSettle.setToX(1.0);
        zoomSettle.setToY(1.0);

        // Rotación 360° suave
        RotateTransition rotate = new RotateTransition(Duration.millis(1100), imagen);
        rotate.setByAngle(360);
        rotate.setInterpolator(Interpolator.EASE_BOTH);

        // Secuencia de zoom (rebote)
        SequentialTransition zoomBounce = new SequentialTransition(zoomIn, zoomBack, zoomSettle);

        // Combinamos fade + zoomBounce + rotación en paralelo para que se sincronicen
        ParallelTransition parallelAnim = new ParallelTransition(fade, zoomBounce, rotate);

        parallelAnim.play();
    }

    public void lanzarConfeti() {
        int numConfeti = 30;  // Número de partículas
        double areaWidth = contenedor.getWidth();
        double areaHeight = contenedor.getHeight();

        for (int i = 0; i < numConfeti; i++) {
            // Tamaño y posición inicial aleatoria dentro del StackPane
            double tamaño = 4 + aleatorio.nextDouble() * 6;
            Circle circulo = new Circle(tamaño);
            circulo.setFill(Color.color(aleatorio.nextDouble(), aleatorio.nextDouble(), aleatorio.nextDouble()));

            // Posición inicial aleatoria alrededor de la imagen
            circulo.setTranslateX(aleatorio.nextDouble() * areaWidth - areaWidth / 2);
            circulo.setTranslateY(aleatorio.nextDouble() * areaHeight - areaHeight / 2);

            // Añadir el círculo al StackPane
            contenedor.getChildren().add(circulo);

            // Animación: movimiento y desvanecimiento
            TranslateTransition movimiento = new TranslateTransition(Duration.seconds(1 + aleatorio.nextDouble()), circulo);
            movimiento.setByX(aleatorio.nextDouble() * 200 - 100);  // se mueve horizontalmente un poco
            movimiento.setByY(aleatorio.nextDouble() * -200 - 50);  // hacia arriba

            FadeTransition fade = new FadeTransition(Duration.seconds(1.5), circulo);
            fade.setFromValue(1);
            fade.setToValue(0);

            ScaleTransition escala = new ScaleTransition(Duration.seconds(1.5), circulo);
            escala.setToX(0);
            escala.setToY(0);

            ParallelTransition animacion = new ParallelTransition(movimiento, fade, escala);

            // Cuando termine, quita la partícula para limpiar
            animacion.setOnFinished(e -> contenedor.getChildren().remove(circulo));

            animacion.play();
        }
    }

    public void lanzarHumoDerrota() {
        if (!(imagen.getParent() instanceof Pane padre)) return;

        int numParticulas = 20;
        Random rand = new Random();

        for (int i = 0; i < numParticulas; i++) {
            Circle humo = new Circle(12, Color.rgb(180, 180, 180, 0.4));
            humo.setEffect(new BoxBlur(7, 7, 3));

            // Posición inicial: desplazada hacia abajo y derecha respecto al centro
            double startX = rand.nextDouble() * 40 + 40;
            double startY = rand.nextDouble() * 40 + 40;

            humo.setTranslateX(startX);
            humo.setTranslateY(startY);

            padre.getChildren().add(humo);

            // Movimiento ascendente y lateral disperso
            TranslateTransition moverArriba = new TranslateTransition(Duration.seconds(3 + rand.nextDouble() * 2), humo);
            moverArriba.setByY(-80 - rand.nextDouble() * 40);
            moverArriba.setByX(rand.nextDouble() * 40 - 20);
            moverArriba.setInterpolator(Interpolator.EASE_OUT);

            // Fade out y escalado para dispersar
            FadeTransition fade = new FadeTransition(Duration.seconds(3 + rand.nextDouble() * 2), humo);
            fade.setFromValue(0.4);
            fade.setToValue(0);

            ScaleTransition escala = new ScaleTransition(Duration.seconds(3 + rand.nextDouble() * 2), humo);
            escala.setFromX(1);
            escala.setFromY(1);
            escala.setToX(2.0);
            escala.setToY(2.0);

            ParallelTransition desaparecer = new ParallelTransition(moverArriba, fade, escala);

            desaparecer.setDelay(Duration.seconds(rand.nextDouble() * 2));
            desaparecer.setOnFinished(e -> padre.getChildren().remove(humo));
            desaparecer.play();
        }
    }
}


