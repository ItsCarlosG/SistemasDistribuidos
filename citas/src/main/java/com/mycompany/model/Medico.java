package com.mycompany.model;

import java.io.Serializable;

public class Medico extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String especialidad;
    private String nombreCompleto;

    public Medico(int id, String usuario, String contrasena, String nombreCompleto, String especialidad) {
        super(id, usuario, contrasena, "Medico");
        this.nombreCompleto = nombreCompleto;
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    @Override
    public String toString() {
        return "Medico [id=" + getId() + ", usuario=" + getUsuario() + ", nombre=" + nombreCompleto + ", especialidad=" + especialidad + "]";
    }
}