package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;
import monopoly.partida.Valor;

public class Impuesto extends Casilla {

    private final int valor; // El impuesto definido

    public Impuesto(String nombre, int posicion, int valor) {
        super(nombre, Casilla.TIMPUESTO, posicion);
        this.valor = valor;
    }

    @Override
    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita();
        int cantidad = (this.valor > 0) ? this.valor : Valor.IMPUESTO_FIJO;
        juego.notificarMensaje("Jugador actual: " + actual.getNombre() + " debe pagar: " + cantidad);

            // Actualizar estadísticas
        actual.restarDinero(cantidad);
        actual.getEstadisticas().sumarPagoTasasImpuestos(cantidad);

        // Añadir al bote del Parking
        // Usamos la referencia estática que mantuvimos en la clase padre Casilla
        Casilla parking = Casilla.getParkingReferencia();
        if (parking != null) {
            parking.sumarValor(cantidad); // Esto funcionará cuando implementemos Parking
            // Nota: getValor() en Casilla devuelve 0 por defecto, pero Parking lo sobrescribirá
            juego.notificarMensaje("Dinero añadido al parking. Bote actual: " + parking.getValor());
        }
    }

    @Override
    public String toString() {
        return "{\n"
                + "tipo: impuesto,\n"
                + "Tasa: " + this.valor + "\n"
                + "}";
    }

    // Sobrescribimos getValor por si se consulta desde fuera
    @Override
    public int getValor() {
        return this.valor;
    }
}