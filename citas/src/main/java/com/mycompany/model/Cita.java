package com.mycompany.model;

import java.io.Serializable;

public class Cita implements Serializable {
    private static final long serialVersionUID = 1L; // Buena práctica para Serializable
    private int id;
    private String pacienteId; // Username del paciente
    private String medicoId;   // Username del médico
    private String fecha;      // Formato YYYY-MM-DD
    private String hora;       // Formato HH:mm
    private String motivo;
    private String especialidadRequerida;
    private String estado;     // Pendiente, Atendida, Cancelada

    public Cita(int id, String pacienteId, String medicoId, String fecha, String hora, String motivo, String especialidadRequerida) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.especialidadRequerida = especialidadRequerida;
        this.estado = "Pendiente"; // Estado por defecto
    }

    // Getters
    public int getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getMedicoId() { return medicoId; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getMotivo() { return motivo; }
    public String getEspecialidadRequerida() { return especialidadRequerida; }
    public String getEstado() { return estado; }

    public void setId(int id) { this.id = id; } // Usado por el servidor al crear la cita
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Cita [ID=" + id + ", Paciente=" + pacienteId + ", Médico=" + medicoId +
                ", Fecha=" + fecha + ", Hora=" + hora + ", Motivo='" + motivo + '\'' +
                ", Especialidad='" + especialidadRequerida + '\'' + ", Estado=" + estado + "]";
    }
}