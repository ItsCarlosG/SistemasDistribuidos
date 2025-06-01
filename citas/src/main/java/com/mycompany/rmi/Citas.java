package com.mycompany.rmi;

import com.mycompany.model.Cita;
// import com.mycompany.model.Medico; // Si se quiere devolver objetos Medico

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Citas extends Remote {
    // Autenticación
    boolean loginPaciente(String usuario, String contrasena) throws RemoteException;
    boolean loginMedico(String usuario, String contrasena) throws RemoteException;

    // Registro
    boolean registrarPaciente(String usuario, String contrasena, String nombreCompleto) throws RemoteException;
    boolean registrarMedico(String usuario, String contrasena, String nombreCompleto, String especialidad) throws RemoteException;

    // Operaciones Paciente
    void agendarCita(Cita cita) throws RemoteException;
    List<Cita> getCitasPaciente(String pacienteUser) throws RemoteException;
    void cancelarCita(int citaId, String pacienteUser) throws RemoteException;

    List<Cita> getCitasMedico(String medicoUser) throws RemoteException;
    void modificarEstadoCita(int citaId, String nuevoEstado) throws RemoteException;

    List<String> getEspecialidadesDisponibles() throws RemoteException;
    List<String> getMedicosDisponiblesPorEspecialidad(String especialidad) throws RemoteException;
    boolean verificarDisponibilidadMedico(String medicoUser, String fecha, String hora) throws RemoteException;
}