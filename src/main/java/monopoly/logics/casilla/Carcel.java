package monopoly.logics.casilla;

import monopoly.logics.jugador.Jugador;
import monopoly.logics.partida.Valor;

public class Carcel extends Especial {

    public Carcel(int posicion) {
        super("Cárcel", posicion);
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso cárcel)
        int salir = Valor.PRECIO_SALIR_CARCEL;
        StringBuilder jugadores = new StringBuilder();

        if (this.avatares != null && !this.avatares.isEmpty()) { //busca los jugadores en la carcel
            for (int i = 0; i < this.avatares.size(); i++) {
                Jugador j = this.avatares.get(i).getJugador();
                String nom = (j == null) ? "-" : j.getNombre();
                if (i > 0) jugadores.append(" ");
                jugadores.append("[").append(nom).append("]");
            }
        } else {
            jugadores.append("[]");
        }

        return "{\n"
                + "salir: " + salir + ",\n"
                + "jugadores: " + jugadores + "\n"
                + "}";
    }
}