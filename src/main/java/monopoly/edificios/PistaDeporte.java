package monopoly.edificios;
import monopoly.casilla.Solar;
import monopoly.jugador.Jugador;

public class PistaDeporte extends Edificio {
    public PistaDeporte(String id, Solar solar, Jugador propietario) {
        super(id, Tipo.PISTA, solar, propietario);
    }

    @Override
    public long getAlquiler() {
        return solar.getAlquilerPistaDeporte();
    }

    @Override
    public String toString() {
        return "PistaDeporte{id='" + id + "', solar=" + solar.getNombre() + "}";
    }
}