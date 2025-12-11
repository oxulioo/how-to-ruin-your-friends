package monopoly.casilla;

import monopoly.Juego;
import monopoly.jugador.Jugador;

import java.util.ArrayList;

public class Grupo {

    private final ArrayList<Casilla> miembros;
    private final String colorGrupo;
    private final int numCasillas;
    private long rentabilidad = 0;

    public Grupo(Casilla cas1, Casilla cas2, String colorGrupo) {
        this.miembros=new ArrayList<>();
        this.colorGrupo=colorGrupo;
        this.numCasillas=2;
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
    }

    public Grupo(Casilla cas1, Casilla cas2, Casilla cas3, String colorGrupo) {
        this.miembros=new ArrayList<>();
        this.colorGrupo=colorGrupo;
        this.numCasillas=3;
        anhadirCasilla(cas1);
        anhadirCasilla(cas2);
        anhadirCasilla(cas3);
    }

    public void anhadirCasilla(Casilla miembro) {
        if(miembro==null) return;
        // Mantenemos tu comprobación de tipo
        if(!Casilla.TSOLAR.equals(miembro.getTipo())){
            throw new IllegalArgumentException("Error de configuración: El grupo solo acepta casillas tipo Solar. Casilla recibida: " + miembro.getNombre());
        }
        if(!miembros.contains(miembro)){
            miembros.add(miembro);
            // Si es solar, podemos castear a Propiedad o Solar para setear el grupo
            if (miembro instanceof Propiedad) {
                ((Propiedad)miembro).setGrupo(this);
            }
        }
    }

    public boolean esDuenoGrupo(Jugador jugador) {
        if (jugador == null || miembros.size() != numCasillas || numCasillas == 0) {
            return false;
        }
        for (Casilla c : miembros) {
            if (c instanceof Propiedad) {
                Jugador d = ((Propiedad)c).getDueno(); //Downcasting
                if (d == null || d != jugador) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public String getColorGrupo(){ return colorGrupo; }
    public void sumarRentabilidad(long cant) { rentabilidad += cant; }
    public long getRentabilidad() { return rentabilidad; }
    public ArrayList<Casilla> getMiembros() { return this.miembros; }
}