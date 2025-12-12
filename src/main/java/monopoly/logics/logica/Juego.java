package monopoly.logics.logica;
import monopoly.JuegoListener;
import monopoly.logics.carta.Baraja;
import monopoly.logics.carta.Carta;
import monopoly.logics.casilla.*;
import monopoly.logics.edificios.Edificio;
import monopoly.logics.exceptions.*;
import monopoly.logics.jugador.EstadisticasJugador;
import monopoly.logics.jugador.Jugador;
import monopoly.logics.jugador.Trato;
import monopoly.logics.partida.Avatar;
import monopoly.logics.partida.Dado;
import monopoly.logics.partida.Tablero;
import monopoly.logics.partida.Valor;

import java.util.ArrayList;

public class Juego {


    private ArrayList<Jugador> jugadores;
    private ArrayList<Avatar> avatares;
    private int turno;
    private int lanzamientos;
    private Tablero tablero;
    private Dado dado1;
    private Dado dado2;
    private boolean tirado;
    private int doblesConsecutivos = 0;
    private final Jugador banca;
    private final java.util.Map<Long, Integer> numeroPorEdificio = new java.util.HashMap<>();
    private Baraja baraja;

    public Juego() {
        this.banca = new Jugador();
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        this.turno = 0;
        this.lanzamientos = 0;
        this.tirado = false;
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.tablero = new Tablero(this.banca);
        this.baraja = new Baraja(); //Reiniciamos baraja
        this.baraja.barajar();
    }

    public Jugador getBanca() {
        return banca;
    }

    public Tablero getTablero() {
        return this.tablero;
    }

    public int getJugadoresNum() {
        return jugadores.size();
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public int getTurno() {
        return turno;
    }
    public void setTurno(int turno) {}

    public void verTablero() {
        if (tablero == null) {
            notificarMensaje("No hay tablero cargado.");
            return;
        }
        notificarMensaje(tablero.toString());
    }

    public void iniciarPartida() {
        jugadores = new ArrayList<>();
        avatares = new ArrayList<>();
        turno = 0;
        lanzamientos = 0;
        tirado = false;
        dado1 = new Dado();
        dado2 = new Dado();
        tablero = new Tablero(this.banca);
        this.baraja = new Baraja(); //Reiniciamos baraja
        this.baraja.barajar();
    }

    // 1. Lista de listeners
    private final java.util.List<JuegoListener> listeners = new java.util.ArrayList<>();

    public void addListener(JuegoListener listener) {
        if (listener != null) listeners.add(listener);
    }

    // 2. MÉTODOS PÚBLICOS (Importante: public)
    public void notificarMensaje(String msg) {
        for (JuegoListener l : listeners) l.onMensaje(msg);
    }
    public void notificarError(String err) {
        for (JuegoListener l : listeners) l.onError(err);
    }

    public void notificarCarta(String tipo, String mensaje) {
        for (JuegoListener l : listeners) l.onCartaRecibida(tipo, mensaje);
    }

    public void notificarPropiedadComprada(String nombreJugador, String nombrePropiedad, long precio) {
        for (JuegoListener l : listeners) {
            l.onPropiedadComprada(nombreJugador, nombrePropiedad, precio);
        }
    }

    // Método para avisar a la ventana de que alguien se ha movido
    public void notificarJugadorMovido(String nombreJugador, String nombreCasilla, int nuevaPosicion) {
        for (JuegoListener l : listeners) {
            l.onJugadorMovido(nombreJugador, nombreCasilla, nuevaPosicion);
        }
    }

    public void notificarCambioFortuna(String nombre, long fortuna, long cambio) {
        for (JuegoListener l : listeners) {
            l.onCambioFortuna(nombre, fortuna, cambio);
        }
    }

    // 3. El Publicador (puente para los jugadores)
    private final JuegoListener publicador = new JuegoListener() {
        @Override public void onMensaje(String m) { notificarMensaje(m); }
        @Override public void onError(String e) { notificarError(e); }
        @Override public void onJugadorMovido(String j, String c, int n) {}
        @Override public void onCambioFortuna(String j, long f, long c) {
            notificarCambioFortuna(j, f, c);
        }
        @Override public void onDadosLanzados(int d1, int d2, boolean db) {}
        @Override public void onTurnoCambiado(String j) {}
        @Override public void onPropiedadComprada(String j, String p, long pr) {
            notificarPropiedadComprada(j, p, pr);
        }
        @Override public void onCambioEstadoCarcel(String j, boolean e) {}
        @Override public void onCartaRecibida(String t, String m) {
            notificarCarta(t, m);
        }
    };

    public void mostrarJugadorActual() throws MonopolyEtseException {
        if (jugadores == null || jugadores.isEmpty()) {
            throw new AccionInvalidaException("No hay jugadores. Crea uno con: crear jugador <Nombre> <tipoAvatar>");
        }
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);
        notificarMensaje("Tiene el turno: " + actual.getNombre());
    }

    public void crearJugador(String nombre, String tipoAvatar) throws MonopolyEtseException {
        Casilla salida;
        salida = tablero.encontrar_casilla("Salida");
        if (salida == null) {
            throw new AccionInvalidaException("Error crítico: No se encontró la casilla 'Salida' en el tablero.");
        }

        Jugador j;
        j = new Jugador(nombre, tipoAvatar, salida, avatares);
        j.setListener(this.publicador);
        if (j.getNombre() == null) {
            throw new AccionInvalidaException("Error creando jugador");
        }
        j.setListener(this.publicador);
        jugadores.add(j);
        notificarMensaje("Creado jugador '" + nombre + "' con avatar '" + tipoAvatar + "' en Salida.");
        try {
            notificarMensaje(tablero.toString());
        } catch (Throwable ignored) {
        }
    }

    public void listarJugadores() throws MonopolyEtseException {
        if (!hayJugadores()) {
            return;
        }
        for (Jugador j : jugadores) {
           try{ descJugador(j.getNombre());
           } catch(AccionInvalidaException e){
               throw new AccionInvalidaException("No hay jugadores");
           }
        }
    }

    public void descJugador(String nombreBuscado) throws MonopolyEtseException{
        if (nombreBuscado == null || nombreBuscado.isEmpty()) {
            throw new AccionInvalidaException("Uso: describir jugador <Nombre>");
        }
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");
        }

        Jugador j = null;
        for (Jugador x : jugadores) {
            if (x.getNombre().equals(nombreBuscado)) {
                j = x;
                break;
            }
        }
        if (j == null) {
            throw new AccionInvalidaException("Este jugador no existe");

        }

        String nombre = j.getNombre();
        String avatar = "-";
        Avatar a = j.getAvatar();
        if (a != null) {
            String tipo = a.getTipo();
            char id = a.getID();
            avatar = tipo + " (" + id + ")";
        }
        String fortuna = String.format("%d", j.getFortuna());
        String posicion = "-";
        Casilla pos = null;
        if (a != null) pos = a.getPosicion();
        if (pos != null) posicion = pos.getNombre();

        String propiedades = "-";
        java.util.List<Casilla> auxProps = j.getPropiedades();
        if (auxProps != null && !auxProps.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int added = 0;
            for (Casilla c : auxProps) {

                if (c instanceof Propiedad && ((Propiedad) c).gethipotecada() == 0) { //instancia de propiedad y no hipotecada
                    if (added++ > 0) sb.append(", ");
                    sb.append(c.getNombre());
                }
            }
            if (added > 0) propiedades = sb.toString();
        }

        String hipotecadas = "-";
        java.util.List<Casilla> auxHip = j.getPropiedades();
        if (auxHip != null && !auxHip.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int added = 0;
            for (Casilla c : auxHip) {

                if (c instanceof Propiedad && ((Propiedad) c).gethipotecada() == 1) { //instancia de propiedad e hipotecada
                    if (added++ > 0) sb.append(", ");
                    sb.append(c.getNombre());
                }
            }
            if (added > 0) hipotecadas = sb.toString();
        }

