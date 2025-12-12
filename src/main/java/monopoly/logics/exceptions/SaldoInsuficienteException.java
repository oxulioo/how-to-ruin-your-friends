package monopoly.logics.exceptions;

public class SaldoInsuficienteException extends FinancialException {
    private final int nivelBancarrota;

    public SaldoInsuficienteException(String nombre, long falta, int bancarrota) {
        super("El jugador " + nombre + " no tiene saldo. Faltan " + falta + "€.");
        this.nivelBancarrota = bancarrota;
    }
    public boolean esDeBancarrota() {
        return nivelBancarrota == 1;
    }
}