package monopoly.logics.casilla;

public class Salida extends Especial {

    public Salida(int posicion) {
        super("Salida", posicion);
    }

    @Override
    public String toString() {
        // LÓGICA MOVIDA DE infoCasilla (Caso salida)
        return "{\n" +
                "tipo: salida\n" +
                "}";
    }
}