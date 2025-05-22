package com.mycompany;

import java.rmi.registry.Registry;
import javax.swing.JOptionPane;

public class Servidor {
    public static void main(String[] args) {
        try {
            Registry r = java.rmi.registry.LocateRegistry.createRegistry(1099);
            r.rebind("Citas", new rmi());
            JOptionPane.showMessageDialog(null, "Servidor funcionando");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Servidor no arrancó " + e);
        }
    }
}