package monopoly.casilla;

import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

public abstract class Especial extends Casilla {

    public Especial(String nombre, int posicion) {
        super(nombre, Casilla.TESPECIAL, posicion);
    }

    @Override
    // 2. AÑADIR throws MonopolyEtseException
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) throws MonopolyEtseException{}
}