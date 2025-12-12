package monopoly.logics.logica;
import monopoly.logics.exceptions.*;

import java.io.FileNotFoundException;
import java.util.Scanner;


public class Menu {

    private final Juego juego;
    private final Scanner scanner;

    // Constructor that receives the Juego instance
    public Menu(Juego juego) {
        this.juego = juego;
        this.scanner = new Scanner(System.in);
    }


    /*Método que interpreta el comando introducido y toma la acción correspondiente.
     * Parámetro: cadena de caracteres (el comando).
     */
    public void analizarComando(String comando) throws MonopolyEtseException {
        if (comando == null) return;
        if (comando.isEmpty()) return;

        // Exige exactamente "comandos" (en minúsculas) al principio
        if (comando.startsWith("comandos ")) {
            String ruta = comando.substring(9); // NO trim: se usa tal cual
            ejecutarFichero(ruta);
            return;
        }
        // crear jugador <Nombre> <tipoAvatar> (sin trim ni validación)
        if (comando.startsWith("crear jugador ")) {
            String resto = comando.substring("crear jugador ".length());
            int idx = resto.lastIndexOf(' ');
            if (idx <= 0 || idx == resto.length() - 1) {
                throw new AccionInvalidaException("Formato incorrecto. Uso: crear jugador <Nombre> <tipoAvatar>");
            }
            String nombre = resto.substring(0, idx);
            String tipo = resto.substring(idx + 1);
            juego.crearJugador(nombre, tipo);
            return;
        }

        // jugador: muestra quién tiene el turno
        switch (comando) {
            case "jugador" -> {
                juego.mostrarJugadorActual();
                return;
            }


            //listar jugadores
            case "listar jugadores" -> {
                juego.listarJugadores();
                return;
            }


            // lanzar dados
            case "lanzar dados" -> {
                juego.lanzarDados();
                return;
            }
        }

        if (comando.startsWith("lanzar dados ")) {
            String resto = comando.substring("lanzar dados ".length()); // p.ej. "3+4"
            int mas = resto.indexOf('+');
            if (mas > 0 && mas < resto.length() - 1) {
                String s1 = resto.substring(0, mas);
                String s2 = resto.substring(mas + 1);
                try {
                    int d1 = Integer.parseInt(s1); //transformamos texto en numeros enteros
                    int d2 = Integer.parseInt(s2);
                    juego.lanzarDadosForzado(d1, d2);
                    return;
                } catch (NumberFormatException nfe) {
                    throw new AccionInvalidaException("Los dados deben ser números enteros. Uso: lanzar dados X+Y");
                }
            }
            // si no trae '+', dejará pasar al caso normal (lanzar dados)
        }


        // acabar turno: pasa el turno al siguiente jugador
        if (comando.equals("acabar turno")) {
            juego.acabarTurno();
            return;
        }

        // salir cárcel: pagas 500.000 y quedas libre
        if (comando.equals("salir carcel") || comando.equals("salir cárcel")) {
            juego.salirCarcel();
            return;
        }

        // describir jugador <Nombre>
        if (comando.startsWith("describir jugador ")) {
            String nombreArg = comando.substring("describir jugador ".length());
            juego.descJugador(nombreArg);
            return;
        }

        // describir avatar <ID>
        if (comando.startsWith("describir avatar ")) {
            String id = comando.substring("describir avatar ".length()); // tal cual
            juego.descAvatar(id);
            return;
        }

        // listar avatares
        if (comando.equals("listar avatares")) {
            juego.listarAvatares();
            return;
        }


        // describir <Casilla>
        if (comando.startsWith("describir ")) {

            String nombreCasilla = comando.substring("describir ".length());
            juego.descCasilla(nombreCasilla);
            return;
        }

        // comprar <Propiedad>
        if (comando.startsWith("comprar ")) {
            String nombreProp = comando.substring("comprar ".length());
            juego.comprar(nombreProp);
            return;
        }

        // listar en venta
        if (comando.equals("listar enventa")) {
            juego.listarVenta();
            return;
        }

        //  ver tablero
        if (comando.equals("ver tablero")) {
            juego.verTablero();

        }

        if (comando.equals("edificar casa")) {
            juego.edificarCasa();
        }
        if (comando.equals("edificar hotel")) {
            juego.edificarHotel();
        }
        if (comando.equals("edificar piscina")) {
            juego.edificarPiscina();
        }

        if (comando.equals("edificar pista deporte")) {
            juego.edificarPista();
        }

        if (comando.equals("listar edificios")) {
            juego.listarEdificios(null);
        }
        if (comando.startsWith("listar edificios ")) {
            String nombreColor = comando.substring("listar edificios ".length());
            juego.listarEdificios(nombreColor);
        }
        if (comando.startsWith("hipotecar ")) {
            String nombreProp = comando.substring("hipotecar ".length());
            juego.hipotecar(nombreProp);
        }
        if (comando.startsWith("deshipotecar ")) {
            String nombreProp = comando.substring("deshipotecar ".length());
            juego.deshipotecar(nombreProp);
        }
        if (comando.startsWith("vender ")) {//La estructura es vender casas Solar1 3

            String[] partes = comando.substring(7).split(" ");//Elimino los 7 primeros caracteres, es decir, "vender ", quedando la información
            //El split(" "); Lo que hace es, con el resto que queda, separarlo por espacios
            if (partes.length < 3) {
                throw new AccionInvalidaException("Faltan datos. Uso: vender <tipo> <solar> <cantidad>");
            } else {//Si no hay tres partes en la entrada, no es válida
                String tipo = partes[0]; //"casas" por ejemplo
                String solar = partes[1]; //"Solar1" por ejemplo
                int cantidad;
                try {
                    cantidad = Integer.parseInt(partes[2]);
                } catch (NumberFormatException e) {
                    throw new AccionInvalidaException("La cantidad debe ser un número entero.");
                }
                juego.venderPropiedad(tipo, solar, cantidad);
            }
        }
        if (comando.equals("estadisticas")) {
            juego.estadisticasJuego();
        }
        if (comando.startsWith("estadisticas ")) {
            String nombreJugador = comando.substring("estadisticas ".length());
            juego.estadisticasJugador(nombreJugador);
        }
        if (comando.equalsIgnoreCase("ayuda") || comando.equalsIgnoreCase("help")) {
            mostrarAyuda();
            return;
        }
        if (comando.startsWith("trato ")) {
            juego.proponerTrato(comando);
            return;
        }

        if (comando.startsWith("aceptar trato")) {
            String id = comando.substring("aceptar".length()).trim();
            juego.aceptarTrato(id);
            return;
        }

        if (comando.startsWith("eliminar trato")) {
            String idtrato = comando.substring("eliminar".length()).trim();
            juego.eliminarTrato(idtrato);
            return;
        }

        if (comando.equals("tratos")) {
            juego.listarTratos();
        }
    }

