// src/main/java/com/mycompany/rmi/CitasImpl.java

package com.mycompany.rmi;

import com.mycompany.model.Cita;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CitasImpl extends UnicastRemoteObject implements Citas {

    private List<Cita> citas;
    private List<Usuario> usuariosPacientes;
    private List<Usuario> usuariosMedicos;

    public CitasImpl() throws RemoteException {
        super();
        inicializarDatos();
    }

    private void inicializarDatos() {
        citas = new ArrayList<>();
        usuariosPacientes = new ArrayList<>();
        usuariosMedicos = new ArrayList<>();

        // Usuarios de prueba
        usuariosPacientes.add(new Usuario("paciente1", "1234"));
        usuariosMedicos.add(new Usuario("medico1", "abcd"));

        // Citas de prueba
        citas.add(new Cita(1, "paciente1", "medico1", "2025-06-01", "10:00"));
    }

    @Override
    public boolean loginPaciente(String usuario, String contrasena) throws RemoteException {
        return usuariosPacientes.stream().anyMatch(u -> u.usuario.equals(usuario) && u.contrasena.equals(contrasena));
    }

    @Override
    public boolean loginMedico(String usuario, String contrasena) throws RemoteException {
        return usuariosMedicos.stream().anyMatch(u -> u.usuario.equals(usuario) && u.contrasena.equals(contrasena));
    }

    @Override
    public void agendarCita(Cita cita) throws RemoteException {
        citas.add(cita);
    }

    @Override
    public List<Cita> getCitasPaciente(String pacienteId) throws RemoteException {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getPacienteId().equals(pacienteId)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public void cancelarCita(int citaId) throws RemoteException {
        citas.removeIf(c -> c.getId() == citaId);
    }

    @Override
    public List<Cita> getCitasMedico(String medicoId) throws RemoteException {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getMedicoId().equals(medicoId)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    @Override
    public void modificarEstadoCita(int citaId, String nuevoEstado) throws RemoteException {
        for (Cita c : citas) {
            if (c.getId() == citaId) {
                c.setEstado(nuevoEstado);
                break;
            }
        }
    }

    static class Usuario {
        String usuario;
        String contrasena;

        Usuario(String usuario, String contrasena) {
            this.usuario = usuario;
            this.contrasena = contrasena;
        }
    }
}