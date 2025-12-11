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
        // --- A. INICIALIZAR EL MOTOR ---
        juego = new Juego();
        juego.addListener(this);


        // --- B. PREPARAR LA SALIDA (TEXTO) ---
        textArea = new TextArea();
        textArea.setEditable(false);
        // Fuente monoespaciada para que el tablero cuadre perfecto
        textArea.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 12;");


        // CORRECCIÓN: Primero iniciamos/limpiamos, luego creamos jugadores
        // juego.iniciarPartida(); // Opcional, el constructor ya lo hace
        try {
            juego.crearJugador("Pedro", "pelota");
            juego.crearJugador("Maria", "coche");
            juego.notificarMensaje("--- PARTIDA LISTA PARA JUGAR ---");
        } catch (Exception e) {
            onError(e.getMessage());
        }

        // --- C. ZONA DE CONTROLES (BOTONES) ---
        // 1. Crear los botones
        javafx.scene.control.Button btnLanzar = new javafx.scene.control.Button("🎲 Lanzar Dados");
        javafx.scene.control.Button btnTerminar = new javafx.scene.control.Button("⏭ Terminar Turno");
        javafx.scene.control.Button btnTablero = new javafx.scene.control.Button("🗺 Ver Tablero");
        javafx.scene.control.Button btnSalir = new javafx.scene.control.Button("❌ Salir");

        // 2. Darles acción (Conectar botón -> Método del Juego)
        btnLanzar.setOnAction(e -> ejecutarAccion(() -> juego.lanzarDados()));
        btnTerminar.setOnAction(e -> ejecutarAccion(() -> juego.acabarTurno()));
        btnTablero.setOnAction(e -> ejecutarAccion(() -> juego.verTablero()));
        btnSalir.setOnAction(e -> System.exit(0));

        // 3. Organizar los botones en una caja horizontal (HBox)
        javafx.scene.layout.HBox panelBotones = new javafx.scene.layout.HBox(10); // 10px de separación
        panelBotones.setPadding(new javafx.geometry.Insets(10)); // Margen interior
        panelBotones.setAlignment(javafx.geometry.Pos.CENTER); // Centrados
        panelBotones.getChildren().addAll(btnLanzar, btnTerminar, btnTablero, btnSalir);

        // --- D. MONTAJE FINAL (BorderPane) ---
        BorderPane organizador = new BorderPane();
        organizador.setCenter(textArea);  // Texto en el centro
        organizador.setBottom(panelBotones); // Botones abajo

        Scene scene = new Scene(organizador, 900, 700);
        stage.setTitle("How to Ruin Your Friends - GUI v1.0");
        stage.setScene(scene);
        stage.show();
    }

    // 1. Definimos nuestra propia interfaz que SÍ permite lanzar excepciones
    @FunctionalInterface
    interface AccionPeligrosa {
        void ejecutar() throws Exception;
    }

    // 2. Modificamos el método para usar nuestra interfaz en vez de Runnable
    private void ejecutarAccion(AccionPeligrosa accion) {
        try {
            accion.ejecutar(); // Ahora Java deja ejecutar esto aunque lance error
        } catch (Exception e) {
            // Si explota, capturamos el error y lo mostramos en la pantalla
            onError(e.getMessage());
        }
    }
    
    @Override
    public void onMensaje(String mensaje) {
        // Cuando el juego dice algo, lo añadimos al TextArea
        String mensajeLimpio = mensaje.replaceAll("\u001B\\[[;\\d]*m", "");
        textArea.appendText(mensajeLimpio + "\n");
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