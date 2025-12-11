package monopoly.partida;


public final class Valor {

    private Valor() {
    }//Esto evita que se use new Valor()

    public static final int FORTUNA_INICIAL = 15000000; // para cada jugador
    public static final int SUMA_VUELTA = 2000000; // dinero que recibe el jugador cuando pasa por salida
    public static final int PRECIO_SALIR_CARCEL = 500000; // precio a pagar para salir de la carcel
    public static final int IMPUESTO_FIJO = 2000000; // impuesto que se paga al parking gratuito

    public static final int FACTOR_SERVICIO = 50000; // precio por el que se multiplicara el alquiler de un servicio
    public static final int ALQUILER_TRANSPORTE = 250000; // alquiler de un transporte
    public static final int FORTUNA_BANCA = Integer.MAX_VALUE;
    //Dado que la fortuna de la banca es ilimitada, le doy como valor el mayor número que cabe en un int con signo en Java.
    public static final int PRECIO_SERVICIO_TRANSPORTE = 500000; //Precio de compra de dichas casillas


    // ==== Colores de fondo RGB (Monopoly clásico) con texto blanco ====
    public static final String RESET = "\u001B[0m";
    public static final String TEXTO_BLANCO = "\u001B[38;2;255;255;255m";   // texto blanco puro

    public static final String RGB_MARRON = TEXTO_BLANCO + "\u001B[48;2;150;75;0m";      // Marrón
    public static final String RGB_CIAN = TEXTO_BLANCO + "\u001B[48;2;173;216;230m";   // Cian
    public static final String RGB_ROSA = TEXTO_BLANCO + "\u001B[48;2;255;105;180m";   // Rosa
    public static final String RGB_NARANJA = TEXTO_BLANCO + "\u001B[48;2;255;165;0m";     // Naranja
    public static final String RGB_ROJO = TEXTO_BLANCO + "\u001B[48;2;220;20;60m";     // Rojo
    public static final String RGB_AMARILLO = TEXTO_BLANCO + "\u001B[48;2;255;215;0m";     // Amarillo
    public static final String RGB_VERDE = TEXTO_BLANCO + "\u001B[48;2;0;128;0m";       // Verde
    public static final String RGB_AZUL = TEXTO_BLANCO + "\u001B[48;2;0;102;204m";     // Azul oscuro

    // ==== Otros tipos de casilla ====
    public static final String RGB_TRANSPORTE = TEXTO_BLANCO + "\u001B[48;2;70;70;90m";      // Transporte (gris)
    public static final String RGB_SERVICIOS = TEXTO_BLANCO + "\u001B[48;2;128;0;128m";     // Servicios (morado)
    public static final String RGB_IMPUESTO = TEXTO_BLANCO + "\u001B[48;2;139;0;0m";       // Impuesto (rojo oscuro)
    public static final String RGB_SUERTE = TEXTO_BLANCO + "\u001B[48;2;0;153;76m";      // Suerte (verde claro)
    public static final String RGB_ESPECIAL = TEXTO_BLANCO + "\u001B[48;2;255;69;0m";      // Color para las esquinas (naranja)
    public static final String RGB_COMUNIDAD = TEXTO_BLANCO + "\u001B[48;2;192;128;255m";
}