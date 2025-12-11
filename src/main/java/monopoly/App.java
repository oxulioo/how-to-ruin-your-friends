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

    private javafx.scene.control.Label lblTurno;
    private javafx.scene.control.Label lblDinero;
    private javafx.scene.control.Label lblPosicion;

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
        javafx.scene.control.Button btnComprar = new javafx.scene.control.Button("💸 Comprar Propiedad");

        // Botones de Construcción Avanzada
        javafx.scene.control.Button btnCasa = new javafx.scene.control.Button("🏠 Edificar Casa");
        javafx.scene.control.Button btnHotel = new javafx.scene.control.Button("🏨 Edificar Hotel");
        javafx.scene.control.Button btnPiscina = new javafx.scene.control.Button("🏊 Edificar Piscina");
        javafx.scene.control.Button btnPista = new javafx.scene.control.Button("🎾 Edificar Pista Deportiva");

        // Botón de Finanzas
        javafx.scene.control.Button btnHipotecar = new javafx.scene.control.Button("📉 Hipotecar");
        javafx.scene.control.Button btnDeshipotecar = new javafx.scene.control.Button("📈 Deshipotecar");

        // 2. Darles acción (Conectar botón -> Método del Juego)
        btnLanzar.setOnAction(e -> ejecutarAccion(() -> juego.lanzarDados()));
        btnTerminar.setOnAction(e -> ejecutarAccion(() -> juego.acabarTurno()));
        btnTablero.setOnAction(e -> ejecutarAccion(() -> juego.verTablero()));
        btnSalir.setOnAction(e -> System.exit(0));
        btnComprar.setOnAction(e -> ejecutarAccion(() -> {
            // 1. Preguntamos al juego: "¿De quién es el turno?"
            // (Nota: Esto requiere que tengas getters en Juego.java, ahora los revisamos)
            monopoly.jugador.Jugador jugadorActual = juego.getJugadores().get(juego.getTurno());

            // 2. Preguntamos: "¿Dónde está este señor?"
            monopoly.casilla.Casilla casillaActual = jugadorActual.getAvatar().getPosicion();

            // 3. Ejecutamos la orden de compra con el nombre exacto
            juego.comprar(casillaActual.getNombre());
        }));
        // Acción de Edificar Casa
        btnCasa.setOnAction(e -> ejecutarAccion(() -> {
            // El método edificarCasa() de Juego ya sabe que tiene que construir
            // donde esté el jugador actual, así que es muy fácil:
            juego.edificarCasa();
        }));
        // Acción de Hipotecar (La casilla actual)
        btnHipotecar.setOnAction(e -> ejecutarAccion(() -> {
            // 1. Buscamos quién juega
            monopoly.jugador.Jugador actual = juego.getJugadores().get(juego.getTurno());
            // 2. Buscamos dónde está
            monopoly.casilla.Casilla casilla = actual.getAvatar().getPosicion();

            // 3. Ejecutamos la hipoteca
            // (El método hipotecar pide el NOMBRE de la casilla)
            juego.hipotecar(casilla.getNombre());
        }));
        // --- Acciones de Construcción ---
        btnHotel.setOnAction(e -> ejecutarAccion(() -> juego.edificarHotel()));
        btnPiscina.setOnAction(e -> ejecutarAccion(() -> juego.edificarPiscina()));
        btnPista.setOnAction(e -> ejecutarAccion(() -> juego.edificarPista()));
        // --- Acción de Deshipotecar (Lo contrario a hipotecar) ---
        btnDeshipotecar.setOnAction(e -> ejecutarAccion(() -> {
            // 1. Buscamos quién juega
            monopoly.jugador.Jugador actual = juego.getJugadores().get(juego.getTurno());
            // 2. Buscamos dónde está
            monopoly.casilla.Casilla casilla = actual.getAvatar().getPosicion();
            // 3. Ejecutamos la deshipoteca
            juego.deshipotecar(casilla.getNombre());
        }));


        // --- NUEVO: PANEL DE INFORMACIÓN LATERAL ---
        lblTurno = new javafx.scene.control.Label("Turno: -");
        lblTurno.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: blue;");

        lblDinero = new javafx.scene.control.Label("Dinero: - €");
        lblDinero.setStyle("-fx-font-size: 16px; -fx-text-fill: green;");

        lblPosicion = new javafx.scene.control.Label("Posición: Salida");
        lblPosicion.setStyle("-fx-font-size: 14px;");

        // Caja vertical para poner estos datos a la derecha
        javafx.scene.layout.VBox panelInfo = new javafx.scene.layout.VBox(20); // 20px de separación
        panelInfo.setPadding(new javafx.geometry.Insets(20));
        panelInfo.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;"); // Fondo grisáceo
        panelInfo.setPrefWidth(200); // Ancho fijo
        panelInfo.getChildren().addAll(new javafx.scene.control.Label("--- ESTADO ---"), lblTurno, lblDinero, lblPosicion);



        // --- ORGANIZACIÓN VISUAL EN DOS FILAS ---

        // Fila 1: Acciones de Turno y Básicas
        javafx.scene.layout.HBox filaSuperior = new javafx.scene.layout.HBox(10);
        filaSuperior.setAlignment(javafx.geometry.Pos.CENTER);
        filaSuperior.getChildren().addAll(btnLanzar, btnComprar, btnHipotecar, btnDeshipotecar, btnTerminar);

        // Fila 2: Construcción (Edificios) y Sistema
        javafx.scene.layout.HBox filaInferior = new javafx.scene.layout.HBox(10);
        filaInferior.setAlignment(javafx.geometry.Pos.CENTER);
        filaInferior.getChildren().addAll(btnCasa, btnHotel, btnPiscina, btnPista, btnTablero, btnSalir);

        // Contenedor Vertical (Columna que guarda las dos filas)
        javafx.scene.layout.VBox panelBotones = new javafx.scene.layout.VBox(10); // 10px de separación vertical
        panelBotones.setPadding(new javafx.geometry.Insets(15));
        panelBotones.getChildren().addAll(filaSuperior, filaInferior);

        // --- D. MONTAJE FINAL (BorderPane) ---
        BorderPane organizador = new BorderPane();
        organizador.setCenter(textArea);  // Texto en el centro
        organizador.setBottom(panelBotones); // Botones abajo
        organizador.setRight(panelInfo);

        if (!juego.getJugadores().isEmpty()) {
            onTurnoCambiado(juego.getJugadores().get(0).getNombre());
        }

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
        // Usamos Platform.runLater por si el error viene de otro hilo
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alerta.setTitle("⚠️ Error");
            alerta.setHeaderText(null); // Sin cabecera para ahorrar espacio
            alerta.setContentText(mensajeError);
            alerta.showAndWait(); // El juego se para hasta que le das a OK
        });
    }

    // El resto de métodos los dejamos vacíos por ahora (los usaremos cuando dibujemos el tablero)
    @Override public void onDadosLanzados(int d1, int d2, boolean db) {}
    @Override public void onPropiedadComprada(String j, String p, long pr) {}
    @Override public void onCambioEstadoCarcel(String j, boolean e) {}

    @Override
    public void onCartaRecibida(String tipo, String mensaje) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert carta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            carta.setTitle("🃏 Carta de " + tipo);
            carta.setHeaderText("Has cogido una carta...");
            carta.setContentText(mensaje);
            carta.showAndWait();
        });
    }

    // --- MÉTODOS DE ESCUCHA (Listeners) ---

    @Override
    public void onTurnoCambiado(String nuevoJugador) {
        // Platform.runLater es obligatorio para tocar la interfaz desde otro proceso
        javafx.application.Platform.runLater(() -> {
            lblTurno.setText("Turno: " + nuevoJugador);

            // TRUCO: Al cambiar de turno, leemos también el dinero y posición del nuevo jugador
            try {
                monopoly.jugador.Jugador j = juego.getJugadores().get(juego.getTurno());
                lblDinero.setText("Dinero: " + j.getFortuna() + " €");
                lblPosicion.setText("Posición: " + j.getAvatar().getPosicion().getNombre());
            } catch (Exception e) { /* Ignoramos fallos al iniciar */ }
        });
    }

    @Override
    public void onCambioFortuna(String nombreJugador, long fortunaActual, long cantidadCambio) {
        javafx.application.Platform.runLater(() -> {
            try {
                // Solo actualizamos si el cambio de dinero le pasó al jugador actual
                monopoly.jugador.Jugador actual = juego.getJugadores().get(juego.getTurno());
                if (actual.getNombre().equals(nombreJugador)) {
                    lblDinero.setText("Dinero: " + fortunaActual + " €");
                }
            } catch (Exception e) {}
        });
    }

    @Override
    public void onJugadorMovido(String nombreJugador, String nombreCasilla, int nuevaPosicion) {
        javafx.application.Platform.runLater(() -> {
            try {
                monopoly.jugador.Jugador actual = juego.getJugadores().get(juego.getTurno());
                if (actual.getNombre().equals(nombreJugador)) {
                    lblPosicion.setText("Posición: " + nombreCasilla);
                }
            } catch (Exception e) {}
        });
    }

    // Este main es necesario para engañar a Java y que arranque JavaFX
    public static void main(String[] args) {
        launch(); // Llama internamente al método start()
    }
}