package monopoly.logics.partida;

import monopoly.logics.casilla.Casilla;
import monopoly.logics.jugador.Jugador;

import java.util.ArrayList;

public class Avatar {


    private char id;
    private final String tipo;
    private Casilla lugar;
    private final Jugador jugador;

    public Avatar(String tipo, Casilla posicion, Jugador jugador, ArrayList<Avatar> avataresCreados) {
        this.tipo = tipo;
        this.lugar = posicion;
        try { if (posicion != null) posicion.anhadirAvatar(this); } catch (Throwable ignored) {}
        this.jugador = jugador;
        this.generarId(avataresCreados);
    }

    public char getID() {
        return id;
    }
    public String getTipo() {
        return tipo;
    }
    public Casilla getPosicion() {
        return lugar;
    }
    public Jugador getJugador() {
        return jugador;
    }
    public void setPosicion(Casilla posicion) {
        if (this.lugar == posicion) return; // no hacemos nada

        try {
            if (this.lugar != null) this.lugar.eliminarAvatar(this); //lo sacamos de la casilla anterior
        } catch (Throwable ignored) {}
        // ponemos en la nueva
        this.lugar = posicion;
        // añadir a la casilla nueva (una sola vez)
        try {
            if (this.lugar != null) this.lugar.anhadirAvatar(this);
        } catch (Throwable ignored) {}
    }



    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) {

        // Comprobacion previa
        if(casillas==null||this.lugar==null){
            jugador.hablar("No se puede mover el avatar, el tablero o la posición es nulo");
            return;
        }

        int totalPosiciones = 0;
        for (ArrayList<Casilla> fila: casillas) {
            totalPosiciones += fila.size();
        }
        if(totalPosiciones==0) return;

        Casilla anteriorCasilla = this.lugar;
        int posActual= this.lugar.getPosicion()-1;

        int nuevaPosicion = (posActual + valorTirada) % totalPosiciones;

        if(posActual + valorTirada >= totalPosiciones){
            jugador.sumarFortuna(Valor.SUMA_VUELTA);//Uso la constante definida para la vuelta
            jugador.getEstadisticas().sumarPasarPorSalida();
            jugador.setVueltas(jugador.getVueltas()+1);
            jugador.hablar("El jugador " + jugador.getNombre() + " pasa por salida y recibe 2.000.000€.");
        }
        int nuevaPosicionActual=nuevaPosicion+1;
        Casilla nuevaCasilla = null;
        for (ArrayList<Casilla> fila: casillas) {
            for (Casilla casilla: fila) {
                if (casilla!=null && casilla.getPosicion() == nuevaPosicionActual){ //Compruebo que la casilla no es null tampoco
                    nuevaCasilla = casilla;
                    break;
                }
            }
            if(nuevaCasilla != null){
                break;
            }
        }
        if(nuevaCasilla != null) {
            //Actualizo la lista de avatares (para imprimir en el tablero)
            setPosicion(nuevaCasilla);
            jugador.hablar("El avatar " + this.id + " avanza " + valorTirada + " casillas desde la casilla "+anteriorCasilla.getNombre()+" hasta la casilla " + nuevaCasilla.getNombre() + ".");
        }else {
            jugador.hablar("No se encuentra la casilla pedida.");
        }
    }

    private void generarId(ArrayList<Avatar> avCreados) {
        char id;
        do {
            id = generarLetraMayuscula();
        } while (existeId(avCreados, id));
        this.id = id;
        avCreados.add(this);
    }

    private boolean existeId(ArrayList<Avatar> avCreados, char id) {
        for (Avatar avatar : avCreados) {
            if (avatar.id == id) {
                return true;
            }
        }
        return false;
    }

    private char generarLetraMayuscula() {
        // devuelve una letra aleatoria entre A y Z
        return (char) (Math.random() * 26 + 65);
    }

}
