package monopoly.casilla;

import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

public class IrCarcel extends Especial {

    public IrCarcel(int posicion) {
        super("IrCarcel", posicion);
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) throws MonopolyEtseException {
        this.incrementarVisita();
        juego.enviarACarcel(actual);
    }

    @Override
    public String toString() {
        return "{\n"
                + "tipo: especial,\n"
                + "nombre: " + nombre + "\n"
                + "}";
    }
}
