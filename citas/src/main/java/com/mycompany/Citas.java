package com.mycompany;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface Citas extends Remote {
    
    public boolean agendarCita(String idPaciente, String idMedico, LocalDate fecha, LocalTime hora, String motivoConsulta) throws RemoteException;
    public List<String> listarCita() throws RemoteException;

}