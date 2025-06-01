package com.mycompany.server;

import com.mycompany.rmi.Citas;
import com.mycompany.rmi.CitasImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
    public static void main(String[] args) {
        try {
            Citas citasService = new CitasImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("CitasService", citasService);

            System.out.println("Servidor RMI de Citas Médicas iniciado y escuchando en el puerto 1099...");
            System.out.println("Servicio 'CitasService' registrado.");
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}