package server;

import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

import java.rmi.RemoteException;

public class ImpServidorJuegoRMI implements IntServidorJuegoRMI{


    @Override
    public IntServidorPartidasRMI nuevoServidorPartidas() throws RemoteException {
        return new ImpServidorPartidasRMI();
    }

    @Override
    public boolean proponPartida(String nombreJugador, IntCallbackCliente callbackClientObject) throws RemoteException {
        return false;
    }

    @Override
    public boolean borraPartida(String nombreJugador) throws RemoteException {
        return false;
    }

    @Override
    public String[] listaPartidas() throws RemoteException {
        return new String[0];
    }

    @Override
    public boolean aceptaPartida(String nombreJugador, String nombreRival) throws RemoteException {
        return false;
    }
}
