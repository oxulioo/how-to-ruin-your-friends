package monopoly.logics.edificios;
import monopoly.logics.casilla.Solar;
import monopoly.logics.jugador.Jugador;

public class Piscina extends Edificio {
    public Piscina(String id, Solar solar, Jugador propietario) {
        super(id, Tipo.PISCINA, solar, propietario);
    }

    @Override
    public long getAlquiler() {
        return solar.getAlquilerPiscina();
    }

    @Override
    public String toString() {
        return "Piscina{id='" + id + "', solar=" + solar.getNombre() + "}";
    }
}