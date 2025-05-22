package com.mycompany;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class rmi extends UnicastRemoteObject implements Citas{
    
    private List<String> citas;

    public rmi() throws RemoteException{
        citas = new ArrayList<>();
    }
    
    @Override
    public boolean agendarCita(String idPaciente, String idMedico, LocalDate fecha, LocalTime hora, String motivoConsulta) throws RemoteException{
        
        //Validar que la fecha no sea pasada
        if (fecha.isBefore(LocalDate.now())) {
            System.out.println("No se puede agendar en una fecha pasada.");
            return false;
        }

        // Validar disponibilidad del médico en esa fecha y hora
        for (String citaTexto : citas) {
            if (citaTexto.contains("Médico: " + idMedico) &&
                citaTexto.contains("Fecha: " + fecha.toString()) &&
                citaTexto.contains("Hora: " + hora.toString())){
                System.out.println("El médico ya tiene una cita agendada en esa fecha y hora.");
                return false;  // Ya existe cita para ese médico en esa hora
            }
        }

        // Crear texto de la cita (para guardar en la lista)
        String citaTexto = "Paciente: " + idPaciente +
                           ", Médico: " + idMedico +
                           ", Fecha: " + fecha.toString() +
                           ", Hora: " + hora.toString() +
                           ", Motivo: " + motivoConsulta;

        // Agregar a la lista de citas
        citas.add(citaTexto);

        System.out.println("Cita agendada correctamente.");
        return true;

    }

    @Override
    public List<String> listarCita() throws RemoteException {
        
        return new ArrayList<>(citas);

    }

}