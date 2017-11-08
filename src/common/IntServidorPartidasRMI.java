package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IntServidorPartidasRMI extends Remote {

    void nuevaPartida(int f, int c, int b) throws RemoteException;

    int pruebaCasilla(int f, int c) throws RemoteException;

    String getBarco(int idBarco) throws RemoteException;

    String[] getSolucion() throws RemoteException;

}
