package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.*;
import java.util.Scanner;

import common.IntCallbackCliente;
import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

import javax.swing.*;
import javax.xml.transform.sax.SAXSource;


public class ClienteFlotaRMI {

    public static final int NUMFILAS = 8, NUMCOLUMNAS = 8, NUMBARCOS = 6;
    private String USERNAME;
    private int quedan = NUMBARCOS, disparos = 0;
    private GuiTablero guiTablero = null;
    private IntServidorPartidasRMI partida = null;
    private IntServidorJuegoRMI sj = null;
    private IntCallbackCliente cliente = null;
    private Scanner input;

    public static void main(String args[]) {
        ClienteFlotaRMI cliente = new ClienteFlotaRMI();
        cliente.conecta();
    } //end main

    private void conecta() {
        try {
            // start a security manager - this is needed if stub
            // downloading is in use for this application.

            System.setSecurityManager(new SecurityManager());
            String registryURL = "rmi://localhost:1099/sinkthefloat";
            // find the remote object and cast it to an
            //   interface object
            sj = (IntServidorJuegoRMI) Naming.lookup(registryURL);
            System.out.println("Lookup completed ");
            // invoke the remote method
            partida = sj.nuevoServidorPartidas();
            partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
            System.out.println("Escriba su nombre de usuario");
            input = new Scanner(System.in);
            USERNAME = input.next();
            cliente = new ImpCallbackCliente();
            SwingUtilities.invokeLater(() -> {
                guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
                guiTablero.dibujaTablero();
            });
        } // end try
        catch (Exception e) {
            System.out.println("Exception in ClienteFlotaRMI: " + e);
        }
    }

    private class GuiTablero {

        private int numFilas, numColumnas;
        private JMenuBar menuBar;
        private JFrame frame = null; // Tablero de juego
        private JLabel estado = null; // Texto en el panel de estado
        private JButton buttons[][] = null; // Botones asociados a las casillas
        // de la partida

