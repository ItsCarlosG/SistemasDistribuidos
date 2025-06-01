package com.mycompany.rmi;

import com.mycompany.model.Cita;
import com.mycompany.model.Medico;
import com.mycompany.model.Usuario;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CitasImpl extends UnicastRemoteObject implements Citas {
    private static final long serialVersionUID = 1L;

    private final Map<String, Usuario> pacientes = new ConcurrentHashMap<>();
    private final Map<String, Medico> medicos = new ConcurrentHashMap<>();
    private final List<Cita> citas = new CopyOnWriteArrayList<>();

    private final AtomicInteger citaIdCounter = new AtomicInteger(1);
    private final AtomicInteger usuarioIdCounter = new AtomicInteger(1);

    public CitasImpl() throws RemoteException {
        super();
        inicializarDatosDePrueba();
    }

    private void inicializarDatosDePrueba() {
        int idPac1 = usuarioIdCounter.getAndIncrement();
        int idMed1 = usuarioIdCounter.getAndIncrement();
        int idMed2 = usuarioIdCounter.getAndIncrement();
        int idMed3 = usuarioIdCounter.getAndIncrement();

        pacientes.put("paciente1", new Usuario(idPac1, "paciente1", "1234", "Ana Pérez", "Paciente"));
        pacientes.put("user", new Usuario(usuarioIdCounter.getAndIncrement(), "user", "pass", "Carlos Lopez", "Paciente"));


        Medico medico1 = new Medico(idMed1, "medico1", "1234", "Dr. Carlos Sánchez", "Cardiología");
        Medico medico2 = new Medico(idMed2, "medico2", "efgh", "Dra. Laura Gómez", "Pediatría");
        Medico medico3 = new Medico(idMed3, "medico3", "ijkl", "Dr. Juan Rodríguez", "Cardiología");
        medicos.put("medico1", medico1);
        medicos.put("medico2", medico2);
        medicos.put("medico3", medico3);

        Cita cita1 = new Cita(citaIdCounter.getAndIncrement(), "paciente1", "medico1", "2024-08-01", "10:00", "Chequeo general", "Cardiología");
        Cita cita2 = new Cita(citaIdCounter.getAndIncrement(), "paciente1", "medico2", "2024-08-05", "11:30", "Vacunación", "Pediatría");
        cita2.setEstado("Atendida");
        citas.add(cita1);
        citas.add(cita2);

        System.out.println("Datos de prueba inicializados:");
        System.out.println("Pacientes: " + pacientes.keySet());
        System.out.println("Médicos: " + medicos.values().stream().map(m -> m.getUsuario() + " (" + m.getEspecialidad() + ")").collect(Collectors.toList()));
        System.out.println("Citas: " + citas.size());
    }

    @Override
    public boolean loginPaciente(String usuario, String contrasena) throws RemoteException {
        Usuario u = pacientes.get(usuario);
        return u != null && u.verificarContrasena(contrasena);
    }

    @Override
    public boolean loginMedico(String usuario, String contrasena) throws RemoteException {
        Medico m = medicos.get(usuario);
        return m != null && m.verificarContrasena(contrasena);
    }

    @Override
    public boolean registrarPaciente(String usuario, String contrasena, String nombreCompleto) throws RemoteException {
        if (pacientes.containsKey(usuario) || medicos.containsKey(usuario)) {
            System.out.println("Intento de registro fallido: Usuario '" + usuario + "' ya existe.");
            return false; // Usuario ya existe
        }
        int newId = usuarioIdCounter.getAndIncrement();
        Usuario nuevoPaciente = new Usuario(newId, usuario, contrasena, nombreCompleto, "Paciente");
        pacientes.put(usuario, nuevoPaciente);
        System.out.println("Paciente registrado: " + usuario);
        return true;
    }

    @Override
    public boolean registrarMedico(String usuario, String contrasena, String nombreCompleto, String especialidad) throws RemoteException {
        if (medicos.containsKey(usuario) || pacientes.containsKey(usuario)) {
            System.out.println("Intento de registro fallido: Médico '" + usuario + "' ya existe.");
            return false;
        }
        int newId = usuarioIdCounter.getAndIncrement();
        Medico nuevoMedico = new Medico(newId, usuario, contrasena, nombreCompleto, especialidad);
        medicos.put(usuario, nuevoMedico);
        System.out.println("Médico registrado: " + usuario + " (" + especialidad + ")");
        return true;
    }

    @Override
    public void agendarCita(Cita cita) throws RemoteException {
        if (cita.getId() == 0) {
            cita.setId(citaIdCounter.getAndIncrement());
        }
        if (!pacientes.containsKey(cita.getPacienteId()) || !medicos.containsKey(cita.getMedicoId())) {
            throw new RemoteException("Paciente o médico no válido para la cita.");
        }
        if (!verificarDisponibilidadMedico(cita.getMedicoId(), cita.getFecha(), cita.getHora())) {
            throw new RemoteException("El médico " + cita.getMedicoId() + " no está disponible en la fecha y hora seleccionadas.");
        }
        citas.add(cita);
        System.out.println("Cita agendada: ID " + cita.getId() + " para " + cita.getPacienteId() + " con " + cita.getMedicoId());
    }

    @Override
    public List<Cita> getCitasPaciente(String pacienteUser) throws RemoteException {
        return citas.stream()
                .filter(c -> c.getPacienteId().equals(pacienteUser))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelarCita(int citaId, String pacienteUser) throws RemoteException {
        boolean removed = citas.removeIf(c -> c.getId() == citaId && c.getPacienteId().equals(pacienteUser) && "Pendiente".equals(c.getEstado()));
        if (removed) {
            System.out.println("Cita ID " + citaId + " cancelada por paciente " + pacienteUser);
        } else {
            System.out.println("No se pudo cancelar la cita ID " + citaId + ". O no existe, no pertenece al paciente, o no está pendiente.");
        }
    }

    @Override
    public List<Cita> getCitasMedico(String medicoUser) throws RemoteException {
        return citas.stream()
                .filter(c -> c.getMedicoId().equals(medicoUser))
                .collect(Collectors.toList());
    }

    @Override
    public void modificarEstadoCita(int citaId, String nuevoEstado) throws RemoteException {
        for (Cita c : citas) {
            if (c.getId() == citaId) {
                if ("Pendiente".equals(nuevoEstado) || "Atendida".equals(nuevoEstado) || "Cancelada".equals(nuevoEstado)) {
                    c.setEstado(nuevoEstado);
                    System.out.println("Estado de cita ID " + citaId + " modificado a: " + nuevoEstado);
                    return;
                } else {
                    throw new RemoteException("Estado '" + nuevoEstado + "' no válido.");
                }
            }
        }
        throw new RemoteException("Cita ID " + citaId + " no encontrada para modificar estado.");
    }

    @Override
    public List<String> getEspecialidadesDisponibles() throws RemoteException {
        return medicos.values().stream()
                .map(Medico::getEspecialidad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMedicosDisponiblesPorEspecialidad(String especialidad) throws RemoteException {
        return medicos.values().stream()
                .filter(m -> m.getEspecialidad().equalsIgnoreCase(especialidad))
                .map(Medico::getUsuario)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarDisponibilidadMedico(String medicoUser, String fecha, String hora) throws RemoteException {

        for (Cita c : citas) {
            if (c.getMedicoId().equals(medicoUser) &&
                    c.getFecha().equals(fecha) &&
                    c.getHora().equals(hora) &&
                    !"Cancelada".equals(c.getEstado())) {
                return false;
            }
        }
        return true;
    }
}