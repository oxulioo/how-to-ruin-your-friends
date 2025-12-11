package monopoly.partida;

public class Dado {

    //Función para simular lanzamiento de un dado: devolverá un valor aleatorio entre 1 y 6.
    public int hacerTirada() {
        //sumamos uno porque genera del 0 al 5
        return (int) (Math.random() * 6) + 1;
    }

}
