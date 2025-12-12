package monopoly.logics.casilla;

import monopoly.logics.logica.Juego;
import monopoly.logics.jugador.Jugador;

public class Parking extends Accion {

    private int bote; // El valor acumulado

    public Parking(int posicion, int valorInicial) {
        super("Parking", posicion);
        this.bote = valorInicial;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        if(this.getValor() > 0){ // getValor() devuelve el bote,
            int premio = this.getValor();
            actual.sumarFortuna(premio);
            actual.getEstadisticas().sumarPremiosInversionesOBote(premio);
            // Como el atributo bote es privado en Parking, lo manejamos aquí:
            this.bote = 0;
            juego.notificarMensaje(actual.getNombre() + " cae en el Parking y se lleva el bote de " + premio + "€.");
        } else {
            juego.notificarMensaje(actual.getNombre() + " cae en el Parking, pero el bote está a 0€.");
        }
    }

    // --- MÉTODOS PARA GESTIONAR EL BOTE ---
    // Impuesto.java llamará a estos métodos a través de la referencia estática en Casilla

    @Override
    public void sumarValor(int suma) {
        this.bote += suma;
    }

    @Override
    public int getValor() {
        return this.bote;
    }

    @Override
    public String toString() {
        StringBuilder jugadores = new StringBuilder();
        if (this.avatares != null && !this.avatares.isEmpty()) { //avatares que se encuentran en ese momento en el parking
            for (int i = 0; i < this.avatares.size(); i++) {
                Jugador j = this.avatares.get(i).getJugador();
                String nombre = (j == null) ? "-" : j.getNombre();
                if (i > 0) jugadores.append(", ");
                jugadores.append(nombre);
            }
        }

        return "{\n"
                + "bote: " + this.bote + ",\n"
                + "jugadores: [" + jugadores + "]\n"
                + "}";
    }
}