        String edificiosLista = getString(j);

        String enCarcel = j.isEnCarcel() ? "Sí" : "No";

        notificarMensaje("Jugador: " + nombre);
        notificarMensaje("  Avatar: " + avatar);
        notificarMensaje("  Fortuna: " + fortuna);
        notificarMensaje("  Posición: " + posicion);
        notificarMensaje("  Propiedades: " + propiedades);
        notificarMensaje("  Hipotecas: " + hipotecadas);
        notificarMensaje("  Edificios:" + edificiosLista);
        notificarMensaje("  En cárcel: " + enCarcel);
        notificarMensaje("");
    }

    private static String getString(Jugador j) {
        String edificiosLista = "-";
        java.util.List<Edificio> eds = j.getMisEdificios();
        if (eds != null && !eds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < eds.size(); i++) {
                Edificio e = eds.get(i);

                sb.append(e.getId());

                if (i < eds.size() - 1) sb.append(", "); //numero de iteraciones menor que el total, ponemos una coma
            }
            edificiosLista = sb.toString();
        }
        return edificiosLista;
    }

    public void descAvatar(String ID) throws MonopolyEtseException {
        if (ID == null || ID.isEmpty()) {
            throw new AccionInvalidaException("Debes especificar un ID de avatar");

        }
        if (avatares == null || avatares.isEmpty()) {
            throw new AccionInvalidaException("No hay avatares creados");

        }

        char idBuscado = ID.charAt(0);
        Avatar encontrado = null;
        for (Avatar a : avatares) {
            if (a.getID() == idBuscado) {
                encontrado = a;
                break;
            }
        }
        if (encontrado == null) {
            throw new AccionInvalidaException ("No existe el avatar con ese ID");


        }

        String tipo = encontrado.getTipo();
        String pos = "-";
        Casilla c = encontrado.getPosicion();
        if (c != null) pos = c.getNombre();

        String jugadorNombre = "-";
        Jugador j = encontrado.getJugador();
        if (j != null) jugadorNombre = j.getNombre();

        notificarMensaje("Avatar: " + idBuscado);
        notificarMensaje("  Tipo: " + tipo);
        notificarMensaje("  Jugador: " + jugadorNombre);
        notificarMensaje("  Posición: " + pos);
        notificarMensaje("");
    }

    public void descCasilla(String nombre) throws MonopolyEtseException{
        if (nombre == null || nombre.isEmpty()){
            throw new AccionInvalidaException("Debes especificar un nombre de casilla");

        }
        Casilla c;
        c = tablero.encontrar_casilla(nombre);
        if (c == null) {
            throw new AccionInvalidaException ("No existe la casilla con ese nombre");

        }
        // Casilla.toString() ya hace lo correcto en las hijas, metodo implementado en las hijas
        notificarMensaje(c.toString());
    }

    public void declararBancarrota() {
        Jugador deudor = jugadores.get(turno);
        notificarMensaje("\n!!! BANCARROTA !!!");
        notificarMensaje("El jugador " + deudor.getNombre() + " cae en bancarrota y abandona la partida.");
        notificarMensaje("Sus propiedades vuelven a la Banca y sus edificios son demolidos.");

        Avatar av = deudor.getAvatar();
        if (av != null) {
            if (av.getPosicion() != null) {
                av.getPosicion().eliminarAvatar(av);
            }
            avatares.remove(av);
        }
        java.util.List<Casilla> props = new ArrayList<>(deudor.getPropiedades());
        for (Casilla c : props) {
            if (c instanceof Propiedad p) {
                p.setDueno(banca);
                p.sethipotecada(0); //si tenia hopotecadas, las deshipotecamos para poner como nuevo dueño la banca
                if (p instanceof Solar s) {
                    java.util.List<Edificio> listaEdificios = new ArrayList<>(s.getEdificios());
                    for (Edificio ed : listaEdificios) {

                        s.eliminarEdificio(ed);
                    }
                    //Sacamos todos sus edificios
                    s.setNumCasas(0);
                    s.setNumHoteles(0);
                    s.setNumPiscinas(0);
                    s.setNumPistas(0);
                }
            }
        }
        deudor.getPropiedades().clear(); //sin propiedades
        jugadores.remove(deudor); //eliminamos de la partida
        if (turno >= jugadores.size()) {
            turno = 0;
        }
    }

    public void lanzarDados() throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");

        }
        if (lanzamientos == 1) {
            throw new AccionInvalidaException("Ya has tirado una vez, no puedes volver a tirar.");

        }
        Jugador actual = jugadores.get(turno);

        int d1 = dado1.hacerTirada();
        int d2 = dado2.hacerTirada();
        int suma = d1 + d2;
        if (d1 != d2) lanzamientos = 1;

        if (actual.isEnCarcel()) {
            if ((actual.getTiradasCarcel() == 3 && d1 != d2 && lanzamientos != 1) || (d1 == d2 && lanzamientos != 1)) {
                notificarMensaje("Sales de la carcel");
                salirCarcel();
                actual.setTiradasCarcel(0);
            }
            if (d1 != d2 && actual.getTiradasCarcel() < 3 && lanzamientos != 1) {
                actual.setTiradasCarcel(actual.getTiradasCarcel() + 1);
                notificarMensaje("Has tirado " + actual.getTiradasCarcel() + " veces en la carcel.");
                return;
            }
        }
        boolean esDoble = (d1 == d2);

        notificarMensaje("Dados: " + d1 + " + " + d2 + " = " + suma + (esDoble ? " (dobles)" : ""));

        if (esDoble) {
            doblesConsecutivos++;
            if (doblesConsecutivos >= 3) {
                notificarMensaje("¡Tres dobles seguidos! " + actual.getNombre() + " va a la cárcel.");
                lanzamientos = 1;
                enviarACarcel(actual);
                doblesConsecutivos = 0;
                tirado = true;
                return;
            }
        } else {
            doblesConsecutivos = 0;
            tirado = true;
        }

            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);
            Casilla c = a.getPosicion();
            notificarJugadorMovido(actual.getNombre(), a.getPosicion().getNombre(), a.getPosicion().getPosicion());

            if (c instanceof IrCarcel) { //si de es la casilla de ir a la carcel
               actual.encarcelar();
            }
            if (c != null) {
               try{ c.evaluarCasilla(actual, this, suma); //acccion de la casilla
               } catch (SaldoInsuficienteException e){
                   throw new SaldoInsuficienteException(actual.getNombre(), suma-actual.getFortuna(), 1);
               }
            }

        notificarMensaje(tablero.toString());

        if (esDoble) {
            notificarMensaje(actual.getNombre() + " ha sacado dobles y puede volver a lanzar.");
            tirado = false;
        }
    }

    public void lanzarDadosForzado(int d1, int d2) throws MonopolyEtseException {
        if (!hayJugadores()) {
           throw new AccionInvalidaException("No hay jugadores");
        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);


        int suma = d1 + d2;
        boolean esDoble = (d1 == d2);

        if (esDoble) {
            doblesConsecutivos++;
            if (doblesConsecutivos >= 3) {
                enviarACarcel(actual);
                doblesConsecutivos = 0;
                tirado = true;
                return;
            }
        } else {
            doblesConsecutivos = 0;
            tirado = true;
        }


            Avatar a = actual.getAvatar();
            a.moverAvatar(tablero.getPosiciones(), suma);
            Casilla c = a.getPosicion();
            if (c != null) c.evaluarCasilla(actual, this, suma);
            else {
                throw new AccionInvalidaException("No hay casilla para mover el avatar");

            }


        try {
            notificarMensaje(tablero.toString());
        } catch (Throwable ignore) {
        }
        if (esDoble) tirado = false;
    }

    public void comprar(String nombre) throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");

        }
        if (turno < 0) turno = 0;
        if (turno >= jugadores.size()) turno = turno % jugadores.size();
        Jugador actual = jugadores.get(turno);

        Casilla cas = tablero.encontrar_casilla(nombre);
        if (cas == null) {
           throw new AccionInvalidaException ("No existe la casilla con ese nombre");
        }
        if (!(cas instanceof Propiedad prop)) {
            throw new AccionInvalidaException("Solo se puede comprar propiedades.");
        }
        Casilla pos = actual.getAvatar().getPosicion();
        if (pos == null || pos != cas) {
            throw new AccionInvalidaException("No puedes comprar propiedades fuera de tu casilla actual.");
        }
        Jugador propietario = prop.getDueno();
        if (propietario != null && propietario != this.getBanca()) { //miramos que no tenga dueño
            throw new AccionInvalidaException("Esta propiedad no esta en venta. La propiedad actualmente pertenece a "+ prop.getDueno()+".");
        }
        int precio = prop.getValor();
        double saldo = actual.getFortuna();
        if (saldo < precio) {
            long faltante = (long) (precio - saldo);
            throw new SaldoInsuficienteException(actual.getNombre(),faltante,0);
        }
        prop.comprar(actual); // Llama al comprar de Propiedad
        notificarPropiedadComprada(actual.getNombre(), prop.getNombre(), prop.getValor());
        if (prop.getDueno() == actual) {
            actual.getEstadisticas().sumarDineroInvertido(precio);
        }
        notificarMensaje(actual.getNombre() + " compra '" + prop.getNombre() + "' por " + precio + ".");
        notificarMensaje("La fortuna actual de " + actual.getNombre() + " es: " + actual.getFortuna() + ".");
        notificarMensaje(tablero.toString());
    }

    public void salirCarcel() throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");
        }
        Jugador actual = jugadores.get(turno);
        if (!actual.isEnCarcel()) {
            throw new AccionInvalidaException(actual.getNombre() + " no está en la cárcel.");
        }
        if (actual.getFortuna() < Valor.PRECIO_SALIR_CARCEL) {
            throw new SaldoInsuficienteException(actual.getNombre(), Valor.PRECIO_SALIR_CARCEL - actual.getFortuna(),1);
        }
        actual.restarDinero(Valor.PRECIO_SALIR_CARCEL);
        actual.getEstadisticas().sumarPagoTasasImpuestos(Valor.PRECIO_SALIR_CARCEL);
        actual.salirCarcel();
        notificarMensaje(actual.getNombre() + " paga y sale de la cárcel.");
        try {
            notificarMensaje(tablero.toString());
        } catch (Throwable ignored) {
        }
    }

    public void listarVenta() {
        // Buscamos todas las propiedades que pertenecen a la Banca
        ArrayList<Propiedad> enVenta = new ArrayList<>();
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                // Verificamos que sea Propiedad y que el dueño sea la Banca
                if (c instanceof Propiedad p && p.getDueno().equals(banca)) {
                    enVenta.add(p);
                }
            }
        }


        if (enVenta.isEmpty()) { // vacio
            notificarMensaje("No hay propiedades en venta.");
            return;
        }

        StringBuilder sb = new StringBuilder();


        for (int i = 0; i < enVenta.size(); i++) {
            Propiedad p = enVenta.get(i);

            sb.append("{\n");

            sb.append("nombre: ").append(p.getNombre()).append(",\n");
            sb.append("tipo: ").append(p.getTipo().toLowerCase()).append(",\n");

            // Solo los Solares tienen Grupo
            if (p instanceof Solar s) { //si es un solar::
                String color = (s.getGrupo() != null) ? s.getGrupo().getColorGrupo() : "-";
                sb.append("grupo: ").append(color).append(",\n");
            }

            sb.append("valor: ").append(p.getValor()).append("\n");
            sb.append("}");

            // Añadimos coma entre elementos, excepto el último
            if (i < enVenta.size() - 1) {
                sb.append(",\n");
            }
        }

        notificarMensaje(sb.toString());
    }

    public void listarAvatares() throws MonopolyEtseException {
        if (avatares == null || avatares.isEmpty()) return;
        for (Avatar a : avatares) {
            char id;
            String tipo;

            id= a.getID();
            if (id == '\0') {
                throw new AccionInvalidaException("Avatar sin ID");
            }
            tipo = a.getTipo();
            if (tipo == null) {
                throw new AccionInvalidaException("Avatar sin tipo");
            }
            Casilla c = a.getPosicion();
            if(c==null){
                throw new AccionInvalidaException("Avatar sin casilla");
            }

            try {
                Jugador j = a.getJugador();
            } catch (Throwable ignored) {
                for (Jugador j : jugadores) {
                    try {
                        if (j.getAvatar() == a) {
                            break;
                        }
                    } catch (Throwable ignored2) {
                        throw new AccionInvalidaException("Avatar sin jugador");
                    }
                }
            }

            notificarMensaje("{ id: " + a.getID() + ", tipo: " + a.getTipo() + " ... }");
        }
    }

    public void acabarTurno() throws BancarrotaException{
        if (!hayJugadores()) {
            return;
        }

        Jugador actual = jugadores.get(turno);

        if (actual.getFortuna() < 0) { //si esta en numeros rojos
            throw new BancarrotaException("SU TURNO HA TERMINADO CON SALDO INSUFICIENTE, POR LO TANTO, SERÁ DECLARADO EN BANCARROTA");
        } else {

            turno = (turno + 1) % jugadores.size();
        }
        lanzamientos = 0;
        doblesConsecutivos = 0;
        tirado = false;

        // --- AÑADIR ESTO: AVISAR A LA PANTALLA ---
        if (hayJugadores()) {
            Jugador siguiente = jugadores.get(turno);
            // Avisamos por texto (chat)
            notificarMensaje("Nuevo turno para: " + siguiente.getNombre());
            notificarMensaje("Fortuna: " + siguiente.getFortuna() + "€");

            // ¡IMPORTANTE! Avisamos al panel lateral para que cambie la etiqueta
            for (JuegoListener l : listeners) {
                l.onTurnoCambiado(siguiente.getNombre());
            }
        } else {
            notificarMensaje("La partida ha terminado. No quedan jugadores.");
        }
    }

    public void enviarACarcel(Jugador j) throws MonopolyEtseException{
        if (j == null || tablero == null) {
            throw new AccionInvalidaException("No hay jugador");
        }
        Casilla carcel = tablero.encontrar_casilla("Cárcel");
        j.encarcelar();
        Avatar a = j.getAvatar();
        if (a != null) a.setPosicion(carcel);
        else {
            throw new AccionInvalidaException("N");
        }
    }




    private Solar obtenerSolarEdificable(Jugador actual) throws EdificacionNoPermitidaException {
        Casilla pos = actual.getAvatar().getPosicion();


        if (!(pos instanceof Solar)) { //si no es solar
            throw new EdificacionNoPermitidaException("Esta casilla no es un Solar.");
        }
        Solar s = (Solar) pos;


        if (!s.getDueno().equals(actual)) { //miramos que el jugador sea el dueño
            throw new EdificacionNoPermitidaException("No puedes edificar aquí, la propiedad no es tuya.");
        }

        return s;
    }


    public void edificarCasa() throws MonopolyEtseException {
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");
        }
        Jugador actual = jugadores.get(turno);
        Solar s = obtenerSolarEdificable(actual);


        if (s.getGrupo() == null || !s.getGrupo().esDuenoGrupo(actual)) {
            throw new EdificacionNoPermitidaException("No puedes edificar: no tienes el grupo de color completo.");
        }


        if (s.getNumHoteles() > 0) {
            throw new EdificacionNoPermitidaException("No puedes construir casas porque ya hay un hotel.");
        }

        //Se pueden construir un máximo de cuatro casas
        if (s.getNumCasas() >= 4) {
            throw new EdificacionNoPermitidaException("Límite de 4 casas alcanzado. Debes construir un hotel.");
        }

        // Pago y Ejecución
        long coste = s.getPrecioCasa();
        if (actual.getFortuna() < coste) {
            throw new SaldoInsuficienteException(actual.getNombre(), coste - actual.getFortuna(), 0);
        }
        actual.restarDinero((int)coste);
        actual.getEstadisticas().sumarDineroInvertido(coste);

        s.edificar("casa");
        notificarMensaje("Casa construida en " + s.getNombre() + " por " + coste + "€.");
    }


    public void edificarHotel() throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Solar s = obtenerSolarEdificable(actual);

        if (s.getGrupo() == null || !s.getGrupo().esDuenoGrupo(actual)) {
            throw new EdificacionNoPermitidaException("No tienes el grupo de color completo.");
        }

        // si en ese solar no se han construido cuatro casas...
        if (s.getNumCasas() != 4) {
            throw new EdificacionNoPermitidaException("Necesitas tener exactamente 4 casas para construir un hotel.");
        }

        // se puede construir un único hotel
        if (s.getNumHoteles() > 0) {
            throw new EdificacionNoPermitidaException("Solo se permite un único hotel por solar.");
        }

        // Pago y Ejecución
        long coste = s.getPrecioHotel();
        if (actual.getFortuna() < coste) {
            throw new SaldoInsuficienteException(actual.getNombre(), coste - actual.getFortuna(), 0);
        }
        actual.restarDinero((int)coste);
        actual.getEstadisticas().sumarDineroInvertido(coste);

        s.edificar("hotel"); // La sustitución de casas se hace dentro de Solar
        notificarMensaje("Hotel construido en " + s.getNombre() + " por " + coste + "€.");
    }


    public void edificarPiscina() throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Solar s = obtenerSolarEdificable(actual);

        if (s.getGrupo() == null || !s.getGrupo().esDuenoGrupo(actual)) {
            throw new EdificacionNoPermitidaException("No tienes el grupo de color completo.");
        }

        //si no se ha construido un hotel...
        if (s.getNumHoteles() == 0) {
            throw new EdificacionNoPermitidaException("Necesitas tener un hotel para construir una piscina.");
        }

        // se puede construir una única piscina...
        if (s.getNumPiscinas() > 0) {
            throw new EdificacionNoPermitidaException("Solo se permite una única piscina por solar.");
        }

        // Pago y Ejecución
        long coste = s.getPrecioPiscina();
        if (actual.getFortuna() < coste) {
            throw new SaldoInsuficienteException(actual.getNombre(), coste - actual.getFortuna(), 0);
        }
        actual.restarDinero((int)coste);
        actual.getEstadisticas().sumarDineroInvertido(coste);

        s.edificar("piscina");
        notificarMensaje("Piscina construida en " + s.getNombre() + " por " + coste + "€.");
    }


    public void edificarPista() throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);
        Solar s = obtenerSolarEdificable(actual);

        if (s.getGrupo() == null || !s.getGrupo().esDuenoGrupo(actual)) {
            throw new EdificacionNoPermitidaException("No tienes el grupo de color completo.");
        }

        // si no se ha construido un hotel y una piscina...
        if (s.getNumHoteles() == 0 || s.getNumPiscinas() == 0) {
            throw new EdificacionNoPermitidaException("Necesitas tener un hotel y una piscina para construir la pista.");
        }

        // se puede construir una única pista...
        if (s.getNumPistas() > 0) {
            throw new EdificacionNoPermitidaException("Solo se permite una única pista de deporte por solar.");
        }

        // Pago y Ejecución
        long coste = s.getPrecioPistaDeporte();
        if (actual.getFortuna() < coste) {
            throw new SaldoInsuficienteException(actual.getNombre(), coste - actual.getFortuna(), 0);
        }
        actual.restarDinero((int)coste);
        actual.getEstadisticas().sumarDineroInvertido(coste);

        s.edificar("pista");
        notificarMensaje("Pista de deporte construida en " + s.getNombre() + " por " + coste + "€.");
    }


    // Método para listar edificios (todos o por color)
    public void listarEdificios(String color) throws MonopolyEtseException {
        // CASO 1: LISTAR TODOS LOS EDIFICIOS (Sin color)
        if (color == null) {
            java.util.List<Edificio> todos = new ArrayList<>();
            if (jugadores != null) {
                for (Jugador j : jugadores) {
                    if (j != null && j.getMisEdificios() != null) todos.addAll(j.getMisEdificios());
                }
            }

            if (todos.isEmpty()) {
                notificarMensaje("{}");
                return;
            }

            for (int i = 0; i < todos.size(); i++) {
                Edificio e = todos.get(i);
                String tipo = e.getTipo().name().toLowerCase();
                int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0);
                String idStr = tipo + "-" + numTipo;

                String propietario = (e.getPropietario() != null) ? e.getPropietario().getNombre() : "-";
                // En Solar es donde está el nombre, aunque Propiedad también lo tiene
                String casilla = (e.getSolar() != null) ? e.getSolar().getNombre() : "-";
                String grupoStr = "-";

                // Obtenemos el grupo desde el Solar
                if (e.getSolar() != null && e.getSolar().getGrupo() != null) {
                    grupoStr = e.getSolar().getGrupo().getColorGrupo();
                    if (grupoStr == null) {
                        throw new AccionInvalidaException("Grupo sin color");
                    }

                }

                // Usamos el método casteado para el coste
                long coste = costeConstruccion(e.getSolar(), e.getTipo());

                notificarMensaje("{");
                notificarMensaje("id: " + idStr + ",");
                notificarMensaje("propietario: " + propietario + ",");
                notificarMensaje("casilla: " + casilla + ",");
                notificarMensaje("grupo: " + grupoStr + ",");
                notificarMensaje("coste: " + coste);
                notificarMensaje(i < todos.size() - 1 ? "}," : "}");
            }
            return;
        }

        // CASO 2: LISTAR POR GRUPO DE COLOR
        Grupo grupo = tablero.getGrupos().get(color);
        if (grupo == null) {
            String colorCapitalizado = Character.toUpperCase(color.charAt(0)) + color.substring(1).toLowerCase();
            grupo = tablero.getGrupos().get(colorCapitalizado);
            if (grupo == null) {
                throw new AccionInvalidaException("Grupo de color '" + color + "' no encontrado.");
            }
        }

        // Banderas de construcción
        boolean puedeCasa = true;
        boolean puedeHotel = true;
        boolean puedePiscina = true;
        boolean puedePista = true;

        int maxCasas = 0;
        int maxHoteles = 0;
        int maxPiscinas = 0;
        int maxPistas = 0;

        // Iteramos sobre los miembros, filtrando solo los Solares
        for (Casilla c : grupo.getMiembros()) {
            if (c == null) continue;

            // IMPORTANTE: Solo procesamos si es Solar (Propiedad edificable)
            if (c instanceof Solar s) {
                // Casteo a Solar para acceder a sus métodos

                notificarMensaje("{");
                notificarMensaje("propiedad: " + s.getNombre() + ",");

                // Listar edificios por tipo (delegando al método auxiliar)
                notificarMensaje("  casas: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.CASA);

                notificarMensaje("  hoteles: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.HOTEL);

                notificarMensaje("  piscinas: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.PISCINA);

                notificarMensaje("  pistasDeDeporte: ");
                listarEdificiosPorTipo(s, Edificio.Tipo.PISTA);

                // Calcular alquiler actual manualmente para mostrarlo

                long alquilerActual = s.getAlquilerBase();

                if (s.getNumCasas() > 0) alquilerActual = (long) s.getNumCasas() * s.getAlquilerCasa();
                if (s.getNumHoteles() > 0) alquilerActual += (long) s.getNumHoteles() * s.getAlquilerHotel();
                if (s.getNumPiscinas() > 0) alquilerActual += (long) s.getNumPiscinas() * s.getAlquilerPiscina();
                if (s.getNumPistas() > 0) alquilerActual += (long) s.getNumPistas() * s.getAlquilerPistaDeporte();

                notificarMensaje("  alquiler: " + alquilerActual);

                // Contadores
                if (s.getNumCasas() == 4) maxCasas++;
                if (s.getNumHoteles() == 1) maxHoteles++;
                if (s.getNumPiscinas() == 1) maxPiscinas++;
                if (s.getNumPistas() == 1) maxPistas++;
            }
        }

        // Lógica de validación de construcción (idéntica a tu original)
        int numPropiedadesGrupo = grupo.getMiembros().size();

        if (maxCasas == numPropiedadesGrupo) puedeCasa = false;
        if (maxHoteles == numPropiedadesGrupo) {
            puedeHotel = false;
            if (maxCasas == 0) puedeCasa = false;
        }
        if (maxPiscinas == numPropiedadesGrupo) puedePiscina = false;
        if (maxPistas == numPropiedadesGrupo) puedePista = false;

        // Imprimir resultados de qué se puede construir
        if (!puedeCasa || !puedeHotel || !puedePiscina || !puedePista) {
            notificarMensaje("Ya no puedes construír:\n");
        }
        if (!puedeCasa)
            notificarMensaje("   -casas: no se pueden construir casas (límite alcanzado o hay hoteles).\n");
        if (!puedeHotel) notificarMensaje("   -hoteles: no se pueden construir hoteles.\n");
        if (!puedePiscina) notificarMensaje("   -piscinas: no se pueden construir piscinas.\n");
        if (!puedePista) notificarMensaje("   -pistas de deporte: no se pueden construir pistas.\n");

        if (puedeCasa || puedeHotel || puedePiscina || puedePista) {
            notificarMensaje("Aun puedes construír:\n");
        }
        if (puedeCasa) notificarMensaje("   -casas\n");
        if (puedeHotel) notificarMensaje("   -hoteles\n");
        if (puedePiscina) notificarMensaje("   -piscinas\n");
        if (puedePista) notificarMensaje("   -pistas de deporte\n");

        notificarMensaje("}");
    }

    // Método auxiliar corregido con casteo
    private void listarEdificiosPorTipo(Casilla c, Edificio.Tipo tipo) throws MonopolyEtseException {
        if (!(c instanceof Solar s)) {
            throw new AccionInvalidaException("Solo se puede listar por grupo de color.");
        }


        java.util.List<String> nombresEdificios = new ArrayList<>();

        // s.getEdificios() ahora es accesible porque s es Solar
        for (Edificio e : s.getEdificios()) {
            if (e.getTipo() == tipo) {
                int numTipo = numeroPorEdificio.getOrDefault(e.getId(), 0);
                nombresEdificios.add(tipo.name().toLowerCase() + "-" + numTipo);
            }
        }

        if (nombresEdificios.isEmpty()) {
            notificarMensaje("-");
        } else {
            notificarMensaje("[" + String.join(", ", nombresEdificios) + "]");
        }
    }

    public long costeConstruccion(Casilla c, Edificio.Tipo t) throws MonopolyEtseException {
        if (!(c instanceof Solar s)){
            throw new AccionInvalidaException("Solo se puede listar por grupo de color y solar.");
        }
        return switch (t) {
            case CASA -> s.getPrecioCasa();
            case HOTEL -> s.getPrecioHotel();
            case PISCINA -> s.getPrecioPiscina();
            case PISTA -> s.getPrecioPistaDeporte();
        };
    }

    // --- HIPOTECAS ---
    public void hipotecar(String nombreProp) throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);

        // Buscar la propiedad
        Casilla c = this.tablero.encontrar_casilla(nombreProp);
        if (c == null) {
            throw new AccionInvalidaException("La casilla " + nombreProp + " no existe.");
        }

        if (!(c instanceof Propiedad p)) {
            throw new HipotecaNoPermitidaException("Solo se pueden hipotecar propiedades.");
        }

        // Validar Dueño
        if (!p.perteneceAJugador(actual)) {
            throw new HipotecaNoPermitidaException("No puedes hipotecar " + p.getNombre() + " porque no te pertenece.");
        }

        // Validar Estado (si ya está hipotecada)
        if (p.gethipotecada() == 1) {
            throw new HipotecaNoPermitidaException("La propiedad " + p.getNombre() + " ya está hipotecada.");
        }

        // Validar Edificios
        if (p instanceof Solar s) { //solo si es solar
            if (!s.getEdificios().isEmpty()) {

                throw new HipotecaNoPermitidaException("No puedes hipotecar " + s.getNombre() + " mientras tenga edificios. Véndelos primero.");
            }
        }

        // EJECUTAR HIPOTECA (Común para todos)
        p.sethipotecada(1);

        long valorHipoteca = p.getHipoteca();
        actual.sumarFortuna((int) valorHipoteca);

        notificarMensaje(actual.getNombre() + " ha hipotecado " + p.getNombre() + " y recibe " + valorHipoteca + "€.");
        notificarMensaje("Fortuna actual: " + actual.getFortuna() + "€");
    }

    public void deshipotecar(String nombreSolar) throws MonopolyEtseException{
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(nombreSolar);

        // Verificar si existe
        if (c == null) {
            throw new AccionInvalidaException("La casilla " + nombreSolar + " no existe.");
        }

        // Verificar si es una Propiedad (Solar, Transporte, Servicio)
        if (!(c instanceof Propiedad p)) {
            throw new HipotecaNoPermitidaException("La casilla " + c.getNombre() + " no es una propiedad y no se puede deshipotecar.");
        }

        // Verificar dueño
        if (!p.perteneceAJugador(actual)) {
            throw new HipotecaNoPermitidaException(actual.getNombre() + " no puede deshipotecar " + p.getNombre() + ". No es su propiedad.");
        }

        // Verificar si está hipotecada
        if (p.gethipotecada() == 0) {
            throw new HipotecaNoPermitidaException(actual.getNombre() + " no puede deshipotecar " + p.getNombre() + ". No está hipotecada.");
        }

        // Calcular coste (Hipoteca + 10% de interés)
        int coste = (int) (p.getHipoteca() * 1.10);

        // Verificar solvencia y ejecutar
        if (actual.getFortuna() < coste) {
            notificarMensaje("No tienes dinero suficiente para deshipotecar " + p.getNombre() + ". Coste: " + coste + "€");
            throw new SaldoInsuficienteException(actual.getNombre(), coste- actual.getFortuna(),0);
        }

        // Pagar y cambiar estado
        actual.sumarGastos(coste);
        p.sethipotecada(0); // Marcar como deshipotecada


        String color = (p.getGrupo() != null ? p.getGrupo().getColorGrupo() : "-");
        notificarMensaje(actual.getNombre() + " paga " + coste + "€ por deshipotecar " + p.getNombre() +
                ". Ahora puede recibir alquileres y edificar en el grupo " + color + ".");
    }


    public void venderPropiedad(String tipo, String solarName, int cantidad) throws MonopolyEtseException{
        Jugador actual = jugadores.get(turno);
        Casilla c = this.tablero.encontrar_casilla(solarName);


        if (c == null) {
            throw new AccionInvalidaException("La casilla " + solarName + " no existe.");

        }

        //casteo a solar para usar sus metodos
        if (!(c instanceof Solar s)) { //si no es un solar
            throw new AccionInvalidaException("La casilla " + solarName + " no es un Solar, no se puede vender " + tipo + ".");

        }



        if (!s.getDueno().equals(actual)) {
            throw new AccionInvalidaException("No se pueden vender " + tipo + " en " + s.getNombre() + ". Esta propiedad no pertenece a " + actual.getNombre() + ".");

        }

        String t = tipo.toLowerCase();

        // Venta, usamos el casteo a s (solar)
        switch (t) {
            case "casas":
                if (s.getNumHoteles() > 0 || s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
                    throw new AccionInvalidaException("No puedes vender casas mientras tengas hoteles o mejoras superiores.");
                }
                int vendidas = Math.min(cantidad, s.getNumCasas());
                if (s.getNumCasas() == 0) {
                    throw new AccionInvalidaException("No hay casas en " + s.getNombre() + ".");

                }
                if (s.getNumCasas() < cantidad) {
                    notificarMensaje("No hay suficientes casas en esta propiedad. Se venderán " + s.getNumCasas() + " casas. Recibiendo " + s.getNumCasas() * s.getPrecioCasa());
                } else if (s.getNumCasas() == cantidad) { // Corrección menor: else if para evitar doble mensaje si son iguales
                    notificarMensaje("Se venden todas las casas de esta propiedad. Recibiendo " + cantidad * s.getPrecioCasa());
                }

                actual.sumarFortuna(vendidas * s.getPrecioCasa());


                s.setNumCasas(s.getNumCasas() - cantidad);

                eliminarEdificiosDe(s, actual, Edificio.Tipo.CASA, vendidas);
                break;

            case "pista":
                if (s.getNumPistas() == 0) {
                   throw new AccionInvalidaException("No hay pistas en " + s.getNombre() + ".");

                }
                if (cantidad > 1 && s.getNumPistas() == 1) {
                    notificarMensaje("Solamente se puede vender 1 pista, recibiendo " + s.getPrecioPistaDeporte());
                }
                if (s.getNumPistas() == 1) {
                    notificarMensaje("Vendiendo 1 pista, recibiendo " + s.getPrecioPistaDeporte());
                }

                actual.sumarFortuna(s.getPrecioPistaDeporte());
                s.setNumPistas(s.getNumPistas() - cantidad);
                eliminarEdificiosDe(s, actual, Edificio.Tipo.PISTA, 1);
                break;

            case "piscina":
                if (s.getNumPistas() > 0) {
                    throw new AccionInvalidaException("No puedes vender la piscina mientras tengas una pista de deporte.");
                }
                if (s.getNumPiscinas() == 0) {
                    throw new AccionInvalidaException("No hay piscinas en " + s.getNombre() + ".");

                }
                if (cantidad > 1 && s.getNumPiscinas() == 1) {
                    notificarMensaje("Solamente se puede vender 1 piscina, recibiendo " + s.getPrecioPiscina());
                }
                if (s.getNumPiscinas() == 1) {
                    notificarMensaje("Vendiendo 1 piscina, recibiendo " + s.getPrecioPiscina());
                }

                actual.sumarFortuna(s.getPrecioPiscina());
                s.setNumPiscinas(s.getNumPiscinas() - cantidad);
                eliminarEdificiosDe(s, actual, Edificio.Tipo.PISCINA, 1);
                break;

            case "hoteles":
                if (s.getNumPiscinas() > 0 || s.getNumPistas() > 0) {
                    throw new AccionInvalidaException("No puedes vender hoteles mientras tengas piscinas o pistas.");
                }
                if (s.getNumHoteles() == 0) {
                   throw new AccionInvalidaException("No hay hoteles en " + s.getNombre() + ".");

                }
                if (cantidad > 1 && s.getNumHoteles() == 1) {
                    notificarMensaje("Solamente se puede vender 1 hotel, recibiendo " + s.getPrecioHotel());
                    // s.setNumHoteles(s.getNumHoteles() - 1); // Estaba duplicado en tu código
                }
                if (s.getNumHoteles() == 1) {
                    notificarMensaje("Vendiendo 1 hotel, recibiendo " + s.getPrecioHotel());
                    // s.setNumHoteles(s.getNumHoteles() - 1);
                }
                // En tu original restabas dos veces arriba, aquí unificamos la resta final:
                s.setNumHoteles(s.getNumHoteles() - 1);

                actual.sumarFortuna(s.getPrecioHotel());
                eliminarEdificiosDe(s, actual, Edificio.Tipo.HOTEL, 1);
                break;

            default:
                throw new AccionInvalidaException("Tipo de edificio no válido: " + tipo);
        }
    }

    public void estadisticasJugador(String nombreJugador) throws MonopolyEtseException{
        if(!hayJugadores()){
            throw new AccionInvalidaException("No hay jugadores");
        }
        Jugador jugador=null;
        for(Jugador j:jugadores){
            if(j.getNombre().equals(nombreJugador)){
                jugador=j;
                break;
            }
        }
        if(jugador==null){
            throw new AccionInvalidaException("No existe dicho jugador");
        }

        //imprimimos por pantalla todas las banderas que pusimos por pantalla
        EstadisticasJugador estadisticas=jugador.getEstadisticas();
        System.out.println("estadísticas " + nombreJugador);
        System.out.println("{");
        System.out.println("    dineroInvertido: " + estadisticas.getDineroInvertido() + ",");
        System.out.println("    pagoTasasImpuestos: " + estadisticas.getPagoTasasImpuestos() + ",");
        System.out.println("    pagoDeAlquileres: " + estadisticas.getPagoDeAlquileres() + ",");
        System.out.println("    cobroDeAlquileres: " + estadisticas.getCobroDeAlquileres() + ",");
        System.out.println("    pasarPorCasillaDeSalida: " + estadisticas.getPasarPorCasillaDeSalida() + ",");
        System.out.println("    premiosInversionesOBote: " + estadisticas.getPremiosInversionesOBote() + ",");
        System.out.println("    vecesEnLaCarcel: " + estadisticas.getVecesEnLaCarcel());
        System.out.println("}");
    }

    // Método auxiliar actualizado para aceptar 'Solar' en vez de 'Casilla'
    private void eliminarEdificiosDe(Solar s, Jugador propietario, Edificio.Tipo tipo, int n) {
        if (n <= 0) return;

        int quitados = 0;

        java.util.List<Edificio> copiaEdificios = new ArrayList<>(s.getEdificios());

        for (Edificio e : copiaEdificios) {
            if (quitados >= n) break;

            if (e.getTipo() == tipo && e.getPropietario() == propietario) {
                // e.eliminar() llama a s.eliminarEdificio(e) y actualiza contadores
                s.eliminarEdificio(e);


                // Eliminamos el ID del mapa global
                numeroPorEdificio.remove(e.getId());

                quitados++;
            }
        }
    }


    public void procesarCasillaEspecial(Jugador jugador, String tipoCasilla) throws MonopolyEtseException {
        Carta c = this.baraja.sacarCarta(tipoCasilla); // Usamos nuestra instancia
        if (c != null) {
            c.accion(jugador, this);
        } else {
            throw new AccionInvalidaException("No hay cartas de " + tipoCasilla + " disponibles.");
        }
    }

    public void moverJugadorACasilla(Jugador jugador, String nombreCasilla, boolean cobrarSalida) throws MonopolyEtseException {
        Casilla destino = tablero.encontrar_casilla(nombreCasilla);
        if (destino != null) {
            if (cobrarSalida && pasaPorSalida(jugador.getAvatar().getPosicion(), destino)) {
                jugador.sumarFortuna(2000000);
                jugador.getEstadisticas().sumarPasarPorSalida();
            }
            jugador.getAvatar().setPosicion(destino);
            try {
                destino.evaluarCasilla(jugador, this, 0);
            } catch (MonopolyEtseException e) {
                notificarMensaje("Error al evaluar la casilla destino: " + e.getMessage());
                throw e;
            }
        }
    }

    // funcion analoga a la anterior pero mueve hacia atras
    public void moverJugadorAPosicion(Jugador jugador, int posicion) throws MonopolyEtseException {
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (c.getPosicion() == posicion) {
                    jugador.getAvatar().setPosicion(c);
                    try {
                        c.evaluarCasilla(jugador, this, 0);
                    } catch (MonopolyEtseException e) {
                        notificarMensaje("Error al evaluar la casilla tras mover: " + e.getMessage());
                        throw e;
                    }
                    return;
                }
            }
        }
    }

    private boolean pasaPorSalida(Casilla actual, Casilla destino) {
        return destino.getPosicion() < actual.getPosicion();
    }

    private boolean hayJugadores() {
        return jugadores != null && !jugadores.isEmpty();
    }

    public void estadisticasJuego() throws MonopolyEtseException{
        if (!hayJugadores()) {
            throw new AccionInvalidaException("No hay jugadores");
        }

        // Variables para almacenar los récords
        Casilla masRentable = null;
        Casilla masFrecuentada = null;
        Grupo masRentableGrupo = null;
        Jugador masVueltas = null;
        Jugador enCabeza = null;

        long maxRentabilidadCasilla = -1;
        int maxFrecuencia = -1;
        long maxRentabilidadGrupo = -1;
        int maxVueltas = -1;
        long maxFortunaTotal = -1;

        // Recorremos todas las casillas del tablero
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                if (c == null) continue;

                //Casilla mas rentable
                if (c instanceof Solar s) {
                    if (s.getDineroGenerado() > maxRentabilidadCasilla) {
                        maxRentabilidadCasilla = s.getDineroGenerado();
                        masRentable = s;
                    }
                }

                // Casilla más frecuentada
                if (c.frecuenciaVisita() > maxFrecuencia) {
                    maxFrecuencia = c.frecuenciaVisita();
                    masFrecuentada = c;
                }
            }
        }

        // Recorremos todos los grupos
        java.util.HashMap<String, Grupo> grupos = tablero.getGrupos();
        if (grupos != null) {
            for (Grupo g : grupos.values()) {
                // Grupo más rentable
                if (g.getRentabilidad() > maxRentabilidadGrupo) {
                    maxRentabilidadGrupo = g.getRentabilidad();
                    masRentableGrupo = g;
                }
            }
        }

        // Recorremos todos los jugadores
        for (Jugador j : jugadores) {
            if (j == null || j.getNombre().equals("Banca")) continue;

            // Jugador con más vueltas
            if (j.getVueltas() > maxVueltas) {
                maxVueltas = j.getVueltas();
                masVueltas = j;
            }

            // Jugador en cabeza
            long fortunaTotal = valorTotalJugador(j);
            if (fortunaTotal > maxFortunaTotal) {
                maxFortunaTotal = fortunaTotal;
                enCabeza = j;
            }
        }

        // Imprimir resultados
        notificarMensaje("estadisticas");
        notificarMensaje("{");
        notificarMensaje("  casillaMasRentable: " + (masRentable != null ? masRentable.getNombre() : "-") + ",");
        notificarMensaje("  grupoMasRentable: " + (masRentableGrupo != null ? masRentableGrupo.getColorGrupo() : "-") + ",");
        notificarMensaje("  casillaMasFrecuentada: " + (masFrecuentada != null ? masFrecuentada.getNombre() : "-") + ",");
        notificarMensaje("  jugadorMasVueltas: " + (masVueltas != null ? masVueltas.getNombre() : "-") + ",");
        notificarMensaje("  jugadorEnCabeza: " + (enCabeza != null ? enCabeza.getNombre() : "-"));
        notificarMensaje("}");
    }

    private long valorTotalJugador(Jugador j) {
        long total = j.getFortuna();

        for (Casilla c : j.getPropiedades()) {
            // Sumar el valor base de la propiedad
            if (c instanceof Propiedad) {
                total += (c.getValor());
            }

            // Sumar el valor de los edificios (solo si es Solar)
            if (c instanceof Solar s) {
                total += (long) s.getNumCasas() * s.getPrecioCasa();
                total += (long) s.getNumHoteles() * s.getPrecioHotel();
                total += (long) s.getNumPiscinas() * s.getPrecioPiscina();
                total += (long) s.getNumPistas() * s.getPrecioPistaDeporte();
            }
        }
        return total;
    }
    private Jugador buscarJugador(String nombre){
        if(nombre==null) return null;
        for(Jugador j:jugadores){
            if(j.getNombre().equalsIgnoreCase(nombre)) return j;
        }
        return null;
    }

    public void proponerTrato(String comando) throws MonopolyEtseException {
        if (!hayJugadores()) throw new AccionInvalidaException("No hay jugadores.");
        Jugador proponente = jugadores.get(turno);
        // Parseo inicial
        int posDosPuntos = comando.indexOf(':'); // Buscamos el índice del separador ':' que divide al jugador destino del resto del comando.
        if (posDosPuntos == -1) throw new AccionInvalidaException("Formato incorrecto. Uso: trato Jugador: cambiar (A, B)");

        String nombreDestino = comando.substring(6, posDosPuntos).trim(); // Extraemos el nombre: desde el carácter 6 (saltando "trato ") hasta los dos puntos.
        String resto = comando.substring(posDosPuntos + 1).trim(); // Extraemos el resto de la cadena ignorando los dos puntos (+1) para analizar la oferta.

        if (!resto.startsWith("cambiar (") || !resto.endsWith(")")) { //si no empieza por cambiar y no acaba con ), error
            throw new AccionInvalidaException("Formato incorrecto. Debe ser: cambiar (lo_que_das, lo_que_pides)");
        }

        String contenido = resto.substring(9, resto.length() - 1); //quitamos "cambiar (" (9 caracteres) y el último ")", nos deja solo el contenido interior: "Solar1, Solar2" o "Solar1, 500"
        String[] lados = contenido.split(","); // Dividimos oferta y demanda usando la coma como delimitador
        if (lados.length != 2) throw new AccionInvalidaException("Debes separar oferta y demanda con una coma.");

        Jugador propuesto = buscarJugador(nombreDestino);
        if (propuesto == null) throw new AccionInvalidaException("El jugador " + nombreDestino + " no existe.");
        if (propuesto == proponente) throw new AccionInvalidaException("No puedes hacer tratos contigo mismo.");


        // Usamos un método helper que lanza excepción si algo no cuadra
        Object[] oferta = analizarParteTrato(lados[0].trim()); //Tomas la parte izquierda del trato y le quitas los espacios sobrantes.
        Propiedad propOfrece = (Propiedad) oferta[0]; //object es generico, le decimos que lo que hay en la posicion 0 es una propiedad
        int dineroOfrece = (int) oferta[1]; //lo que hay en la posicion 1 es un enetero

        //lo mismo de antes pero con la parte derecha del trato
        Object[] demanda = analizarParteTrato(lados[1].trim());
        Propiedad propPide = (Propiedad) demanda[0];
        int dineroPide = (int) demanda[1];

        // Validamos
        if(propOfrece == null && propPide == null){
            throw new AccionInvalidaException("No se permiten tratos exclusivamente de dinero por dinero");
        }
        if (propOfrece != null && !propOfrece.perteneceAJugador(proponente)) {
            throw new AccionInvalidaException("No puedes ofrecer " + propOfrece.getNombre() + " porque no es tuya.");
        }
        if (propPide != null && !propPide.perteneceAJugador(propuesto)) {
            throw new AccionInvalidaException("No puedes pedir " + propPide.getNombre() + " porque no pertenece a " + propuesto.getNombre());
        }
        if (dineroOfrece > proponente.getFortuna()) {
            throw new SaldoInsuficienteException(proponente.getNombre(), dineroOfrece - proponente.getFortuna(),0);
        }

        if (propOfrece instanceof Solar s && !s.getEdificios().isEmpty()) {
            throw new AccionInvalidaException("No puedes ofrecer " + s.getNombre() + " porque tiene edificios. Véndelos primero.");
        }
        if (propPide instanceof Solar s && !s.getEdificios().isEmpty()) {
            throw new AccionInvalidaException("No puedes pedir " + s.getNombre() + " porque tiene edificios construidos.");
        }
        //  Crear trato
        Trato nuevoTrato = new Trato(proponente, propuesto, propOfrece, dineroOfrece, propPide, dineroPide);
        propuesto.recibirTrato(nuevoTrato);
        proponente.recibirTrato(nuevoTrato); // Opcional: para que el proponente también lo vea si quieres

        notificarMensaje("Trato propuesto correctamente: " + nuevoTrato.getId());
    }

    private Object[] analizarParteTrato(String texto) throws AccionInvalidaException {
        Propiedad p = null;
        int d = 0; //acumula el dinero

        if (texto.isEmpty() || texto.equalsIgnoreCase("nada")) {
            return new Object[]{null, 0};
        }

        String[] elementos = texto.split(" y "); // Soportamos múltiples elementos separados por " y "
        for (String item : elementos) {
            item = item.trim(); // Limpieza de espacios
            if (item.isEmpty()) continue;

            // ESTRATEGIA DE PARSEO:
            // 1. Intentamos convertirlo a número (Dinero).
            // 2. Si falla (NumberFormatException), asumimos que es el nombre de una Propiedad.
            // Intentamos ver si es número
            try {
                int val = Integer.parseInt(item);//convertimos a entero
                if (val < 0) throw new AccionInvalidaException("No puedes usar cantidades negativas: " + val);
                d += val; //sumamos dinero total del trato
            } catch (NumberFormatException e) {
                // Si no es número, TIENE que ser una propiedad válida
                Casilla c = tablero.encontrar_casilla(item);
                if (c == null) {
                    throw new AccionInvalidaException("La propiedad '" + item + "' no existe en el tablero.");
                }
                // Validación de tipo: Solo se pueden intercambiar Propiedades
                if (!(c instanceof Propiedad)) {
                    throw new AccionInvalidaException("'" + item + "' no es una propiedad intercambiable.");
                }
                //Solo 1 propiedad por trato
                if (p != null) {
                    throw new AccionInvalidaException("Solo puedes incluir una propiedad por lado del trato.");
                }
                p = (Propiedad) c;
            }
        }
        return new Object[]{p, d}; // Devolvemos un par genérico con [Propiedad, int Dinero]
    }

    public void listarTratos() {
        if (!hayJugadores()) return;
        Jugador actual = jugadores.get(turno);

        // obtengo la lista de tratos para ver los que estan pendientes
        java.util.Collection<Trato> tratos = actual.getListaTratos();
        if (tratos.isEmpty()) { //vacio
            notificarMensaje("No existen tratos pendientes.");
            return;
        }
        // miramos que tratos influyen al jugador actual, el que tiene turno, e imprimimos cuando es proponente y receptos
        notificarMensaje("Tratos vinculados a " + actual.getNombre() + ":");
        for (Trato t : tratos) {
            String rol = (t.getProponente() == actual) ? "[Propuesto por ti]" : "[Propuesto por "+t.getProponente().getNombre()+"]"; //lo propones tu o otro jugador
            String receptor = (t.getPropuesto() == actual) ? "[Propuesto hacia ti]" : "[Propuesto hacia "+t.getPropuesto().getNombre()+"]";
            notificarMensaje(t + " " + rol + " " + receptor);
        }
    }

    public void aceptarTrato(String idTrato) throws MonopolyEtseException {
        if (!hayJugadores()) return;
        Jugador aceptante = jugadores.get(turno);
        // obtenemos el trato que afecta a la persona receptora
        Trato t = aceptante.getTrato(idTrato);

        if (t == null) throw new AccionInvalidaException("Trato no encontrado: " + idTrato);
        if (t.getProponente() == aceptante) throw new AccionInvalidaException("No puedes aceptar tu propio trato.");

        Jugador proponente = t.getProponente();

        // comprobamos si el trato sigue siendo valido
        if (t.getPropiedadOfrecida() != null && !t.getPropiedadOfrecida().perteneceAJugador(proponente)) {
            throw new AccionInvalidaException("El trato ya no es válido: " + proponente.getNombre() + " ya no tiene " + t.getPropiedadOfrecida().getNombre());
        }
        if (t.getPropiedadDeseada() != null && !t.getPropiedadDeseada().perteneceAJugador(aceptante)) {
            throw new AccionInvalidaException("El trato ya no es válido: Tú ya no tienes " + t.getPropiedadDeseada().getNombre());
        }
        if (proponente.getFortuna() < t.getDineroOfrecido()) {
            throw new SaldoInsuficienteException(proponente.getNombre(), t.getDineroOfrecido() - proponente.getFortuna(),0);
        }
        if (aceptante.getFortuna() < t.getDineroDeseado()) {
            throw new SaldoInsuficienteException(aceptante.getNombre(), t.getDineroDeseado() - aceptante.getFortuna(),0);
        }

        // gestionamos el dinero quien se lo da a quien
        if (t.getDineroOfrecido() > 0) {
            proponente.sumarGastos(t.getDineroOfrecido());
            aceptante.sumarFortuna(t.getDineroOfrecido());
        }
        if (t.getDineroDeseado() > 0) {
            aceptante.sumarGastos(t.getDineroDeseado());
            proponente.sumarFortuna(t.getDineroDeseado());
        }

        // gestionamos las propiedades para transferir
        if (t.getPropiedadOfrecida() != null) {
            transferirPropiedad(t.getPropiedadOfrecida(), proponente, aceptante);
        }
        if (t.getPropiedadDeseada() != null) {
            transferirPropiedad(t.getPropiedadDeseada(), aceptante, proponente);
        }

        aceptante.eliminarTrato(idTrato);
        proponente.eliminarTrato(idTrato); // Borrarlo también del proponente

        notificarMensaje("¡Trato " + idTrato + " aceptado y ejecutado con éxito!");
    }
    // se transfieren las propiedades quitando primero y despues añadiendo para que no se buguee por tener dos dueños
    private void transferirPropiedad(Propiedad p, Jugador origen, Jugador destino) {
        origen.getPropiedades().remove(p);
        destino.getPropiedades().add(p);
        p.setDueno(destino);
    }

    public void eliminarTrato(String idTrato) throws MonopolyEtseException{
        if (!hayJugadores()) throw new AccionInvalidaException("No hay jugadores en el juego.");
        Jugador actual = jugadores.get(turno);

        Trato t = actual.getTrato(idTrato);
        if (t == null) {
            throw new AccionInvalidaException("No se encontró el trato " + idTrato);

        }

        // miramos que sea el proponente el que intente eliminar
        if (t.getProponente() != actual) {
            throw new AccionInvalidaException("No puedes eliminar este trato porque no lo propones.Solo puedes aceptarlo o ignorarlo.");
        }

        // Eliminar de ambos jugadores involucrados
        Jugador otro = t.getPropuesto();

        actual.eliminarTrato(idTrato);
        otro.eliminarTrato(idTrato);

        notificarMensaje("Se ha eliminado el trato " + idTrato);
    }

}