    // Lector de comandos por consola
    public void run() throws MonopolyEtseException {
        //java.util.Scanner sc = new java.util.Scanner(System.in);
        juego.notificarMensaje("Monopoly listo. Escribe comandos. (\"salir\" para terminar)");

        while (true) {
            juego.notificarMensaje("> ");
            String linea = scanner.nextLine();

            if (linea == null) break;
            linea = linea.trim(); //quitamos espacios en blanco
            if (linea.isEmpty()) continue;
            if (linea.equalsIgnoreCase("salir")) {
                juego.notificarMensaje("¡Hasta luego!");
                break;
            }
            catcher(linea);
        }
    }

    private void mostrarAyuda() {
        juego.notificarMensaje("\n=== GUÍA DE COMANDOS MONOPOLY ===");
        juego.notificarMensaje("--- MOVIMIENTO Y TURNOS ---");
        juego.notificarMensaje(" • crear jugador <Nombre> <Avatar> : Crea un nuevo jugador (ej: crear jugador Ana coche).");
        juego.notificarMensaje(" • lanzar dados                    : Tira los dados y mueve tu avatar.");
        juego.notificarMensaje(" • acabar turno                    : Pasa el turno al siguiente jugador.");
        juego.notificarMensaje(" • salir carcel                    : Paga la fianza para salir de la cárcel.");
        juego.notificarMensaje(" • jugador                         : Muestra quién tiene el turno actual.");
        juego.notificarMensaje(" • ver tablero                     : Imprime el estado visual del tablero.");

        juego.notificarMensaje("\n--- GESTIÓN DE PROPIEDADES ---");
        juego.notificarMensaje(" • comprar <Casilla>               : Compra la casilla en la que estás (ej: comprar Solar1).");
        juego.notificarMensaje(" • edificar <tipo>                 : Construye 'casa', 'hotel', 'piscina' o 'pista deporte'.");
        juego.notificarMensaje(" • vender <tipo> <solar> <n>       : Vende n edificios de ese tipo (ej: vender casas Solar1 2).");
        juego.notificarMensaje(" • hipotecar <Casilla>             : Recibes la mitad del valor a cambio de bloquearla.");
        juego.notificarMensaje(" • deshipotecar <Casilla>          : Pagas la hipoteca + 10% para recuperarla.");

        juego.notificarMensaje("\n--- INFORMACIÓN ---");
        juego.notificarMensaje(" • describir <Casilla>             : Muestra detalles de un solar, impuesto, etc.");
        juego.notificarMensaje(" • describir jugador <Nombre>      : Muestra dinero, propiedades y perfil de un jugador.");
        juego.notificarMensaje(" • listar jugadores                : Lista todos los jugadores en la partida.");
        juego.notificarMensaje(" • listar enventa                  : Muestra qué propiedades siguen libres.");
        juego.notificarMensaje(" • listar edificios [color]        : Muestra tus edificios (opcional: filtrar por color de grupo).");
        juego.notificarMensaje(" • estadisticas [jugador]          : Muestra estadísticas globales o de un jugador específico.");

        juego.notificarMensaje("\n--- TRATOS ---");
        juego.notificarMensaje(" • trato <Jugador>: cambiar (A, B) : Propone un cambio. A es lo que das, B lo que pides.");
        juego.notificarMensaje("      Ejemplo: trato Maria: cambiar (Solar1, 5000) -> Das Solar1, pides 5000.");
        juego.notificarMensaje(" • tratos                          : Muestra los tratos que has propuesto o recibido.");
        juego.notificarMensaje(" • aceptar trato <id>              : Acepta y ejecuta un trato (ej: aceptar trato trato1).");
        juego.notificarMensaje(" • eliminar trato <id>             : Borra una propuesta de trato.");

        juego.notificarMensaje("\n • salir                           : Cierra el juego.");
    }

