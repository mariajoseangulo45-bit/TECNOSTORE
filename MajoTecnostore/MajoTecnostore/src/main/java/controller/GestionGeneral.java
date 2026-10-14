package controller;

import view.Ingreso;

public class GestionGeneral {

    Ingreso i = new Ingreso();

    public void menuGeneral() {
        int op;
        do {
            op = i.escogerOpcionGeneral();
            switch (op) {
                case 1 -> {
                    GestionCelular g = new GestionCelular();
                    g.menu();
                }
                case 2 -> {
                    GestionCliente g = new GestionCliente();
                    g.menu();
                }
                case 3 -> {
                    GestorVenta g = new GestorVenta();
                    g.menu();
                }
                case 4 -> {
                    GestorReporte g = new GestorReporte();
                    g.menu();
                }
                case 5 -> {
                    System.out.println("Gracias por usar nuestra aplicacion");
                }
                default -> {
                    System.out.println("Opcion no encontrada");
                }
            }
        } while (op != 5);
    }
}
