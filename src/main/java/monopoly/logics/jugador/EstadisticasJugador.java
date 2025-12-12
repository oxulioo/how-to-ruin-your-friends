package monopoly.logics.jugador;

import monopoly.logics.partida.Valor;

public class EstadisticasJugador {
    private long dineroInvertido;
    private long pagoTasasImpuestos;
    private long pagoDeAlquileres;
    private long cobroDeAlquileres;
    private long pasarPorCasillaDeSalida;
    private long premiosInversionesOBote;
    private int vecesEnLaCarcel;

    public EstadisticasJugador() {
        this.dineroInvertido = 0;
        this.pagoTasasImpuestos = 0;
        this.pagoDeAlquileres = 0;
        this.cobroDeAlquileres = 0;
        this.pasarPorCasillaDeSalida = 0;
        this.premiosInversionesOBote = 0;
        this.vecesEnLaCarcel = 0;
    }

    // Getters
    public long getDineroInvertido() { return dineroInvertido; }
    public long getPagoTasasImpuestos() { return pagoTasasImpuestos; }
    public long getPagoDeAlquileres() { return pagoDeAlquileres; }
    public long getCobroDeAlquileres() { return cobroDeAlquileres; }
    public long getPasarPorCasillaDeSalida() { return pasarPorCasillaDeSalida; }
    public long getPremiosInversionesOBote() { return premiosInversionesOBote; }
    public int getVecesEnLaCarcel() { return vecesEnLaCarcel; }

    // Métodos para actualizar estadísticas
    public void sumarDineroInvertido(long cantidad) { this.dineroInvertido += cantidad; }
    public void sumarPagoTasasImpuestos(long cantidad) { this.pagoTasasImpuestos += cantidad; }
    public void sumarPagoDeAlquileres(long cantidad) { this.pagoDeAlquileres += cantidad; }
    public void sumarCobroDeAlquileres(long cantidad) { this.cobroDeAlquileres += cantidad; }
    public void sumarPasarPorSalida() { this.pasarPorCasillaDeSalida += Valor.SUMA_VUELTA; }
    public void sumarPremiosInversionesOBote(long cantidad) { this.premiosInversionesOBote += cantidad; }
    public void incrementarVecesEnLaCarcel() { this.vecesEnLaCarcel++; }
}