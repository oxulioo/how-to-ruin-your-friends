package monopoly.logics.casilla;

import monopoly.logics.logica.Juego;
import monopoly.logics.jugador.Jugador;

public abstract class Accion extends Casilla {

    public Accion(String nombre, int posicion) {
        super(nombre, Casilla.TESPECIAL, posicion); // Usamos TESPECIAL como tipo genérico interno para no romper compatibilidad
    }

    // Dejamos evaluarCasilla abstracto para que cada acción defina qué hace
    @Override
    public abstract void evaluarCasilla(Jugador actual, Juego juego, int tirada);
}
