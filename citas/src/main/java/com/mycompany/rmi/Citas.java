package com.mycompany.rmi;

import com.mycompany.model.Cita;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Citas extends Remote {
    boolean loginPaciente(String usuario, String contrasena) throws RemoteException;
    boolean loginMedico(String usuario, String contrasena) throws RemoteException;

    void agendarCita(Cita cita) throws RemoteException;
    List<Cita> getCitasPaciente(String pacienteId) throws RemoteException;
    void cancelarCita(int citaId) throws RemoteException;

    List<Cita> getCitasMedico(String medicoId) throws RemoteException;
    void modificarEstadoCita(int citaId, String nuevoEstado) throws RemoteException;
}