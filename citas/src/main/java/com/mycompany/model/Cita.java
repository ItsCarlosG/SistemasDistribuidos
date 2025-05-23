// src/main/java/com/mycompany/model/Cita.java

package com.mycompany.model;

import java.io.Serializable;

public class Cita implements Serializable {
    private int id;
    private String pacienteId;
    private String medicoId;
    private String fecha;
    private String hora;
    private String estado; // Pendiente, Atendida, Cancelada

    public Cita(int id, String pacienteId, String medicoId, String fecha, String hora) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "Pendiente";
    }

    // Getters y Setters
    public int getId() { return id; }
    public String getPacienteId() { return pacienteId; }
    public String getMedicoId() { return medicoId; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "ID: " + id + ", Fecha: " + fecha + ", Hora: " + hora + ", Estado: " + estado;
    }
}