package monopoly.logics.casilla;

import monopoly.logics.logica.Juego;
import monopoly.logics.exceptions.MonopolyEtseException;
import monopoly.logics.jugador.Jugador;

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
