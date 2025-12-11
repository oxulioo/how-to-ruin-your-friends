package monopoly.casilla;

import monopoly.edificios.*;
import monopoly.jugador.Jugador;

public class Solar extends Propiedad {


    //Atributos
    private int numCasas=0;
    private int numHoteles=0;
    private int numPiscinas=0;
    private int numPistas=0;

    private final int precioCasa;
    private final int precioHotel;
    private final int precioPiscina;
    private final int precioPistaDeporte;

    // Alquileres de edificios
    private final int alquilerCasa;
    private final int alquilerHotel;
    private final int alquilerPiscina;
    private final int alquilerPistaDeporte;

    // Alquiler base (necesario recuperarlo del precargarDatosCasillas o pasarlo en constructor)
    private final int alquilerBase;

    // Lista de edificios
    private final java.util.List<Edificio> edificios = new java.util.ArrayList<>();

    public Solar(String nombre, int posicion, Jugador dueno, int valor,
                 int precioCasaYHotel, int precioPiscina,
                 int alquilerBase, int alquilerCasa, int alquilerHotel, int alquilerPiscinaYPistaDeporte) {

        //llamamos al constructor y cargamos todos los datos
        super(nombre, Casilla.TSOLAR, posicion, valor, dueno);
        this.precioCasa = precioCasaYHotel;
        this.precioHotel = precioCasaYHotel;
        this.precioPiscina = precioPiscina;
        this.precioPistaDeporte = 2*precioPiscina;
        this.alquilerBase = alquilerBase;
        this.alquilerCasa = alquilerCasa;
        this.alquilerHotel = alquilerHotel;
        this.alquilerPiscina = alquilerPiscinaYPistaDeporte;
        this.alquilerPistaDeporte = alquilerPiscinaYPistaDeporte;
    }

    // --- GETTERS Y SETTERS MOVIDOS DE CASILLA.JAVA ---

    public int getNumCasas() { return numCasas; }
    public int getNumHoteles() { return numHoteles; }
    public int getNumPiscinas() { return numPiscinas; }
    public int getNumPistas() { return numPistas; }

    public void setNumCasas(int n) { this.numCasas = n; }
    public void setNumHoteles(int n) { this.numHoteles = n; }
    public void setNumPiscinas(int n) { this.numPiscinas = n; }
    public void setNumPistas(int n) { this.numPistas = n; }

    public int getPrecioCasa() { return precioCasa; }
    public int getPrecioHotel() { return precioHotel; }
    public int getPrecioPiscina() { return precioPiscina; }
    public int getPrecioPistaDeporte() { return precioPistaDeporte; }

    public int getAlquilerCasa() { return alquilerCasa; }
    public int getAlquilerHotel() { return alquilerHotel; }
    public int getAlquilerPiscina() { return alquilerPiscina; }
    public int getAlquilerPistaDeporte() { return alquilerPistaDeporte; }

    public int getAlquilerBase() { return this.alquilerBase; }

    public java.util.List<Edificio> getEdificios() { return java.util.Collections.unmodifiableList(edificios); }
    public void eliminarEdificio(Edificio e){ edificios.remove(e); }

    // --- IMPLEMENTACIÓN DE MÉTODOS (Lógica extraída de Casilla.java) ---

    @Override
    public int valor() {
        return this.valor;
    }


    //Reescribimos alquiler, es un metodo abstracto en propiedad
    @Override
    public void alquiler(Jugador actual) {
        if (this.gethipotecada() == 1) return; // Si está hipotecada no cobra

        long alquilerTotal = 0;

        // Regla: El coste de alquiler ... será la suma del coste de alquiler de todos los edificios
        if (!edificios.isEmpty()) {
            for (Edificio e : edificios) {
                alquilerTotal += e.getAlquiler();
                // e.getAlquiler() devuelve el precio base según el tipo (ver clases Casa, Hotel...)
            }
        } else {
            // Si no hay edificios, se aplica la regla estándar del alquiler base
            alquilerTotal = this.alquilerBase;
            // Regla implícita: Si tiene el grupo completo y no hay edificios, se cobra el doble
            if (this.grupo != null && this.grupo.esDuenoGrupo(this.dueno)) {
                alquilerTotal *= 2;
            }
        }

        // Cobrar
        if (alquilerTotal > 0) {
            actual.pagarAlquiler(this, 1); // Pasamos 1 porque el cálculo ya lo hicimos aquí

        }
    }

