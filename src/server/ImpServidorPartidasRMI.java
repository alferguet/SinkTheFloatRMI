package server;

import common.IntServidorPartidasRMI;

public class ImpServidorPartidasRMI implements IntServidorPartidasRMI {


    Partida partida;

    @Override
    public void nuevaPartida(int nf, int nc, int nb) {
        partida = new Partida(nf, nc, nb);
    }

    @Override
    public int pruebaCasilla(int f, int c) {
         return partida.pruebaCasilla(f, c);
    }

    @Override
    public String getBarco(int idBarco) {
        return partida.getBarco(idBarco);
    }

    @Override
    public String[] getSolucion() {
        return partida.getSolucion();
    }
}
