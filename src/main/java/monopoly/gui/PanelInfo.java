package monopoly.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PanelInfo {

    private final VBox panel;
    private final Label lblTurno;
    private final Label lblDinero;
    private final Label lblPosicion;
    private final Label lblTitulo;

    public PanelInfo() {
        lblTurno = new Label("Turno: -");
        lblTurno.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: blue;");

        lblDinero = new Label("Dinero: - €");
        lblDinero.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        lblPosicion = new Label("Posición: Salida");
        lblPosicion.setStyle("-fx-font-size: 18px;");

        lblTitulo = new Label("--- ESTADO ---");
        lblTitulo.setStyle("-fx-font-size: 18px;");

        panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        panel.setPrefWidth(800); // Un poco más ancho

        panel.getChildren().addAll(lblTitulo, lblTurno, lblDinero, lblPosicion);
    }

    public VBox getNodoRaiz() { return panel; }

    public void actualizarTurno(String t) { lblTurno.setText("Turno: " + t); }
    public void actualizarDinero(long d) { lblDinero.setText("Dinero: " + d + " €"); }
    public void actualizarPosicion(String p) { lblPosicion.setText("Posición: " + p); }
}