package monopoly.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.InputStream;

public class CartaAnimacion {

    private final StackPane contenedorRaiz;

    public CartaAnimacion(StackPane raiz) {
        this.contenedorRaiz = raiz;
    }

    public void mostrarCarta(String tipo, String mensaje) {
        // 1. Panel de la carta (Estilo visual)
        VBox cartaVisual = new VBox(15);
        cartaVisual.setAlignment(Pos.CENTER);
        cartaVisual.setMaxSize(350, 250);
        // Fondo blanco con borde y sombra
        cartaVisual.setStyle("-fx-background-color: white; -fx-border-color: #333; -fx-border-width: 2; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 0); -fx-padding: 20;");

        // 2. Título o Imagen
        Label lblTitulo = new Label(tipo.toUpperCase());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e17055;");

        // 3. Texto del mensaje
        Label lblMensaje = new Label(mensaje);
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");

        cartaVisual.getChildren().addAll(lblTitulo, lblMensaje);

        // --- ANIMACIÓN ---
        cartaVisual.setOpacity(0); // Empieza invisible
        contenedorRaiz.getChildren().add(cartaVisual); // La ponemos encima del tablero

        // Aparecer (0.5s)
        FadeTransition aparecer = new FadeTransition(Duration.seconds(0.5), cartaVisual);
        aparecer.setFromValue(0);
        aparecer.setToValue(1);

        // Esperar (3s) para que leas
        PauseTransition esperar = new PauseTransition(Duration.seconds(3));

        // Desaparecer (0.5s)
        FadeTransition desaparecer = new FadeTransition(Duration.seconds(0.5), cartaVisual);
        desaparecer.setFromValue(1);
        desaparecer.setToValue(0);

        // Secuencia completa
        SequentialTransition secuencia = new SequentialTransition(aparecer, esperar, desaparecer);
        secuencia.setOnFinished(e -> contenedorRaiz.getChildren().remove(cartaVisual)); // Limpiar al acabar
        secuencia.play();
    }
}