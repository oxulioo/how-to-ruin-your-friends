package monopoly;

public interface JuegoListener {
    // 📢 NOTIFICACIONES BÁSICAS
    void onMensaje(String mensaje); // Para texto general
    void onError(String mensajeError); // Para fallos

    // 🎲 EVENTOS DE JUEGO (Lo que verá la interfaz gráfica)
    void onJugadorMovido(String nombreJugador, String nombreCasilla, int nuevaPosicion);
    void onCambioFortuna(String nombreJugador, long fortunaActual, long cantidadCambio);
    void onDadosLanzados(int dado1, int dado2, boolean esDoble);
    void onTurnoCambiado(String nuevoJugador);
    void onPropiedadComprada(String j, String p, long pr);
    void onCambioEstadoCarcel(String j, boolean e);
    void onCartaRecibida(String tipo, String mensaje);
    void onEdificioConstruido(String nombreCasilla);

    // Aquí iremos añadiendo más cosas según las necesitemos (compras, cárcel, etc.)
}