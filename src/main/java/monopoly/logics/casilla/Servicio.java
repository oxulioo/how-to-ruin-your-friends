package monopoly.logics.casilla;

import monopoly.logics.logica.Juego;
import monopoly.logics.jugador.Jugador;

public class Servicio extends Propiedad {

    public Servicio(String nombre, int posicion, int valor, Jugador dueno) {
        // AÑADIMOS Casilla.TSERVICIOS
        super(nombre, Casilla.TSERVICIOS, posicion, valor, dueno);
    }

    @Override
    public void alquiler(Jugador j) {
        // Este método se define por herencia, pero en Servicio la lógica depende de la tirada.
        // Por tanto, la lógica real está en evaluarCasilla.
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();

        if (this.dueno != null && !this.dueno.equals(actual) && !this.dueno.getNombre().equals("Banca")) {
            // 1. Contar cuántos servicios tiene el dueño
            int numServicios = 0;
            for (Casilla c : this.dueno.getPropiedades()) {
                if (c instanceof Servicio) {
                    numServicios++;
                }
            }

            // 2. Definir el multiplicador (4 o 10) según la regla
            int multiplicador = (numServicios > 1) ? 10 : 4;

            int factor_pago = multiplicador * tirada;
            actual.pagarAlquiler(this, factor_pago);
        } else if (dueno == null || dueno.getNombre().equals("Banca")) {
            juego.notificarMensaje("Estás en " + nombre + ". Pertenece a la Banca.");
            juego.notificarMensaje("Valor de compra: " + valor);
        }
    }

    @Override
    public int valor() {
        return this.valor;
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso servicios)
        return "{\n"
                + "tipo: servicios,\n"
                + "valor: " + this.valor + "\n"
                + "}";
    }
}