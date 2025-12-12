package monopoly.logics.carta;

import monopoly.logics.logica.Juego;
import monopoly.logics.casilla.Casilla;
import monopoly.logics.casilla.Propiedad;
import monopoly.logics.exceptions.AccionInvalidaException;
import monopoly.logics.exceptions.MonopolyEtseException;
import monopoly.logics.jugador.Jugador;
import monopoly.logics.partida.Valor;

public class CartaSuerte extends Carta {

    public CartaSuerte(int id, String descripcion) {
        super(id, descripcion);
    }

    @Override
    public void accion(Jugador jugador, Juego juego) throws MonopolyEtseException {
        juego.notificarCarta("Comunidad", "Carta de Suerte: " + descripcion);

        switch (id) {
            case 1: // Viaje a Solar19
                juego.moverJugadorACasilla(jugador, "Solar19", true);
                break;
            case 2: // A la cárcel
                juego.enviarACarcel(jugador);
                break;
            case 3: // Lotería
                jugador.sumarFortuna(1000000);
                jugador.getEstadisticas().sumarPremiosInversionesOBote(1000000);
                break;
            case 4: // Pagar a todos
                pagarATodos(jugador, juego, 250000);
                jugador.getEstadisticas().sumarPagoTasasImpuestos(250000L * juego.getJugadoresNum());
                break;
            case 5: // Retroceder 3
                retrocederCasillas(jugador, juego, 3);
                break;
            case 6: // Multa móvil
                pagarSiPuede(jugador, 150000);
                break;
            case 7: // Transporte cercano
                avanzarTransporteMasCercano(jugador, juego);
                break;
            default:
                juego.notificarCarta("Comunidad", "Acción de suerte no definida para ID: " + id);
        }
    }

    private void pagarATodos(Jugador jugador, Juego juego, int cantidad) {
        for (Jugador otro : juego.getJugadores()) {
            if (!otro.equals(jugador) && !"Banca".equals(otro.getNombre())) {
                jugador.restarDinero(cantidad);
                otro.sumarFortuna(cantidad);
                otro.getEstadisticas().sumarCobroDeAlquileres(cantidad);
                // (Al que paga se le cuenta como tasas/impuestos al ser carta de suerte)
                jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
                juego.notificarCarta("Comunidad", jugador.getNombre() + " paga " + cantidad + "€ a " + otro.getNombre() + ".");
            }
        }
    }

    private void retrocederCasillas(Jugador jugador, Juego juego, int casillas) throws MonopolyEtseException {
        Casilla actual = jugador.getAvatar().getPosicion();
        int nuevaPos = (actual.getPosicion() - casillas - 1 + 40) % 40 + 1;
        juego.moverJugadorAPosicion(jugador, nuevaPos);
    }

    private void pagarSiPuede(Jugador jugador, int cantidad) {
        jugador.restarDinero(cantidad);
        jugador.getEstadisticas().sumarPagoTasasImpuestos(cantidad);
        Casilla parking = Casilla.getParkingReferencia();
        if (parking != null) parking.sumarValor(cantidad);
    }

    private void avanzarTransporteMasCercano(Jugador jugador, Juego juego) throws AccionInvalidaException {
        final String[] nomTransportes = {"Trans1", "Trans2", "Trans3", "Trans4"};
        int minDistancia = Integer.MAX_VALUE;
        String transporteMasCercano = null;
        int posActual = jugador.getAvatar().getPosicion().getPosicion();

        // vemos cual esta más cerca
        for (String nombre : nomTransportes) {
            Casilla c = juego.getTablero().encontrar_casilla(nombre);
            if (c != null) {
                int posDestino = c.getPosicion();

                // Distancia hacia adelante (teniendo en cuenta la vuelta al tablero)
                int distancia = (posDestino >= posActual)
                        ? (posDestino - posActual)
                        : (40 - posActual + posDestino);

                if (distancia < minDistancia) {
                    minDistancia = distancia;
                    transporteMasCercano = nombre;
                }
            }
        }

        if (transporteMasCercano == null) throw new AccionInvalidaException("No hay transportes en el tablero.");

        // Nos movemos
        Casilla destino = juego.getTablero().encontrar_casilla(transporteMasCercano); //buscamos la casilla
        juego.notificarCarta("Comunidad", "El transporte más cercano es " + transporteMasCercano + " (avanzas " + minDistancia + " casillas).");



        // Si pasamos por salida al ir allí, cobramos
        if (destino.getPosicion() < posActual) {
            jugador.sumarFortuna(Valor.SUMA_VUELTA);
            jugador.getEstadisticas().sumarPasarPorSalida();
            jugador.setVueltas(jugador.getVueltas() + 1);
            juego.notificarCarta("Comunidad", "Has pasado por Salida: cobras " + Valor.SUMA_VUELTA + "€.");
        }

        // Colocamos el avatar
        jugador.getAvatar().setPosicion(destino);
        destino.incrementarVisita();

        // Regla especial de la carta: Pagar el DOBLE si tiene dueño
        if (destino instanceof Propiedad) { //destino instancia propiedad
            Propiedad p = (Propiedad) destino;
            Jugador dueno = p.getDueno();

            if (dueno != null && !dueno.equals(jugador) && !dueno.getNombre().equals("Banca")) {
                // Calcular alquiler base de transporte (250000 * numTransportes)
                int numTransportes = 0;
                for (Casilla c : dueno.getPropiedades()) {
                    if (c.getTipo().equals(Casilla.TTRANSPORTE)) numTransportes++;
                }
                long alquilerBase = (long) Valor.ALQUILER_TRANSPORTE * numTransportes; //depende ddel nunero que tenga de cartas de transporte pagas mas o menos


                long aPagar = alquilerBase * 2;

                juego.notificarCarta("Comunidad", "La propiedad pertenece a " + dueno.getNombre() + ".");
                juego.notificarCarta("Comunidad", "La carta te obliga a pagar el DOBLE del alquiler: " + aPagar + "€.");

                jugador.restarDinero((int) aPagar);
                dueno.sumarFortuna((int) aPagar);

                // Actualizar estadísticas
                jugador.getEstadisticas().sumarPagoDeAlquileres(aPagar);
                dueno.getEstadisticas().sumarCobroDeAlquileres(aPagar);

            } else if (dueno == null || dueno.getNombre().equals("Banca")) {
                juego.notificarCarta("Comunidad", "Has llegado a " + p.getNombre() + ". Está libre y puedes comprarla.");
            }
        }
    }
}
