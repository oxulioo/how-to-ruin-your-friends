package monopoly.gui;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.InputStream;

public class FichaGUI {

    private final ImageView imagenView;
    private final String nombreJugador;

    public FichaGUI(String nombreJugador, String tipoAvatar) {
        this.nombreJugador = nombreJugador;

        // Carga la imagen (JPG o JPOEG)
        Image imagen = cargarImagen("/imagenes/avatar_" + tipoAvatar + ".jpg");
        if (imagen == null) imagen = cargarImagen("/imagenes/avatar_" + tipoAvatar + ".jpeg");
        // Si no hay imagen, un icono por defecto
        if (imagen == null) imagen = new Image("https://cdn-icons-png.flaticon.com/512/747/747376.png");

        this.imagenView = new ImageView(imagen);

        // Tamaño de la ficha
        this.imagenView.setFitWidth(60);
        this.imagenView.setFitHeight(30);
        this.imagenView.setPreserveRatio(true);

        // Recorte circular
        Circle recorte = new Circle(30, 30, 30);
        this.imagenView.setClip(recorte);

        // Sombra
        this.imagenView.setEffect(new DropShadow(3, Color.BLACK));
    }

    private Image cargarImagen(String ruta) {
        try {
            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null) return new Image(is);
        } catch (Exception e) {}
        return null;
    }

    public void colocarEn(Pane contenedor) {
        if (this.imagenView.getParent() != null) {
            ((Pane) this.imagenView.getParent()).getChildren().remove(this.imagenView);
        }
        contenedor.getChildren().add(this.imagenView);
    }
}