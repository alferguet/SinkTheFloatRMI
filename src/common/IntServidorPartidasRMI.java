package common;

public interface IntServidorPartidasRMI {

    void nuevaPartida(int f, int c, int b);

    int pruebaCasilla(int f, int c);

    String getBarco(int idBarco);

    String[] getSolucion();

}
