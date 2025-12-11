package monopoly.casilla;

import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;
import monopoly.partida.Avatar;

import java.util.ArrayList;

public abstract class Casilla {

    // --- CONSTANTES (Las mantenemos para no romper Tablero.java) ---
    public static final String TSOLAR = "Solar";
    public static final String TESPECIAL = "Especial";
    public static final String TTRANSPORTE = "Transporte";
    public static final String TSERVICIOS = "Servicios";
    public static final String TCOMUNIDAD = "Comunidad";
    public static final String TSUERTE = "Suerte";
    public static final String TIMPUESTO = "Impuesto";

    // --- ATRIBUTOS COMUNES ---
    protected String nombre;
    protected String tipo; // Mantenemos 'tipo'
    protected int posicion;
    protected ArrayList<Avatar> avatares;
    protected int vecesVisitada = 0; // Para estadísticas

    // Variable estática para el bote del parking (usada por casillas de impuestos)
    private static Casilla parkingReferencia;

    // --- CONSTRUCTOR ---
    public Casilla(String nombre, String tipo, int posicion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.avatares = new ArrayList<>();
    }

    public int frecuenciaVisita() {
        return vecesVisitada;
    }

    @Override
    public abstract String toString();

    public void anhadirAvatar(Avatar av) {
        this.avatares.add(av);
    }

    public void eliminarAvatar(Avatar av) {
        this.avatares.remove(av);
    }

    public void incrementarVisita() {
        this.vecesVisitada++;
    }

    // --- GETTERS ---
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getPosicion() { return posicion; }
    public ArrayList<Avatar> getAvatares() { return avatares; }

    // Gestión del Parking estático
    public static Casilla getParkingReferencia() { return parkingReferencia; }
    public static void setParkingReferencia(Casilla c) { parkingReferencia = c; }

    // --- MÉTODOS ABSTRACTOS O DE COMPATIBILIDAD ---


    public abstract void evaluarCasilla(Jugador actual, Juego juego, int tirada) throws MonopolyEtseException;


    public int getValor() {
        return 0;
    }
    public void sumarValor(int cantidad) {} // Para que Parking pueda usarlo
}