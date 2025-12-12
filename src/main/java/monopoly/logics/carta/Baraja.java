package monopoly.logics.carta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baraja {
    private final List<Carta> mazoSuerte;
    private final List<Carta> mazoComunidad;
    private int indiceSuerte = 0;
    private int indiceComunidad = 0;

    public Baraja() {
        this.mazoSuerte = new ArrayList<>();
        this.mazoComunidad = new ArrayList<>();
        inicializarCartas();
        //barajar();
    }

    private void inicializarCartas() {
        // Cartas de Suerte
        mazoSuerte.add(new CartaSuerte(1, "Decides hacer un viaje de placer. Avanza hasta Solar19. Si pasas por la casilla de Salida, cobra 2.000.000€."));
        mazoSuerte.add(new CartaSuerte(2, "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€. "));
        mazoSuerte.add(new CartaSuerte(3, "¡Has ganado el bote de la lotería! Recibe 1.000.000€"));
        mazoSuerte.add(new CartaSuerte(4, "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€."));
        mazoSuerte.add(new CartaSuerte(5, "¡Hora punta de tráfico! Retrocede tres casillas."));
        mazoSuerte.add(new CartaSuerte(6, "Te multan por usar el móvil mientras conduces. Paga 150.000€."));
        mazoSuerte.add(new CartaSuerte(7, "Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al dueño el doble de la operación indicada."));

        // Cartas de Comunidad
        mazoComunidad.add(new CartaCajaComunidad(1, "Paga 500.000€ por un fin de semana en un balneario de 5 estrellas."));
        mazoComunidad.add(new CartaCajaComunidad(2, "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin cobrar los 2.000.000€."));
        mazoComunidad.add(new CartaCajaComunidad(3, "Colócate en la casilla de Salida. Cobra 2.000.000€."));
        mazoComunidad.add(new CartaCajaComunidad(4, "Devolución de Hacienda. Cobra 500.000€."));
        mazoComunidad.add(new CartaCajaComunidad(5, "Retrocede hasta Solar1 para comprar antigüedades exóticas."));
        mazoComunidad.add(new CartaCajaComunidad(6, "Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€."));
    }

    public void barajar() {
        Collections.shuffle(mazoSuerte);
        Collections.shuffle(mazoComunidad);
        indiceSuerte = 0;
        indiceComunidad = 0;
    }

    public Carta sacarCarta(String tipo) {
        if ("Suerte".equals(tipo)) {
            if (mazoSuerte.isEmpty()) return null;
            Carta c = mazoSuerte.get(indiceSuerte);
            indiceSuerte = (indiceSuerte + 1) % mazoSuerte.size(); // Rotación circular
            return c;
        } else if ("Comunidad".equals(tipo)) {
            if (mazoComunidad.isEmpty()) return null;
            Carta c = mazoComunidad.get(indiceComunidad);
            indiceComunidad = (indiceComunidad + 1) % mazoComunidad.size();
            return c;
        }
        return null;
    }
}