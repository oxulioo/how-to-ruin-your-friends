package monopoly.logics.carta;

import monopoly.logics.logica.Juego;
import monopoly.logics.exceptions.MonopolyEtseException;
import monopoly.logics.jugador.Jugador;

public abstract class Carta {
    protected final int id;
    protected final String descripcion;

    public Carta(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }

    // Método abstracto: cada hijo definirá qué hace
    public abstract void accion(Jugador jugador, Juego juego) throws MonopolyEtseException;
}