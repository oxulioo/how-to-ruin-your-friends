package monopoly.logics.casilla;

import monopoly.logics.logica.Juego;
import monopoly.logics.exceptions.MonopolyEtseException;
import monopoly.logics.jugador.Jugador;

public abstract class Especial extends Casilla {

    public Especial(String nombre, int posicion) {
        super(nombre, Casilla.TESPECIAL, posicion);
    }

    @Override
    // 2. AÑADIR throws MonopolyEtseException
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) throws MonopolyEtseException{}
}