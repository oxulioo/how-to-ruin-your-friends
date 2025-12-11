package monopoly;

import monopoly.exceptions.MonopolyEtseException;

public class MonopolyETSE {

    public static void main(String[] args) {
        // 1. CREAR EL MOTOR
        Juego juego = new Juego();

        // 2. CREAR LA "PANTALLA" (LISTENER)
        // Como hemos quitado los prints del juego, necesitamos a alguien que
        // reciba los mensajes y los escriba en la consola de verdad (System.out).
        JuegoListener vistaConsola = new JuegoListener() {
            @Override
            public void onMensaje(String mensaje) {
                System.out.println(mensaje); // <--- Aquí es donde realmente se imprime
            }

            @Override
            public void onError(String mensajeError) {
                System.err.println("ERROR: " + mensajeError);
            }

            // Estos métodos son para la interfaz gráfica, aquí los dejamos vacíos
            @Override public void onJugadorMovido(String j, String c, int n) {}
            @Override public void onCambioFortuna(String j, long f, long c) {}
            @Override public void onDadosLanzados(int d1, int d2, boolean db) {}
            @Override public void onTurnoCambiado(String j) {}
            @Override public void onPropiedadComprada(String j, String p, long pr) {}
            @Override public void onCambioEstadoCarcel(String j, boolean e) {}
        };

        // 3. CONECTAR LOS CABLES
        // Le decimos al juego: "Todo lo que pase, cuéntaselo a vistaConsola"
        juego.addListener(vistaConsola);

        // 4. ARRANCAR EL MENÚ
        Menu menu = new Menu(juego);
        juego.iniciarPartida();

        try {
            menu.run(); // Esto bloquea el programa y se queda esperando que escribas
        } catch (MonopolyEtseException e) {
            System.out.println("Ocurrió un error en el juego: " + e.getMessage());
        }
    }
}