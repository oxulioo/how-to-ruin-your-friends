package monopoly.jugador;
import monopoly.casilla.Propiedad;

public class Trato {
    private static int contadorId = 1;

    private final String id;
    private final Jugador proponente;
    private final Jugador propuesto;

    // Lo que da el proponente
    private final Propiedad propiedadOfrecida;
    private final int dineroOfrecido;

    // Lo que da el propuesto (lo que el proponente pide)
    private final Propiedad propiedadDeseada;
    private final int dineroDeseado;

    public Trato(Jugador proponente, Jugador propuesto, Propiedad propOfrece, int dineroOfrece, Propiedad propPide, int dineroPide) {
        this.id = "trato" + contadorId++;
        this.proponente = proponente;
        this.propuesto = propuesto;
        this.propiedadOfrecida = propOfrece;
        this.dineroOfrecido = dineroOfrece;
        this.propiedadDeseada = propPide;
        this.dineroDeseado = dineroPide;
    }

    public String getId() { return id; }
    public Jugador getProponente() { return proponente; }
    public Jugador getPropuesto() { return propuesto; }
    public Propiedad getPropiedadOfrecida() { return propiedadOfrecida; }
    public int getDineroOfrecido() { return dineroOfrecido; }
    public Propiedad getPropiedadDeseada() { return propiedadDeseada; }
    public int getDineroDeseado() { return dineroDeseado; }

    @Override
    public String toString() {
        // Formato para listar tratos
        String ofrece = (propiedadOfrecida != null ? propiedadOfrecida.getNombre() : "") +
                (propiedadOfrecida != null && dineroOfrecido > 0 ? " y " : "") +
                (dineroOfrecido > 0 ? dineroOfrecido + "€" : "");

        String pide = (propiedadDeseada != null ? propiedadDeseada.getNombre() : "") +
                (propiedadDeseada != null && dineroDeseado > 0 ? " y " : "") +
                (dineroDeseado > 0 ? dineroDeseado + "€" : "");

        // Si no ofrece/pide nada de un tipo, poner algo para que no quede vacío
        if (ofrece.isEmpty()) ofrece = "nada"; // Raro, pero posible
        if (pide.isEmpty()) pide = "nada";

        return "id: " + id + ",\n" +
                "trato: cambiar (" + ofrece + ", " + pide + ")";
    }
}