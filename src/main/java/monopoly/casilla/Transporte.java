package monopoly.casilla;

import monopoly.jugador.Jugador;

public class Transporte extends Propiedad {

    public Transporte(String nombre, int posicion, int valor, Jugador dueno) {

        super(nombre, Casilla.TTRANSPORTE, posicion, valor, dueno);
    }

    @Override
    public void alquiler(Jugador actual) {
        if (this.dueno == null) return;

        // Contamos cuantos transportes tiene el dueño
        int numTransportes = 0;
        for (Casilla c : this.dueno.getPropiedades()) {
            if (c instanceof Transporte) {
                numTransportes++;
            }
        }

        //  El alquiler es 250000 * número de transportes
        // Usamos el factor_pago para multiplicar el precio base
        actual.pagarAlquiler(this, numTransportes);
    }

    @Override
    public int valor() {
        return this.valor;
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso transporte)
        return "{\n"
                + "tipo: transporte,\n"
                + "valor: " + this.valor + "\n"
                + "}";
    }
}