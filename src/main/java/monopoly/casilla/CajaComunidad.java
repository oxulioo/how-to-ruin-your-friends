package monopoly.casilla;

import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

public class CajaComunidad extends Accion {

    public CajaComunidad(int posicion) {
        super("Caja", posicion); // Mantenemos el nombre "Caja" para coincidir con tu lógica de Tablero/Carta
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        juego.notificarMensaje(actual.getNombre() + " cae en Caja de Comunidad.");
        // Delegamos en Juego la gestión de la carta
        try {
            juego.procesarCasillaEspecial(actual, "Comunidad");
        } catch (MonopolyEtseException e) {
            juego.notificarMensaje("Error en carta: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "{\n" +
                "tipo: comunidad\n" +
                "}";
    }
}
