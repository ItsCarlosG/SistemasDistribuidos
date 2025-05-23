
package com.mycompany.client;

import com.mycompany.rmi.Citas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class MedicoClient {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Citas citasStub = (Citas) registry.lookup("CitasService");

            Scanner scanner = new Scanner(System.in);

            System.out.print("Usuario: ");
            String user = scanner.nextLine();
            System.out.print("Contraseña: ");
            String pass = scanner.nextLine();

            if (!citasStub.loginMedico(user, pass)) {
                System.out.println("Login fallido.");
                return;
            }

            while (true) {
                System.out.println("\n1. Ver Citas\n2. Modificar Estado\n3. Salir");
                int opc = Integer.parseInt(scanner.nextLine());

                if (opc == 3) break;
                else if (opc == 1) {
                    citasStub.getCitasMedico(user).forEach(System.out::println);
                } else if (opc == 2) {
                    System.out.print("ID de cita: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Nuevo estado (Atendida/Cancelada): ");
                    String estado = scanner.nextLine();
                    citasStub.modificarEstadoCita(id, estado);
                    System.out.println("Estado actualizado.");
                }
            }

        } catch (Exception e) {
            System.err.println("Cliente error: " + e.getMessage());
        }
    }
}