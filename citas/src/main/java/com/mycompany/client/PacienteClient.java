// src/main/java/com/mycompany/client/PacienteClient.java

package com.mycompany.client;

import com.mycompany.model.Cita;
import com.mycompany.rmi.Citas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class PacienteClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Citas citasStub = (Citas) registry.lookup("CitasService");

            Scanner scanner = new Scanner(System.in);

            System.out.print("Usuario: ");
            String user = scanner.nextLine();
            System.out.print("Contraseña: ");
            String pass = scanner.nextLine();

            if (!citasStub.loginPaciente(user, pass)) {
                System.out.println("Login fallido.");
                return;
            }

            while (true) {
                System.out.println("\n1. Ver Citas\n2. Agendar Cita\n3. Cancelar Cita\n4. Salir");
                int opc = Integer.parseInt(scanner.nextLine());

                if (opc == 4) break;
                else if (opc == 1) {
                    citasStub.getCitasPaciente(user).forEach(System.out::println);
                } else if (opc == 2) {
                    System.out.print("Fecha (YYYY-MM-DD): ");
                    String fecha = scanner.nextLine();
                    System.out.print("Hora (HH:mm): ");
                    String hora = scanner.nextLine();
                    // ID fijo solo para ejemplo
                    int id = (int)(Math.random() * 1000);
                    citasStub.agendarCita(new Cita(id, user, "medico1", fecha, hora));
                    System.out.println("Cita agendada.");
                } else if (opc == 3) {
                    System.out.print("ID de cita a cancelar: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    citasStub.cancelarCita(id);
                    System.out.println("Cita cancelada.");
                }
            }

        } catch (Exception e) {
            System.err.println("Cliente error: " + e.getMessage());
        }
    }
}