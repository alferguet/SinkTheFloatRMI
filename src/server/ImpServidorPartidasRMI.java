package server;

import common.IntServidorPartidasRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ImpServidorPartidasRMI extends UnicastRemoteObject implements IntServidorPartidasRMI {


    Partida partida;

    ImpServidorPartidasRMI() throws RemoteException {
        super();
    }

    @Override
    public void nuevaPartida(int nf, int nc, int nb) throws RemoteException {
        partida = new Partida(nf, nc, nb);
    }

    @Override
    public int pruebaCasilla(int f, int c) throws RemoteException {
         return partida.pruebaCasilla(f, c);
    }

    @Override
    public String getBarco(int idBarco) throws RemoteException {
        return partida.getBarco(idBarco);
    }

    @Override
    public String[] getSolucion()throws RemoteException {
        return partida.getSolucion();
    }
}