        /**
         * Constructor de una tablero dadas sus dimensiones
         */
        GuiTablero(int numFilas, int numColumnas) {
            this.numFilas = numFilas;
            this.numColumnas = numColumnas;
            frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        /**
         * Dibuja el tablero de juego y crea la partida inicial
         */
        public void dibujaTablero() {
            frame.setTitle(USERNAME);
            anyadeMenu();
            anyadeMenuPartidas();
            anyadeGrid(numFilas, numColumnas);
            anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
            frame.setSize(300, 300);
            frame.setVisible(true);
        } // end dibujaTablero

        /**
         * Anyade el menu de opciones del juego y le asocia un escuchador
         */
        private void anyadeMenu() { //COMENTAR
            MenuListener mlist = new MenuListener();
            JMenu menu;
            JMenuItem mossol, npar, salir;
            menuBar = new JMenuBar();
            menu = new JMenu("Opciones");
            menuBar.add(menu);
            mossol = new JMenuItem("Mostrar Solucion");
            npar = new JMenuItem("Nueva Partida");
            salir = new JMenuItem("Salir");
            menu.add(mossol);
            menu.add(npar);
            menu.add(salir);
            mossol.addActionListener(mlist);
            npar.addActionListener(mlist);
            salir.addActionListener(mlist);
            frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        } // end anyadeMenu

        private void anyadeMenuPartidas() {
            PartidaListener plist = new PartidaListener();
            JMenu partidas;
            JMenuItem propon, borrar, listar, aceptar;
            partidas = new JMenu("Partidas");
            menuBar.add(partidas);
            propon = new JMenuItem("Propon Partida");
            borrar = new JMenuItem("Borrar Partida");
            listar = new JMenuItem("Listar Partidas");
            aceptar = new JMenuItem("Aceptar Partida");
            partidas.add(propon);
            partidas.add(borrar);
            partidas.add(listar);
            partidas.add(aceptar);
            propon.addActionListener(plist);
            borrar.addActionListener(plist);
            listar.addActionListener(plist);
            aceptar.addActionListener(plist);

        }

        /**
         * Anyade el panel con las casillas del mar y sus etiquetas. Cada
         * casilla sera un boton con su correspondiente escuchador
         *
         * @param nf numero de filas
         * @param nc numero de columnas
         */
        private void anyadeGrid(int nf, int nc) { //COMENTAR
            buttons = new JButton[nf][nc];
            ButtonListener blist = new ButtonListener();
            GridLayout centroLay = new GridLayout(nf + 1, nc + 2);
            JPanel centro = new JPanel();
            centro.setLayout(centroLay);
            centro.add(new JLabel(" "));
            for (int i = 1; i <= nc; i++) {
                centro.add(new JLabel(Integer.toString(i), SwingConstants.CENTER));
            }
            centro.add(new JLabel(" "));
            char letra = 'A';
            for (int i = 0; i < nf; i++) {
                centro.add(new JLabel(String.valueOf((letra)), SwingConstants.CENTER));

                for (int n = 0; n < nc; n++) {
                    buttons[i][n] = new JButton();
                    buttons[i][n].addActionListener(blist);
                    buttons[i][n].putClientProperty("fila", i);
                    buttons[i][n].putClientProperty("columna", n);
                    centro.add(buttons[i][n]);
                }
                centro.add(new JLabel(String.valueOf((letra)), SwingConstants.CENTER));
                letra++;
            }

            frame.getContentPane().add(centro, BorderLayout.CENTER);

        }

        /**
         * Anyade el panel de estado al tablero
         *
         * @param cadena cadena inicial del panel de estado
         */
        private void anyadePanelEstado(String cadena) { // DONE
            JPanel panelEstado = new JPanel();
            estado = new JLabel(cadena);
            panelEstado.add(estado);
            // El panel de estado queda en la posición SOUTH del frame
            frame.getContentPane().add(panelEstado, BorderLayout.SOUTH);
        } // end anyadePanel Estado

        /**
         * Cambia la cadena mostrada en el panel de estado
         *
         * @param cadenaEstado nuevo estado
         */
        public void cambiaEstado(String cadenaEstado) { // DONE
            estado.setText(cadenaEstado);
        } // end cambiaEstado

        /**
         * Muestra la solucion de la partida y marca la partida como finalizada
         */
        public void muestraSolucion() {
            // Colorea casillas a mar y despues comprueba el id de cada casilla para pintarlo
            for (int i = 0; i < numFilas; i++) {
                for (int j = 0; j < numColumnas; j++) {
                    int casillaId = 0;
                    try {
                        casillaId = partida.pruebaCasilla(i, j);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (casillaId == -1)
                        guiTablero.pintaBoton(buttons[i][j], Color.CYAN);
                    else if (casillaId == -2)
                        guiTablero.pintaBoton(buttons[i][j], Color.MAGENTA);
                    else
                        guiTablero.pintaBoton(buttons[i][j], Color.MAGENTA);

                }

            }

        } // end muestraSolucion

        /**
         * Pinta un barco como hundido en el tablero
         *
         * @param cadenaBarco cadena con los datos del barco codifificados como
         *                    "filaInicial#columnaInicial#orientacion#tamanyo"
         */
        public void pintaBarcoHundido(String cadenaBarco) {
            // Una vez hundido se encarga de pintarlo segun orientacion
            String[] partes = cadenaBarco.split("#");

            int fInicial = Integer.parseInt(partes[0]);
            int cInicial = Integer.parseInt(partes[1]);
            String orientacion = partes[2];
            int tamayo = Integer.parseInt(partes[3]);

            if (orientacion.equals("V")) {
                for (int i = 0; i < tamayo; i++) {
                    pintaBoton(buttons[fInicial + i][cInicial], Color.RED);
                }
            } else {
                for (int i = 0; i < tamayo; i++) {
                    pintaBoton(buttons[fInicial][cInicial + i], Color.RED);
                }
            }
        }

        // end pintaBarcoHundido

        /**
         * Pinta un botón de un color dado
         *
         * @param b     boton a pintar
         * @param color color a usar
         */
        public void pintaBoton(JButton b, Color color) {
            b.setBackground(color);
            // El siguiente código solo es necesario en Mac OS X
            b.setOpaque(true);
            b.setBorderPainted(false);
        } // end pintaBoton

        /**
         * Limpia las casillas del tablero pintándolas del gris por defecto
         */
        public void limpiaTablero() {
            for (int i = 0; i < numFilas; i++) {
                for (int j = 0; j < numColumnas; j++) {
                    buttons[i][j].setBackground(null);
                    buttons[i][j].setOpaque(true);
                    buttons[i][j].setBorderPainted(true);
                }
            }
        } // end limpiaTablero

        /**
         * Destruye y libera la memoria de todos los componentes del frame
         */
        public void liberaRecursos() {
            frame.dispose();
        } // end liberaRecursos


    } // end class GuiTablero

    /******************************************************************************************/
    /********************* * CLASE INTERNA MenuListener * ***************************************/
    /******************************************************************************************/

    /**
     * Clase interna que escucha el menu de Opciones del tablero
     */
    private class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) { // Segun elemento menu
            // realiza accion
            JMenuItem elem = (JMenuItem) e.getSource();
            String texto = elem.getText();
            switch (texto) {
                case ("Mostrar Solucion"):
                    guiTablero.muestraSolucion();
                    break;
                case ("Nueva Partida"):
                    try {
                        partida.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    // Resetea el juego a partir de aqui
                    guiTablero.limpiaTablero();
                    quedan = NUMBARCOS;
                    disparos = 0;
                    guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
                    break;
                case ("Salir"):
                    guiTablero.liberaRecursos();
                    input.close();
                    try {
                        sj.borraPartida(USERNAME);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                    break;
            }

        } // end actionPerformed

    } // end class MenuListener

