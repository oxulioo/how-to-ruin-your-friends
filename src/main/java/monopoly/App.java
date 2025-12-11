package monopoly;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// Extender de "Application" convierte esta clase en una App gráfica
public class App extends Application implements JuegoListener {

    private Juego juego;
    private TextArea textArea;
    @Override
    public void start(Stage stage) {
        // 1. PREPARAR EL CONTENIDO (Los actores)
        // Vamos a poner una etiqueta de texto simple para probar
        juego = new Juego();
        juego.addListener(this);

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Un StackPane es un contenedor que apila cosas en el centro
        BorderPane organizador = new BorderPane();
        organizador.setCenter(textArea); // Añadimos la etiqueta al panel

        // 2. CREAR LA ESCENA (La película)
        // Creamos una escena de 640x480 píxeles con nuestro organizador dentro
        Scene scene = new Scene(organizador, 800, 600);

        // 3. CONFIGURAR EL ESCENARIO (La ventana)
        stage.setTitle("How to Ruin Your Friends - V2"); // Título de la barra superior
        stage.setScene(scene); // Le decimos qué escena mostrar
        stage.show(); // ¡Telón arriba!

        juego.notificarMensaje("Iniciando el sistema del juego.");
        try {
            // Creamos unos jugadores de prueba directamente en el motor
            juego.crearJugador("Pedro", "pelota");
            juego.crearJugador("Maria", "coche");
            juego.iniciarPartida(); // Esto imprimirá cosas como el orden de turnos
        } catch (Exception e) {
            juego.notificarError(e.getMessage());
        }
    }

    @Override
    public void onMensaje(String mensaje) {
        // Cuando el juego dice algo, lo añadimos al TextArea
        textArea.appendText(mensaje + "\n");
    }

    @Override
    public void onError(String mensajeError) {
        // Los errores también al texto, quizás podríamos ponerlos en mayúsculas
        textArea.appendText("ERROR: " + mensajeError + "\n");
    }

    // El resto de métodos los dejamos vacíos por ahora (los usaremos cuando dibujemos el tablero)
    @Override public void onJugadorMovido(String j, String c, int n) {}
    @Override public void onCambioFortuna(String j, long f, long c) {}
    @Override public void onDadosLanzados(int d1, int d2, boolean db) {}
    @Override public void onTurnoCambiado(String j) {}
    @Override public void onPropiedadComprada(String j, String p, long pr) {}
    @Override public void onCambioEstadoCarcel(String j, boolean e) {}

    // Este main es necesario para engañar a Java y que arranque JavaFX
    public static void main(String[] args) {
        launch(); // Llama internamente al método start()
    }
}