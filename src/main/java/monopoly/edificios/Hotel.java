package monopoly.edificios;
import monopoly.casilla.Solar;
import monopoly.jugador.Jugador;

public class Hotel extends Edificio {
    public Hotel(String id, Solar solar, Jugador propietario) {
        super(id, Tipo.HOTEL, solar, propietario);
    }

    @Override
    public long getAlquiler() {
        return solar.getAlquilerHotel();
    }

    @Override
    public String toString() {
        return "Hotel{id='" + id + "', solar=" + solar.getNombre() + "}";
    }
}