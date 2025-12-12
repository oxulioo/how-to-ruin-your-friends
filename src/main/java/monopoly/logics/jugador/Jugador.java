package monopoly.logics.jugador;

import monopoly.logics.casilla.*;
import monopoly.logics.edificios.Edificio;
import monopoly.logics.partida.Avatar;
import monopoly.logics.partida.Valor;

import java.util.ArrayList;

public class Jugador {


    // Si no lo tienes, ponlo ahora mismo al principio de la clase
    private monopoly.JuegoListener listener;
    public void setListener(monopoly.JuegoListener l) { this.listener = l; }

    public void hablar(String mensaje) {
        if (listener != null) listener.onMensaje(mensaje);
    }





    private String nombre;
    private Avatar avatar;
    private int fortuna;
    private boolean enCarcel;
    private int tiradasCarcel;
    private int vueltas;
    private ArrayList<Casilla> propiedades;
    private final java.util.List<Edificio> misEdificios = new ArrayList<>();
    private EstadisticasJugador estadisticas;

    public java.util.List<Edificio> getMisEdificios() { return java.util.Collections.unmodifiableList(misEdificios); }
    public void anadirEdificio(Edificio e) { misEdificios.add(e); }
    public void eliminarEdificio(Edificio e) { misEdificios.remove(e); }
    public String getNombre() { return nombre; }
    public Avatar getAvatar() { return avatar; }
    public int getFortuna() { return fortuna; }
    public boolean isEnCarcel() { return enCarcel; }
    public void salirCarcel() { enCarcel = false; }
    public int getVueltas() { return vueltas; }
    public void setVueltas(int v) { this.vueltas = v; }

    public ArrayList<Casilla> getPropiedades() {
        if (propiedades == null) propiedades = new ArrayList<>();
        return propiedades;
    }

    public int getTiradasCarcel() { return tiradasCarcel; }
    public void setTiradasCarcel(int t) { this.tiradasCarcel = t; }
    public EstadisticasJugador getEstadisticas() { return estadisticas; }

    public Jugador() {
        this.nombre = "Banca";
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.fortuna = Valor.FORTUNA_BANCA;
        this.propiedades = new ArrayList<>();
        this.avatar = new Avatar("Banca", null, this, new ArrayList<>());
        this.estadisticas = new EstadisticasJugador();
    }

    public Jugador(String nombre, String tipoAvatar, Casilla casilla, ArrayList<Avatar> avCreados) {
        if (existeNombre(nombre, avCreados)) {
            hablar("Jugador existe");
            return;
        }
        this.nombre = nombre;
        this.fortuna = Valor.FORTUNA_INICIAL;
        this.enCarcel = false;
        this.tiradasCarcel = 0;
        this.vueltas = 0;
        this.propiedades = new ArrayList<>();
        this.avatar = new Avatar(tipoAvatar, casilla, this, avCreados);
        this.avatar.setPosicion(casilla);
        this.estadisticas = new EstadisticasJugador();
    }

    private boolean existeNombre(String nombre, ArrayList<Avatar> avCreados) {
        for (Avatar avatar : avCreados) {
            if (avatar.getJugador().getNombre().equals(nombre)) return true;
        }
        return false;
    }

    public void anadirPropiedad(Casilla casilla) {
        if (casilla == null) {
            hablar("Error: La casilla no existe");
            return;
        }

        // Comprobamos si es Propiedad
        if (!(casilla instanceof Propiedad p)) {
            hablar("La casilla " + casilla.getNombre() + " no es una propiedad.");
            return;
        }

        //Miramos que no sea de otra persona
        if (p.getDueno() != null && p.getDueno() != this && !"Banca".equals(p.getDueno().getNombre())) {
            hablar("La casilla " + p.getNombre() + " pertenece al jugador " + p.getDueno().getNombre() + ".");
            return;
        }

        if (!propiedades.contains(p)) {
            propiedades.add(p);
            p.setDueno(this);
        } else {
            hablar("La casilla " + p.getNombre() + " ya pertenece al jugador");
        }
    }

