package monopoly.casilla;

import monopoly.Juego;
import monopoly.exceptions.MonopolyEtseException;
import monopoly.jugador.Jugador;

public class Suerte extends Accion {

    public Suerte(int posicion) {
        super("Suerte", posicion);
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada)  {
        this.incrementarVisita();
        juego.notificarMensaje(actual.getNombre() + " cae en Suerte.");

       try {
           juego.procesarCasillaEspecial(actual, "Suerte"); //llamamos para realizar la accion de suerte, para que nos de una carta
       } catch (MonopolyEtseException e){
           juego.notificarMensaje("Error en carta: " + e.getMessage());
       }
    }

    @Override
    public String toString() {
        return "{\n" +
                "tipo: suerte\n" +
                "}";
    }
}
