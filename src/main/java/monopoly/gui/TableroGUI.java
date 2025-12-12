package monopoly.gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import monopoly.logics.casilla.Casilla;
import monopoly.logics.casilla.Solar;
import monopoly.logics.logica.Juego;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TableroGUI {

    private final GridPane grid;
    private final Juego juego;
    // Lista de contenedores de fichas (FlowPane) indexada por posición (0-39)
    private final List<FlowPane> contenedoresFichas = new ArrayList<>();


    // Medidas base (Vertical)
    private final int ANCHO_STD = 85;
    private final int LARGO_STD = 125;

    public TableroGUI(Juego juego) {
        this.juego = juego;
        this.grid = new GridPane();
        inicializarTablero();
    }

    public GridPane getNodoRaiz() { return grid; }

    public Pane getContenedor(int posicion) {
        if (posicion >= 0 && posicion < contenedoresFichas.size()) {
            return contenedoresFichas.get(posicion);
        }
        return null;
    }

    private void inicializarTablero() {
        grid.setAlignment(Pos.CENTER);
        // Color de fondo "tapete"
        grid.setStyle("-fx-background-color: #cde6d0; -fx-padding: 20; -fx-border-color: #2c3e50; -fx-border-width: 5;");
        // Sin huecos para que parezca continuo
        grid.setHgap(0);
        grid.setVgap(0);

        List<Casilla> casillasLogicas = new ArrayList<>();
        if (juego.getTablero() != null) {
            for (ArrayList<Casilla> lado : juego.getTablero().getPosiciones()) {
                casillasLogicas.addAll(lado);
            }
        }

        for (int i = 0; i < 40; i++) {
            // 1. Determinar Lado y Rotación
            int lado = 0; // 0:Sur, 1:Oeste, 2:Norte, 3:Este
            if (i > 10 && i < 20) lado = 1;
            else if (i > 20 && i < 30) lado = 2;
            else if (i > 30) lado = 3;

            // Esquinas
            boolean esEsquina = (i % 10 == 0);

            // 2. Crear la "Carta" interna (Siempre vertical 70x100 o cuadrada 100x100)
            // Luego la rotaremos.
            double w = esEsquina ? LARGO_STD : ANCHO_STD;
            double h = LARGO_STD;

            StackPane carta = new StackPane();
            carta.setPrefSize(w, h);
            carta.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 0.5;");

            // --- CONTENIDO DE LA CARTA ---
            String nombre = "C" + i;
            Casilla cLogica = (i < casillasLogicas.size()) ? casillasLogicas.get(i) : null;
            if (cLogica != null) nombre = cLogica.getNombre();

            // Intentar cargar Imagen
            boolean tieneImagen = cargarFondo(carta, nombre, w, h);

            // Si no hay imagen, diseño manual
            if (!tieneImagen) {
                VBox diseño = new VBox();
                diseño.setAlignment(Pos.TOP_CENTER); // Barra de color siempre ARRIBA en la carta base

            // ... dentro de inicializarTablero ...
                if (cLogica instanceof Solar) {
                    Pane barraColor = new Pane();
                    barraColor.setPrefSize(w, h * 0.25);
                    String color = ((Solar) cLogica).getGrupo().getColorGrupo();
                    barraColor.setStyle("-fx-background-color: " + obtenerColorHex(color) + "; -fx-border-color: black; -fx-border-width: 0 0 1 0;");

                    // --- AÑADIR ESTA LÍNEA AQUÍ ---
                    dibujarEdificios(barraColor, (Solar) cLogica);
                    // -----------------------------

                    diseño.getChildren().add(barraColor);
                } else {
                    // Espaciador si no hay color
                    Pane spacer = new Pane();
                    spacer.setPrefHeight(10);
                    diseño.getChildren().add(spacer);
                }

                Label lbl = new Label(nombre);
                lbl.setWrapText(true);
                lbl.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-alignment: center;");
                lbl.setMaxWidth(w - 4);
                diseño.getChildren().add(lbl);

                carta.getChildren().add(diseño);
            }

            // --- CONTENEDOR FICHAS (Dentro de la carta) ---
            FlowPane panelFichas = new FlowPane();
            panelFichas.setAlignment(Pos.CENTER);
            panelFichas.setHgap(2); panelFichas.setVgap(2);
            panelFichas.setUserData(nombre); // Para búsqueda
            contenedoresFichas.add(panelFichas);
            carta.getChildren().add(panelFichas);


            // 3. ROTACIÓN MÁGICA
            // Envolvemos la carta en un Group para que JavaFX recalcule los bordes al rotar
            Group rotador = new Group(carta);

            // Calculamos rotación
            if (esEsquina) {
                if (i == 10) carta.setRotate(90);       // Cárcel
                else if (i == 20) carta.setRotate(180); // Parking
                else if (i == 30) carta.setRotate(-90); // Ir Cárcel
            } else {
                if (lado == 1) carta.setRotate(90);      // OESTE
                else if (lado == 2) carta.setRotate(180); // NORTE
                else if (lado == 3) carta.setRotate(-90); // ESTE
            }

            // 4. CONTENEDOR FINAL (CELDA DEL GRID)
            // Este contenedor sí tiene las medidas finales corregidas.
            // Si es lateral, el ancho es 100 y alto 70.
            StackPane celdaGrid = new StackPane(rotador);

            if (lado == 1 || lado == 3) {
                // Laterales: Invertimos dimensiones
                celdaGrid.setPrefSize(LARGO_STD, ANCHO_STD); // 100 ancho, 70 alto
            } else {
                // Arriba/Abajo/Esquinas
                celdaGrid.setPrefSize(w, h);
            }

            // Colocar en Grid
            colocarEnGrid(celdaGrid, i);
        }
    }

    private void colocarEnGrid(StackPane p, int i) {
        int x = 0, y = 0;
        if (i >= 0 && i <= 10) {        x = 10 - i; y = 10; } // SUR
        else if (i > 10 && i <= 20) {   x = 0; y = 10 - (i - 10); } // OESTE
        else if (i > 20 && i <= 30) {   x = i - 20; y = 0; } // NORTE
        else if (i > 30 && i < 40) {    x = 10; y = i - 30; } // ESTE
        grid.add(p, x, y);
    }

    private boolean cargarFondo(Pane padre, String nombre, double w, double h) {
        try {
            String ruta = "/imagenes/" + nombre.replace(" ", "") + ".jpg";
            InputStream is = getClass().getResourceAsStream(ruta);
            if (is == null) is = getClass().getResourceAsStream(ruta.replace(".jpg", ".png"));

            if (is != null) {
                ImageView img = new ImageView(new Image(is));
                img.setFitWidth(w);
                img.setFitHeight(h);
                padre.getChildren().add(img);
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    public void resaltarCasilla(String nombrePropiedad, String colorBorde) {
        for (FlowPane panelFichas : contenedoresFichas) {
            if (nombrePropiedad.equals(panelFichas.getUserData())) {
                // La jerarquía ahora es: FlowPane -> StackPane(Carta) -> Group -> StackPane(Celda)
                // Buscamos la Carta (abuelo) para pintarle el borde
                if (panelFichas.getParent() instanceof StackPane carta) {
                    carta.setStyle("-fx-background-color: white; -fx-border-color: " + colorBorde + "; -fx-border-width: 4;");
                }
                break;
            }
        }
    }

    private String obtenerColorHex(String c) {
        if (c == null) return "#ddd";
        return switch (c.toLowerCase()) {
            case "marron" -> "#8B4513";
            case "cian" -> "#00FFFF";
            case "rosa" -> "#FF69B4";
            case "naranja" -> "#FFA500";
            case "rojo" -> "#FF0000";
            case "amarillo" -> "#FFD700";
            case "verde" -> "#008000";
            case "azul" -> "#0000FF";
            default -> "#ddd";
        };
    }

// Método principal de dibujo de edificios
private void dibujarEdificios(Pane contenedor, Solar solar) {
    HBox filaCasas = new HBox(-10); // 1px de separación
    filaCasas.setAlignment(Pos.CENTER);
    // Ajustamos tamaño para que no se salga de la barra de color
    filaCasas.setPrefSize(contenedor.getPrefWidth(), contenedor.getPrefHeight());

    // 1. CASAS (Hasta 4)
    if (solar.getNumCasas() > 0) {
        for (int i = 0; i < solar.getNumCasas(); i++) {
            // Intenta cargar "casa.png", si no hay, pinta un cuadrado VERDE
            filaCasas.getChildren().add(crearIcono("casa", Color.LIMEGREEN, 20));
        }
    }

    // 2. HOTEL (1)
    if (solar.getNumHoteles() > 0) {
        // Intenta cargar "hotel.png", si no hay, pinta un cuadrado ROJO
        filaCasas.getChildren().add(crearIcono("hotel", Color.RED, 25));
    }

    // 3. PISCINA (1)
    if (solar.getNumPiscinas() > 0) {
        // Intenta cargar "piscina.png", si no hay, pinta un circulo CYAN
        filaCasas.getChildren().add(crearIcono("piscina", Color.CYAN, 25));
    }

    // 4. PISTA (1)
    if (solar.getNumPistas() > 0) {
        // Intenta cargar "pista.png", si no hay, pinta un cuadrado NARANJA
        filaCasas.getChildren().add(crearIcono("pista", Color.ORANGE, 25));
    }

    contenedor.getChildren().add(filaCasas);
}

    // Método auxiliar inteligente: Busca imagen -> Si falla, crea forma geométrica
    private javafx.scene.Node crearIcono(String nombreArchivo, Color colorBackup, double tamaño) {
        try {
            // 1. Intentamos cargar la imagen (casa.png, hotel.png...)
            String ruta = "/imagenes/" + nombreArchivo + ".png";
            InputStream is = getClass().getResourceAsStream(ruta);

            // Soporte para .jpg o .jpeg por si acaso
            if (is == null) is = getClass().getResourceAsStream("/imagenes/" + nombreArchivo + ".jpg");
            if (is == null) is = getClass().getResourceAsStream("/imagenes/" + nombreArchivo + ".jpeg");

            if (is != null) {
                ImageView icono = new ImageView(new Image(is));
                icono.setFitWidth(tamaño);
                icono.setFitHeight(tamaño);
                icono.setPreserveRatio(true);
                // Le damos una sombrita para que destaque sobre el color de fondo
                icono.setEffect(new javafx.scene.effect.DropShadow(2, Color.BLACK));
                return icono;
            }
        } catch (Exception e) {
            // Ignoramos error y vamos al plan B
        }
        return null;
    }
}