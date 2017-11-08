package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IntCallbackCliente extends Remote {

    void notify(String message) throws RemoteException;
}