    // --- MÉTODO EDIFICAR (Gestión de objetos) ---
    public void edificar(String tipo) {
        Edificio nuevoEdificio;
        Jugador propietario = this.getDueno();

        switch (tipo.toLowerCase()) {
            case "casa":
                nuevoEdificio = new Casa(generarIdEdificio("casa"), this, propietario);
                this.setNumCasas(this.getNumCasas() + 1);
                this.edificios.add(nuevoEdificio);
                propietario.anadirEdificio(nuevoEdificio);
                break;

            case "hotel":
                // Regla:se deberán substituir todas las casas por el hotel
                eliminarCasasParaHotel(); // Método privado que borra las 4 casas

                nuevoEdificio = new Hotel(generarIdEdificio("hotel"), this, propietario);
                this.setNumCasas(0); // Reiniciamos contador de casas
                this.setNumHoteles(this.getNumHoteles() + 1); // Único hotel
                this.edificios.add(nuevoEdificio);
                propietario.anadirEdificio(nuevoEdificio);
                break;

            case "piscina":
                nuevoEdificio = new Piscina(generarIdEdificio("piscina"), this, propietario);
                this.setNumPiscinas(this.getNumPiscinas() + 1);
                this.edificios.add(nuevoEdificio);
                propietario.anadirEdificio(nuevoEdificio);
                break;

            case "pista":
            case "pista deporte":
                nuevoEdificio = new PistaDeporte(generarIdEdificio("pista"), this, propietario);
                this.setNumPistas(this.getNumPistas() + 1);
                this.edificios.add(nuevoEdificio);
                propietario.anadirEdificio(nuevoEdificio);
                break;
        }
    }

    @Override
    public String toString() {
        return infoCasilla();
    }

    // Método auxiliar para usar tu infoCasilla original
    public String infoCasilla() {

        String grupoStr = (this.grupo != null) ? this.grupo.getColorGrupo() : "-";
        String propietario = (this.dueno == null) ? "Banca" : this.dueno.getNombre();

        String estadoHipoteca = this.gethipotecada() == 1 ? "Sí" : "No";

        // Se han añadido las variables de contador de edificios y el estado de hipoteca al JSON
        return "{\n"
                + "nombre: " + nombre + ",\n"
                + "tipo: solar,\n"
                + "grupo: " + grupoStr + ",\n"
                + "propietario: " + propietario + ",\n"
                + "hipotecada: " + estadoHipoteca + ",\n"
                + "valor: " + this.valor + ",\n"
                + "alquiler: " + this.alquilerBase + ",\n"
                + "num_casas: " + this.getNumCasas() + ",\n"
                + "num_hoteles: " + this.getNumHoteles() + ",\n"
                + "num_piscinas: " + this.getNumPiscinas() + ",\n"
                + "num_pistas: " + this.getNumPistas() + ",\n"
                + "valor hotel: " + precioHotel + ",\n"
                + "valor casa: " + precioCasa + ",\n"
                + "valor piscina: " + precioPiscina + ",\n"
                + "valor pista de deporte: " + precioPistaDeporte + ",\n"
                + "alquiler casa: " + alquilerCasa + ",\n"
                + "alquiler hotel: " + alquilerHotel + ",\n"
                + "alquiler piscina: " + alquilerPiscina + ",\n"
                + "alquiler pista de deporte: " + alquilerPistaDeporte + "\n"
                + "}";
    }

    private String generarIdEdificio(String prefijo) {
        return prefijo + "-" + (this.edificios.size() + 1);
    }

    // Método auxiliar para limpiar casas al construir hotel
    private void eliminarCasasParaHotel() {
        // Usamos un iterador para borrar de forma segura mientras recorremos
        java.util.Iterator<Edificio> iter = edificios.iterator(); //nos movemos por la coleecion de elementos (edificios)
        int casasBorradas = 0;
        while (iter.hasNext() && casasBorradas < 4) { //mientras tenfa siguiente y no sea mayor que 4 el numero de casas borradas
            Edificio e = iter.next();
            if (e instanceof Casa) {
                iter.remove();
                // También quitar de la lista del jugador
                if (e.getPropietario() != null) {
                    e.getPropietario().eliminarEdificio(e);
                }
                casasBorradas++;
            }
        }
    }


}


