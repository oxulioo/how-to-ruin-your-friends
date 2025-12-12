package monopoly;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    @Override
    public void start(Stage stage) {
        juego = new Juego();
        juego.addListener(this);

        // GUI Components
        tableroGui = new TableroGUI(juego);
        panelInfo = new PanelInfo();

        areaLog = new TextArea();
        areaLog.setEditable(false);
        areaLog.setPrefHeight(40);
        areaLog.setStyle("-fx-font-family: 'monospaced'; -fx-font-size: 11;");

        // Inicializar Partida
        try {
            juego.crearJugador("Pedro", "pelota");
            juego.crearJugador("Maria", "coche");

            // Crear Fichas Visuales
            crearFicha("Pedro", "pelota");
            crearFicha("Maria", "coche");

            if (!juego.getJugadores().isEmpty()) {
                onTurnoCambiado(juego.getJugadores().get(0).getNombre());
            }
        } catch (Exception e) { onError(e.getMessage()); }

        // Layout Principal
        BorderPane root = new BorderPane();
        root.setCenter(tableroGui.getNodoRaiz());
        root.setRight(panelInfo.getNodoRaiz());
        root.setTop(areaLog);
        root.setBottom(crearBotonera());

        Scene scene = new Scene(root);
        stage.setTitle("Monopoly - GUI Final");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    private void crearFicha(String nombre, String tipo) {
        FichaGUI ficha = new FichaGUI(nombre, tipo);
        fichasVisuales.put(nombre, ficha);
        // Colocar en Salida (Posición 0)
        ficha.colocarEn(tableroGui.getContenedor(0));
    }

    private VBox crearBotonera() {
        // --- 1. CREACIÓN DE TODOS LOS BOTONES ---
        // Acciones de Turno
        Button btnLanzar = new Button("🎲 Lanzar");
        Button btnTerminar = new Button("⏭ Terminar");
        Button btnSalir = new Button("❌ Salir");

        // Acciones de Propiedad
        Button btnComprar = new Button("💸 Comprar");
        Button btnHipotecar = new Button("📉 Hipotecar");
        Button btnDeshipotecar = new Button("📈 Deshipotecar");

        // Acciones de Construcción
        Button btnCasa = new Button("🏠 Casa");
        Button btnHotel = new Button("🏨 Hotel");
        Button btnPiscina = new Button("🏊 Piscina");
        Button btnPista = new Button("🎾 Pista");

        // --- 2. ASIGNACIÓN DE ACCIONES ---
        btnLanzar.setOnAction(e -> safeRun(() -> juego.lanzarDados()));
        btnTerminar.setOnAction(e -> safeRun(() -> juego.acabarTurno()));
        btnSalir.setOnAction(e -> System.exit(0));

        // Comprar: Detecta dónde estás y compra
        btnComprar.setOnAction(e -> safeRun(() -> {
            var actual = juego.getJugadores().get(juego.getTurno());
            juego.comprar(actual.getAvatar().getPosicion().getNombre());
        }));

        // Finanzas: Detecta dónde estás e hipoteca/deshipoteca
        btnHipotecar.setOnAction(e -> safeRun(() -> {
            var actual = juego.getJugadores().get(juego.getTurno());
            juego.hipotecar(actual.getAvatar().getPosicion().getNombre());
        }));
        btnDeshipotecar.setOnAction(e -> safeRun(() -> {
            var actual = juego.getJugadores().get(juego.getTurno());
            juego.deshipotecar(actual.getAvatar().getPosicion().getNombre());
        }));

        // Construcción
        btnCasa.setOnAction(e -> safeRun(() -> juego.edificarCasa()));
        btnHotel.setOnAction(e -> safeRun(() -> juego.edificarHotel()));
        btnPiscina.setOnAction(e -> safeRun(() -> juego.edificarPiscina()));
        btnPista.setOnAction(e -> safeRun(() -> juego.edificarPista()));

        // --- 3. ORGANIZACIÓN VISUAL (Dos filas) ---
        // Fila 1: Movimiento y Gestión financiera
        HBox fila1 = new HBox(10, btnLanzar, btnComprar, btnHipotecar, btnDeshipotecar, btnTerminar);
        fila1.setAlignment(Pos.CENTER);

        // Fila 2: Construcción y Sistema
        HBox fila2 = new HBox(10, btnCasa, btnHotel, btnPiscina, btnPista, btnSalir);
        fila2.setAlignment(Pos.CENTER);

        // Panel final con un poco de separación
        VBox panel = new VBox(10, fila1, fila2);
        panel.setPadding(new Insets(15));

        return panel;
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

    // --- UTILS ---
    @Override public void onDadosLanzados(int d1, int d2, boolean db) {}
    @Override public void onCambioEstadoCarcel(String j, boolean e) {}

    interface Accion { void run() throws Exception; }
    private void safeRun(Accion a) {
        try { a.run(); } catch (Exception e) { onError(e.getMessage()); }
    }

    public static void main(String[] args) { launch(); }
}