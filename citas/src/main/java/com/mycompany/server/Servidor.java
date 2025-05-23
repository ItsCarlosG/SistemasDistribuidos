// src/main/java/com/mycompany/server/Servidor.java

package com.mycompany.server;

import com.mycompany.rmi.Citas;
import com.mycompany.rmi.CitasImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
    public static void main(String[] args) {
        try {
            Citas stub = new CitasImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("CitasService", stub);
            System.out.println("Servidor RMI iniciado...");
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}