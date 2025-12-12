package monopoly.logics.edificios;
import monopoly.logics.casilla.Solar;
import monopoly.logics.jugador.Jugador;


public abstract class Edificio {

    public abstract long getAlquiler();

    public enum Tipo {
        CASA, HOTEL, PISCINA, PISTA
    }

    protected String id;
    protected Tipo tipo;
    protected Solar solar;
    protected Jugador propietario;

    public Edificio(String id, Tipo tipo, Solar solar, Jugador propietario) { //Constructor
        this.id = id;
        this.tipo = tipo;
        this.solar = solar;
        this.propietario = propietario;
    }

    public String getId() { return id; }
    public Tipo getTipo() { return tipo; }
    public Solar getSolar() { return solar; }
    public Jugador getPropietario() { return propietario; }


    public void setSolar(Solar solar) { this.solar = solar; }

}