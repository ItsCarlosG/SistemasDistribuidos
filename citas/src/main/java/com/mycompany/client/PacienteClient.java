package com.mycompany.client;

import com.mycompany.model.Cita;
import com.mycompany.rmi.Citas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class PacienteClient {
    private static final AtomicInteger citaIdCounter = new AtomicInteger((int) (System.currentTimeMillis() % 10000)); // Para generar IDs de cita

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Citas citasStub = (Citas) registry.lookup("CitasService");

            Scanner scanner = new Scanner(System.in);

            System.out.println("--- Portal Paciente (Consola) ---");
            System.out.print("Usuario Paciente: ");
            String user = scanner.nextLine();
            System.out.print("Contraseña: ");
            String pass = scanner.nextLine();

            if (!citasStub.loginPaciente(user, pass)) {
                System.out.println("Login fallido. Usuario o contraseña incorrectos.");
                return;
            }
            System.out.println("Login exitoso como Paciente: " + user);

            while (true) {
                System.out.println("\nOpciones para Paciente:");
                System.out.println("1. Ver Mis Citas");
                System.out.println("2. Agendar Nueva Cita");
                System.out.println("3. Cancelar Cita");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opción: ");
                String input = scanner.nextLine();
                int opc;
                try {
                    opc = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Opción inválida. Intente de nuevo.");
                    continue;
                }

                if (opc == 4) {
                    System.out.println("Saliendo del portal paciente...");
                    break;
                } else if (opc == 1) {
                    System.out.println("\n--- Mis Citas (" + user + ") ---");
                    citasStub.getCitasPaciente(user).forEach(cita ->
                            System.out.println("ID: " + cita.getId() +
                                    ", Médico: " + cita.getMedicoId() +
                                    ", Fecha: " + cita.getFecha() +
                                    ", Hora: " + cita.getHora() +
                                    ", Motivo: " + cita.getMotivo() +
                                    ", Especialidad: " + cita.getEspecialidadRequerida() +
                                    ", Estado: " + cita.getEstado())
                    );
                } else if (opc == 2) {
                    System.out.println("\n--- Agendar Nueva Cita ---");

                    List<String> especialidades = citasStub.getEspecialidadesDisponibles();
                    if (especialidades.isEmpty()) {
                        System.out.println("No hay especialidades disponibles en este momento.");
                        continue;
                    }
                    System.out.println("Especialidades disponibles:");
                    for (int i = 0; i < especialidades.size(); i++) {
                        System.out.println((i + 1) + ". " + especialidades.get(i));
                    }
                    System.out.print("Seleccione una especialidad (número): ");
                    int espIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (espIndex < 0 || espIndex >= especialidades.size()) {
                        System.out.println("Selección de especialidad inválida.");
                        continue;
                    }
                    String especialidadSeleccionada = especialidades.get(espIndex);

                    List<String> medicos = citasStub.getMedicosDisponiblesPorEspecialidad(especialidadSeleccionada);
                    if (medicos.isEmpty()) {
                        System.out.println("No hay médicos disponibles para la especialidad: " + especialidadSeleccionada);
                        continue;
                    }
                    System.out.println("Médicos disponibles para " + especialidadSeleccionada + ":");
                    for (int i = 0; i < medicos.size(); i++) {
                        System.out.println((i + 1) + ". " + medicos.get(i));
                    }
                    System.out.print("Seleccione un médico (número): ");
                    int medIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (medIndex < 0 || medIndex >= medicos.size()) {
                        System.out.println("Selección de médico inválida.");
                        continue;
                    }
                    String medicoSeleccionado = medicos.get(medIndex); // Este es el ID del médico

                    System.out.print("Fecha (YYYY-MM-DD): ");
                    String fecha = scanner.nextLine();
                    System.out.print("Hora (HH:mm): ");
                    String hora = scanner.nextLine();
                    System.out.print("Motivo de la consulta: ");
                    String motivo = scanner.nextLine();

                    if (!citasStub.verificarDisponibilidadMedico(medicoSeleccionado, fecha, hora)) {
                        System.out.println("El médico " + medicoSeleccionado + " no está disponible en la fecha y hora seleccionadas. Intente otro horario.");
                        continue;
                    }

                    int idNuevaCita = citaIdCounter.getAndIncrement();
                    Cita nuevaCita = new Cita(idNuevaCita, user, medicoSeleccionado, fecha, hora, motivo, especialidadSeleccionada);
                    citasStub.agendarCita(nuevaCita);
                    System.out.println("Cita agendada exitosamente con ID: " + idNuevaCita);

                } else if (opc == 3) {
                    System.out.print("ID de cita a cancelar: ");
                    String idInput = scanner.nextLine();
                    int id;
                    try {
                        id = Integer.parseInt(idInput);
                    } catch (NumberFormatException e) {
                        System.out.println("ID de cita inválido.");
                        continue;
                    }
                    citasStub.cancelarCita(id, user); // Se añade user para verificar que el paciente es dueño de la cita
                    System.out.println("Cita cancelada (si existía y le pertenecía).");
                } else {
                    System.out.println("Opción no válida.");
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error en el cliente Paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }
}