    public void sumarFortuna(int valor) {
        this.fortuna += valor;
        if (listener != null) listener.onCambioFortuna(this.nombre, this.fortuna, valor);
    }

    public boolean sumarGastos(int valor) {
        if (this.fortuna < valor) {
            hablar("No tienes suficiente dinero.");
            return false;
        }
        this.fortuna -= valor;
        if (listener != null) listener.onCambioFortuna(this.nombre, this.fortuna, -valor);
        return true;
    }

    public void restarDinero(int valor) {
        this.fortuna -= valor;
        if (listener != null) listener.onCambioFortuna(this.nombre, this.fortuna, -valor);
        if(fortuna < 0) {
            hablar("ALERTA, USTED ESTÁ EN NÚMEROS ROJOS, REGULARICE SU SITUACIÓN ANTES DE ACABAR EL TURNO O SERÁ DECLARADO EN BANCARROTA.");
        }
    }

    public void encarcelar() {
        this.enCarcel = true;
        this.tiradasCarcel = 0;
        this.estadisticas.incrementarVecesEnLaCarcel();
    }

    public void pagarAlquiler(Casilla c, int factor_pago) {
        if (c == null) return;

        // Verificamos que sea Propiedad
        if (!(c instanceof Propiedad p)) return;
        Jugador dueno = p.getDueno();
        if (dueno != null && dueno != this) { //si tiene dueño y no es el
            int importe = getImporte(c, factor_pago);
            this.restarDinero(importe);
            this.estadisticas.sumarPagoDeAlquileres(importe);
            dueno.sumarFortuna(importe);
            dueno.getEstadisticas().sumarCobroDeAlquileres(importe);

            // Si es Solar, tiene metodo sumarDineroGenerado propio, o lo casteamos
            if (c instanceof Solar) {
                ((Solar)c).sumarDineroGenerado(importe);
                if (p.getGrupo() != null) {
                    p.getGrupo().sumarRentabilidad(importe);
                }
            }
            hablar(this.nombre + " ha pagado " + importe + " € a " + dueno.getNombre());
        }
    }

    private static int getImporte(Casilla c, int factor_pago) {
        int alquiler = 0;

        // Si es solar, calculamos con edificios
        if (c instanceof Solar s) {
            int casas = s.getNumCasas();
            int hoteles = s.getNumHoteles();
            int piscinas = s.getNumPiscinas();
            int pistas = s.getNumPistas();

            if (casas > 0 || hoteles > 0 || piscinas > 0 || pistas > 0) {
                if (casas > 0) alquiler += casas * s.getAlquilerCasa();
                if (hoteles > 0) alquiler += hoteles * s.getAlquilerHotel();
                if (piscinas > 0) alquiler += piscinas * s.getAlquilerPiscina();
                if (pistas > 0) alquiler += pistas * s.getAlquilerPistaDeporte();
            } else {
                alquiler = s.getAlquilerBase();
            }
        } else {
            // Transporte o Servicio (casillas que son propiedad pero no solar)
            // Usamos una lógica genérica o accedemos a los valores definidos
            if (c instanceof Transporte) alquiler = Valor.ALQUILER_TRANSPORTE;
            else if (c instanceof Servicio) alquiler = Valor.FACTOR_SERVICIO; //Servicio depende de dados
        }

        return factor_pago * alquiler;
    }

    //Atributo, Hashmap con todos los tratos que tiene ese jugador
    private final java.util.Map<String, Trato> tratosRecibidos = new java.util.HashMap<>();

    // Mete el trato en el hashmap
    public void recibirTrato(Trato t) {
        if (t != null) tratosRecibidos.put(t.getId(), t);
    }

    // sacamos trato, lo borramos
    public void eliminarTrato(String idTrato) {
        tratosRecibidos.remove(idTrato);
    }

    // vemos el trato
    public Trato getTrato(String idTrato) {
        return tratosRecibidos.get(idTrato);
    }

    //vemos todos los tratos
    public java.util.Collection<Trato> getListaTratos() {
        return tratosRecibidos.values();
    }

    @Override
    public String toString() {
        return this.nombre; // Cuando alguien intente imprimir al jugador, saldrá su nombre
    }

}