    private void catcher(String linea) {
        try {
            analizarComando(linea);
        } catch (SaldoInsuficienteException e) { //bancarrota
            // CASO 1: Problemas de dinero (Requisito 29: tratada diferente)
            // Podrías ponerlo en rojo, o sugerir hipotecar
            juego.notificarMensaje("[!] PROBLEMA DE FONDOS: " + e.getMessage());

        } catch (CompraNoPermitidaException e) {
            // CASO 2: Error específico de compras
            juego.notificarMensaje("[!] COMPRA RECHAZADA: " + e.getMessage());

        } catch (EdificacionNoPermitidaException e) {
            // CASO 3: Error al edificar
            juego.notificarMensaje("[!] NO PUEDES CONSTRUIR: " + e.getMessage());

        } catch (BancarrotaException e) {
            juego.notificarMensaje("[!] BANCARROTA: " + e.getMessage());
            juego.declararBancarrota();

        } catch (AccionInvalidaException e) {
            // CASO 4: Otros errores de reglas (turno, moverse, etc)
            juego.notificarMensaje("[!] Acción no válida: " + e.getMessage());

        } catch (MonopolyEtseException e) {
            // CASO 5: Cualquier otra excepción propia que se nos haya olvidado
            juego.notificarMensaje("Error del juego: " + e.getMessage());

        } catch (Exception e) {
            // Error inesperado de Java (Bugs, NullPointer, etc)
            juego.notificarMensaje("Ocurrió un error interno: " + e);

        }
    }


    private void ejecutarFichero(String ruta) {
        if (ruta == null) {
            juego.notificarMensaje("Error leyendo: " + null);
            return;
        }
        try (java.util.Scanner sc = new java.util.Scanner(new java.io.File(ruta))) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();

                catcher(linea);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