    private class PartidaListener implements ActionListener {


        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem elem = (JMenuItem) e.getSource();
            String texto = elem.getText();
            switch (texto) {
                case ("Propon Partida"):
                    try {
                        if (sj.proponPartida(USERNAME, cliente)) {
                            System.out.println("Has creado la partida con exito");
                        } else {
                            System.out.println("Error en la creacion de la partida");
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case ("Borrar Partida"):
                    try {
                        if (sj.borraPartida(USERNAME)) {
                            System.out.println("Has borrado la partida con exito");
                        } else {
                            System.out.println("Error en el borrado de la partida");
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case ("Listar Partidas"):
                    try {
                        String[] partidas = sj.listaPartidas();
                        if (partidas.length == 0) {
                            System.out.println("No hay partidas creadas");
                            break;
                        }
                        System.out.println("Partidas creadas:");
                        for (String partida : partidas) {
                            System.out.println("  " + partida);
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case ("Aceptar Partida"):
                    System.out.println("Escribe el nombre del rival");
                    input.nextLine();
                    String RIVAL = input.next();
                    if (RIVAL.equals(USERNAME)) {
                        System.out.println("No puedes aceptar tu propia partida");
                        break;
                    }
                    try {
                        if (sj.aceptaPartida(USERNAME, RIVAL)) {
                            System.out.println("Partida aceptada con exito");
                        } else {
                            System.out.println("No se ha podido aceptar la partida");
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    break;
            }
        }
    }

    /******************************************************************************************/
    /********************* * CLASE INTERNA ButtonListener * *************************************/
    /******************************************************************************************/
    /**
     * Clase interna que escucha cada uno de los botones del tablero Para poder
     * identificar el boton que ha generado el evento se pueden usar las
     * propiedades de los componentes, apoyandose en los metodos
     * putClientProperty y getClientProperty
     */
    private class ButtonListener implements ActionListener { // Segun casilla la
        // prueba si no
        // ha sido ya
        // tocada

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton boton = new JButton();
            Color colorDefault = boton.getBackground();
            boton = (JButton) e.getSource();

            if (quedan > 0 && boton.getBackground().equals(colorDefault)) {
                int fila = (int) boton.getClientProperty("fila");
                int columna = (int) boton.getClientProperty("columna");
                int casilla = 0;
                try {
                    casilla = partida.pruebaCasilla(fila, columna);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                if (casilla == -1)
                    guiTablero.pintaBoton(boton, Color.CYAN);
                else if (casilla == -2)
                    guiTablero.pintaBoton(boton, Color.ORANGE);
                else {
                    try {
                        guiTablero.pintaBarcoHundido(partida.getBarco(casilla));
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    quedan--;
                }
                disparos++;

                if (quedan == 0) {
                    guiTablero.cambiaEstado("Game Over con disparos: " + disparos);

                } else {
                    guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
                }
            }
            // end actionPerformed

        } // end class ButtonListener


    }


}//end class
