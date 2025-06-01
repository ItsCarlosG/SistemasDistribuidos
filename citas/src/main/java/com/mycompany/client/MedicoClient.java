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

            System.out.println("--- Portal Médico (Consola) ---");
            System.out.print("Usuario Médico: ");
            String user = scanner.nextLine();
            System.out.print("Contraseña: ");
            String pass = scanner.nextLine();

            if (!citasStub.loginMedico(user, pass)) {
                System.out.println("Login fallido. Usuario o contraseña incorrectos.");
                return;
            }
            System.out.println("Login exitoso como Médico: " + user);

            while (true) {
                System.out.println("\nOpciones para Médico:");
                System.out.println("1. Ver Citas Asignadas");
                System.out.println("2. Modificar Estado de Cita");
                System.out.println("3. Salir");
                System.out.print("Seleccione una opción: ");
                String input = scanner.nextLine();
                int opc;
                try {
                    opc = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Opción inválida. Intente de nuevo.");
                    continue;
                }

                if (opc == 3) {
                    System.out.println("Saliendo del portal médico...");
                    break;
                } else if (opc == 1) {
                    System.out.println("\n--- Citas Asignadas para " + user + " ---");
                    citasStub.getCitasMedico(user).forEach(cita ->
                            System.out.println("ID: " + cita.getId() +
                                    ", Paciente: " + cita.getPacienteId() +
                                    ", Fecha: " + cita.getFecha() +
                                    ", Hora: " + cita.getHora() +
                                    ", Motivo: " + cita.getMotivo() +
                                    ", Estado: " + cita.getEstado())
                    );
                } else if (opc == 2) {
                    System.out.print("ID de cita a modificar: ");
                    String idInput = scanner.nextLine();
                    int id;
                    try {
                        id = Integer.parseInt(idInput);
                    } catch (NumberFormatException e) {
                        System.out.println("ID de cita inválido.");
                        continue;
                    }

                    System.out.print("Nuevo estado (Pendiente/Atendida/Cancelada): ");
                    String estado = scanner.nextLine();
                    if (!estado.equals("Pendiente") && !estado.equals("Atendida") && !estado.equals("Cancelada")) {
                        System.out.println("Estado inválido. Debe ser Pendiente, Atendida o Cancelada.");
                        continue;
                    }
                    citasStub.modificarEstadoCita(id, estado);
                    System.out.println("Estado de la cita actualizado.");
                } else {
                    System.out.println("Opción no válida.");
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error en el cliente Médico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}