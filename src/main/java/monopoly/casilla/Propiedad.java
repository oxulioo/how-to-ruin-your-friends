package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

public abstract class Propiedad extends Casilla {


    protected Jugador dueno;
    protected int valor;
    protected Grupo grupo;
    protected int hipoteca;
    protected int hipotecada; // Mantengo tu int hipotecada (0 o 1) en vez de boolean
    protected long dineroGenerado=0;
    public long getDineroGenerado(){
        return dineroGenerado;
    }
    public void sumarDineroGenerado(long cantidad){
        this.dineroGenerado+=cantidad;
    }
    public Propiedad(String nombre, String tipo, int posicion, int valor, Jugador dueno) {
        super(nombre, tipo, posicion); // ¡Ahora sí le pasamos el tipo a Casilla!
        this.valor = valor;
        this.hipoteca = valor/2;
        this.dueno = dueno;
        this.hipotecada = 0;
        this.grupo = null;
    }

    // --- MÉTODOS MOVIDOS DE CASILLA.JAVA ---

    public Jugador getDueno() {
        return dueno;
    }
    public void setDueno(Jugador d) {
        this.dueno = d;
    }
    public Grupo getGrupo() {
        return grupo;
    }
    public void setGrupo(Grupo g) {
        this.grupo = g;
    }
    public int getValor() {
        return valor;
    }
    public int getHipoteca() {
        return hipoteca;
    }
    public int gethipotecada() {
        return hipotecada;
    }
    public void sethipotecada(int h) {
        hipotecada = h;
    }

    public boolean perteneceAJugador(Jugador jugador) {
        return dueno != null && dueno.equals(jugador);
    }

   //Abstractos
    public abstract void alquiler(Jugador j);

    public abstract int valor();

    //metodo existente
    public void comprar(Jugador solicitante) {
        int precio = Math.max(0, this.valor);

        if (!solicitante.sumarGastos(precio)) {
            solicitante.hablar(solicitante.getNombre() + " no tiene suficiente dinero para comprar " + this.nombre);
            return;
        }
        // Se añade la propiedad al solicitante.
        solicitante.anadirPropiedad(this);
    }


    public void evaluarCasilla(Jugador actual, Juego juego, int tirada) {
        this.incrementarVisita(); // Tu línea original


        if (dueno != null && !dueno.equals(actual) && !dueno.getNombre().equals("Banca")) {
            // Llama al método alquiler() específico de cada hijo (Solar/Transporte)
            alquiler(actual);
        }

        else if (dueno == null || dueno.getNombre().equals("Banca")) {
            juego.notificarMensaje("Estás en " + nombre + ". Pertenece a la Banca.");
            juego.notificarMensaje("Valor de compra: " + valor);
        }
    }
}