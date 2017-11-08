package server;

import common.IntCallbackCliente;
import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ImpServidorJuegoRMI extends UnicastRemoteObject implements IntServidorJuegoRMI{

    private Map<String,IntCallbackCliente> listaPartidas;

    ImpServidorJuegoRMI() throws RemoteException {
        super();
        listaPartidas = new HashMap<>();

    }

    @Override
    public IntServidorPartidasRMI nuevoServidorPartidas() throws RemoteException {
        return new ImpServidorPartidasRMI();
    }

    @Override
    public boolean proponPartida(String nombreJugador, IntCallbackCliente callbackClientObject) throws RemoteException {
        if(!listaPartidas.containsKey(nombreJugador)){
            listaPartidas.put(nombreJugador,callbackClientObject);
            System.out.println("Partida creada por el jugador: "+nombreJugador);
            return true;
        }
        System.out.println("Partida ya creada por el jugador: "+nombreJugador);
        return false;
    }

    @Override
    public boolean borraPartida(String nombreJugador) throws RemoteException {
        if(listaPartidas.containsKey(nombreJugador)){
            listaPartidas.remove(nombreJugador);
            System.out.println("Partida eliminada del jugador: "+nombreJugador);
            return true;
        }
        System.out.println("No tiene partida creada el jugador: "+nombreJugador);
        return false;
    }

    @Override
    public String[] listaPartidas() throws RemoteException {
        String[] retList = new String[listaPartidas.size()];
        int cont = 0;
        for (String partida : listaPartidas.keySet()) {
            retList[cont] = partida;
            cont++;
        }
        return retList;
    }

    @Override
    public boolean aceptaPartida(String nombreJugador, String nombreRival) throws RemoteException {
        try {
            if (listaPartidas.containsKey(nombreRival)) {
                listaPartidas.get(nombreRival).notify("El jugador " + nombreJugador + " ha aceptado tu partida");
                listaPartidas.remove(nombreRival);
                return true;
            } else {
                System.out.println("No existe una partida creada por " + nombreRival);
                return false;
            }
        } catch (Exception ex){
            System.out.println("Ha habido un error no se ha podido acceder a dicha partida");
            listaPartidas.remove(nombreRival);
            return false;
        }

    }
}
