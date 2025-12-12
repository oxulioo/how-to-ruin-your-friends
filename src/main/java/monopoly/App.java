package monopoly;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// IMPORTAMOS LOS NUEVOS COMPONENTES
import monopoly.gui.FichaGUI;
import monopoly.gui.PanelInfo;
import monopoly.gui.TableroGUI;
import monopoly.logics.logica.Juego;

import java.util.HashMap;
import java.util.Map;

public class App extends Application implements JuegoListener {

    private Juego juego;
    private TableroGUI tableroGui;
    private PanelInfo panelInfo;
    private TextArea areaLog;
    private final Map<String, FichaGUI> fichasVisuales = new HashMap<>();
    private javafx.scene.layout.StackPane centroStack;
    private monopoly.gui.CartaAnimacion cartaAnimacion;

    private static final double ANCHO_IDEAL = 1920;
    private static final double ALTO_IDEAL = 1080;

    @Override
    public void start(Stage stage) {
        juego = new Juego();
        juego.addListener(this);

        // GUI Components
        tableroGui = new TableroGUI(juego);
        panelInfo = new PanelInfo();

        // LOG (Chat)
        areaLog = new TextArea();
        areaLog.setEditable(false);
        areaLog.setWrapText(true);
        areaLog.setPrefHeight(400);
        areaLog.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 14px;"); // Fuente un poco más grande

        // Inicializar Partida
        try {
            juego.crearJugador("Pedro", "ficha1");
            juego.crearJugador("Maria", "ficha2");
            crearFicha("Pedro", "ficha1");
            crearFicha("Maria", "ficha2");
            if (!juego.getJugadores().isEmpty()) onTurnoCambiado(juego.getJugadores().get(0).getNombre());
        } catch (Exception e) { onError(e.getMessage()); }

        // --- 1. CREAMOS LA INTERFAZ A TAMAÑO FIJO (1920x1080) ---
        BorderPane layoutJuego = new BorderPane();
        layoutJuego.setPrefSize(ANCHO_IDEAL, ALTO_IDEAL); // Forzamos el tamaño de diseño

        // Izquierda
        VBox panelIzquierdo = new VBox(15);
        panelIzquierdo.setPadding(new Insets(20));
        panelIzquierdo.setStyle("-fx-background-color: #dfe6e9; -fx-border-color: #b2bec3; -fx-border-width: 0 2 0 0;");
        panelIzquierdo.setPrefWidth(320); // Un poco más ancho para que se vea bien
        panelIzquierdo.getChildren().addAll(new javafx.scene.control.Label("--- HISTORIAL ---"), areaLog, crearBotoneraVertical());
        panelIzquierdo.setAlignment(Pos.TOP_CENTER);

        layoutJuego.setLeft(panelIzquierdo);
        layoutJuego.setRight(panelInfo.getNodoRaiz());

        // Centro (Tablero)
        // Ya no necesitamos el zoom complejo de antes, porque escalaremos TODA la ventana.
        // Simplemente centramos el tablero.
        StackPane marcoCentral = new StackPane(tableroGui.getNodoRaiz());
        marcoCentral.setStyle("-fx-background-color: #2d3436;");
        layoutJuego.setCenter(marcoCentral);

        // Referencias para animaciones
        this.centroStack = marcoCentral;
        this.cartaAnimacion = new monopoly.gui.CartaAnimacion(centroStack);


        // --- 2. LA MAGIA: EL CONTENEDOR ESCALABLE ---
        // Usamos un StackPane negro de fondo (para las barras negras si sobran bordes)
        StackPane rootScaler = new StackPane(layoutJuego);
        rootScaler.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(rootScaler);

        // Listener que detecta cambios de tamaño de ventana y escala todo el contenido
        javafx.beans.value.ChangeListener<Number> escalador = (obs, oldVal, newVal) -> {
            double anchoVentana = scene.getWidth();
            double altoVentana = scene.getHeight();

            // Calculamos cuánto tenemos que escalar para que quepa (manteniendo proporción)
            double escalaX = anchoVentana / ANCHO_IDEAL;
            double escalaY = altoVentana / ALTO_IDEAL;
            double escala = Math.min(escalaX, escalaY); // Elegimos el menor para que no se corte nada

            layoutJuego.setScaleX(escala);
            layoutJuego.setScaleY(escala);
        };

        scene.widthProperty().addListener(escalador);
        scene.heightProperty().addListener(escalador);

        stage.setTitle("Monopoly - Full Screen Scaled");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    // Método mágico para escalar el tablero
    private void ajustarEscalaTablero(Pane contenedor, javafx.scene.Node contenido) {
        double anchoDisponible = contenedor.getWidth();
        double altoDisponible = contenedor.getHeight();

        if (anchoDisponible == 0 || altoDisponible == 0) return;

        // Tamaño real de tu tablero (aprox 720x720 con las medidas actuales)
        double tamanoReal = contenido.getBoundsInLocal().getWidth();
        // Si aún no se ha dibujado, usamos un estimado base para evitar dividir por 0
        if (tamanoReal == 0) tamanoReal = 800;

        // Queremos que quepa en el lado más pequeño (ancho o alto)
        double menorLado = Math.min(anchoDisponible, altoDisponible);

        // Calculamos factor de zoom (dejamos un 5% de margen para que no toque los bordes)
        double factorZoom = (menorLado / tamanoReal) * 0.95;

        contenido.setScaleX(factorZoom);
        contenido.setScaleY(factorZoom);
    }

    // Nueva botonera vertical para que quepa bien a la izquierda
    private VBox crearBotoneraVertical() {
        Button btnLanzar = new Button("🎲 LANZAR");
        btnLanzar.setMaxWidth(Double.MAX_VALUE); // Que ocupe todo el ancho
        btnLanzar.setStyle("-fx-font-size: 14px; -fx-base: #74b9ff; -fx-font-weight: bold;");

        Button btnTerminar = new Button("⏭ TERMINAR");
        btnTerminar.setMaxWidth(Double.MAX_VALUE);

        Button btnComprar = new Button("💸 COMPRAR");
        btnComprar.setMaxWidth(Double.MAX_VALUE);
        btnComprar.setStyle("-fx-base: #55efc4;"); // Verde menta

        // Botones de gestión
        Button btnHipotecar = new Button("📉 Hipotecar");
        Button btnDeshipotecar = new Button("📈 Deshipotecar");
        HBox boxFinanzas = new HBox(5, btnHipotecar, btnDeshipotecar);
        boxFinanzas.setAlignment(Pos.CENTER);

        // Botones de construcción (Grid 2x2 para ahorrar espacio)
        Button btnCasa = new Button("🏠");
        Button btnHotel = new Button("🏨");
        Button btnPiscina = new Button("🏊");
        Button btnPista = new Button("🎾");

        // Tooltips para que se sepa qué son los iconos
        btnCasa.setTooltip(new javafx.scene.control.Tooltip("Construir Casa"));
        btnHotel.setTooltip(new javafx.scene.control.Tooltip("Construir Hotel"));

        HBox boxConstruccion = new HBox(5, btnCasa, btnHotel, btnPiscina, btnPista);
        boxConstruccion.setAlignment(Pos.CENTER);

        Button btnTruco = new Button("⚡ CHEAT");
        Button btnSalir = new Button("❌ SALIR");
        HBox boxSistema = new HBox(5, btnTruco, btnSalir);
        boxSistema.setAlignment(Pos.CENTER);

        // Asignar Acciones (Igual que antes)
        btnLanzar.setOnAction(e -> safeRun(() -> juego.lanzarDados()));
        btnTerminar.setOnAction(e -> safeRun(() -> juego.acabarTurno()));
        btnSalir.setOnAction(e -> System.exit(0));
        btnComprar.setOnAction(e -> safeRun(() -> {
            var actual = juego.getJugadores().get(juego.getTurno());
            juego.comprar(actual.getAvatar().getPosicion().getNombre());
        }));
        btnHipotecar.setOnAction(e -> safeRun(() -> {
            var actual = juego.getJugadores().get(juego.getTurno());
            juego.hipotecar(actual.getAvatar().getPosicion().getNombre());
        }));
        btnDeshipotecar.setOnAction(e -> safeRun(() -> {
            var actual = juego.getJugadores().get(juego.getTurno());
            juego.deshipotecar(actual.getAvatar().getPosicion().getNombre());
        }));
        btnCasa.setOnAction(e -> safeRun(() -> juego.edificarCasa()));
        btnHotel.setOnAction(e -> safeRun(() -> juego.edificarHotel()));
        btnPiscina.setOnAction(e -> safeRun(() -> juego.edificarPiscina()));
        btnPista.setOnAction(e -> safeRun(() -> juego.edificarPista()));

        btnTruco.setOnAction(e -> {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Truco");
            dialog.setHeaderText("Completar Grupo");
            dialog.setContentText("Color:");
            dialog.showAndWait().ifPresent(c -> safeRun(() -> juego.cheatCompletarGrupo(c)));
        });

        // Contenedor Vertical
        VBox panel = new VBox(10);
        panel.getChildren().addAll(
                new javafx.scene.control.Separator(),
                new javafx.scene.control.Label("--- JUEGO ---"),
                btnLanzar, btnComprar, btnTerminar,
                new javafx.scene.control.Label("--- GESTIÓN ---"),
                boxFinanzas,
                new javafx.scene.control.Label("--- CONSTRUIR ---"),
                boxConstruccion,
                new javafx.scene.control.Separator(),
                boxSistema
        );
        return panel;
    }

    private void crearFicha(String nombre, String tipo) {
        FichaGUI ficha = new FichaGUI(nombre, tipo);
        fichasVisuales.put(nombre, ficha);
        // Colocar en Salida (Posición 0)
        ficha.colocarEn(tableroGui.getContenedor(0));
    }

    // --- LISTENERS ---

    @Override
    public void onPropiedadComprada(String nombreJugador, String nombrePropiedad, long precio) {
        Platform.runLater(() -> {
            // 1. Pintar el borde en el tablero (Acción visual principal)
            String color = nombreJugador.equalsIgnoreCase("Pedro") ? "red" : "blue";
            tableroGui.resaltarCasilla(nombrePropiedad, color);

            // 2. Feedback en el LOG (Chat) en lugar de Popup intrusivo
            areaLog.appendText("\n💰 ¡COMPRA REALIZADA! 💰\n");
            areaLog.appendText(nombreJugador + " es ahora dueño de " + nombrePropiedad + "\n");
            areaLog.appendText("Precio pagado: " + precio + "€\n\n");

            // OPCIONAL: Si quieres un sonido, aquí iría.
            // NO lanzamos Alert() para no interrumpir el flujo.
        });
    }

    @Override
    public void onJugadorMovido(String j, String c, int n) {
        Platform.runLater(() -> {
            // Fix índice para mover ficha
            int idx = (n == 0) ? 0 : n - 1;
            if(fichasVisuales.containsKey(j)) {
                fichasVisuales.get(j).colocarEn(tableroGui.getContenedor(idx));
            }
            // Actualizar info
            var actual = juego.getJugadores().get(juego.getTurno());
            if (actual.getNombre().equals(j)) panelInfo.actualizarPosicion(c);
        });
    }

    @Override
    public void onTurnoCambiado(String j) {
        Platform.runLater(() -> {
            panelInfo.actualizarTurno(j);
            try {
                var jug = juego.getJugadores().get(juego.getTurno());
                panelInfo.actualizarDinero(jug.getFortuna());
                panelInfo.actualizarPosicion(jug.getAvatar().getPosicion().getNombre());
            } catch (Exception e) {}
        });
    }

    @Override
    public void onCambioFortuna(String j, long f, long c) {
        Platform.runLater(() -> {
            try {
                var actual = juego.getJugadores().get(juego.getTurno());
                if (actual.getNombre().equals(j)) panelInfo.actualizarDinero(f);
            } catch (Exception e) {}
        });
    }

    @Override
    public void onMensaje(String m) {
        String limpio = m.replaceAll("\u001B\\[[;\\d]*m", "");
        Platform.runLater(() -> areaLog.appendText(limpio + "\n"));
    }

    @Override
    public void onError(String m) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(m);
            a.showAndWait();
        });
    }

    @Override
    public void onCartaRecibida(String t, String m) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Carta " + t);
            a.setHeaderText("Evento de " + t);
            a.setContentText(m);
            a.showAndWait();
        });
    }

    @Override
    public void onEdificioConstruido(String nombreCasilla) {
        Platform.runLater(() -> {
            // 1. Limpiamos el contenedor del tablero (centroStack)
            // Ya no buscamos la raíz de la escena, usamos la referencia directa.
            this.centroStack.getChildren().clear();

            // 2. Creamos el nuevo tablero actualizado
            this.tableroGui = new TableroGUI(juego);

            // 3. Lo añadimos al contenedor central
            this.centroStack.getChildren().add(this.tableroGui.getNodoRaiz());

            // 4. Recolocamos las fichas de los jugadores (igual que antes)
            for (monopoly.logics.jugador.Jugador j : juego.getJugadores()) {
                if (fichasVisuales.containsKey(j.getNombre())) {
                    FichaGUI ficha = fichasVisuales.get(j.getNombre());

                    // Protección contra nulos por si acaso
                    if (j.getAvatar() != null && j.getAvatar().getPosicion() != null) {
                        int pos = j.getAvatar().getPosicion().getPosicion();
                        int idx = (pos == 0) ? 0 : pos - 1;
                        ficha.colocarEn(tableroGui.getContenedor(idx));
                    }
                }
            }

            areaLog.appendText("🏗️ Obra finalizada en " + nombreCasilla + "\n");
        });
    }

    // --- UTILS ---
    @Override public void onDadosLanzados(int d1, int d2, boolean db) {}
    @Override public void onCambioEstadoCarcel(String j, boolean e) {}

    interface Accion { void run() throws Exception; }
    private void safeRun(Accion a) {
        try { a.run(); } catch (Exception e) { onError(e.getMessage()); }
    }

    public static void main(String[] args) { launch(); }
}