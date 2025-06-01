package com.mycompany.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String usuario;
    private String contrasena;
    private String rol;
    private String nombreCompleto;

    public Usuario(int id, String usuario, String contrasena, String nombreCompleto, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }
    protected Usuario(int id, String usuario, String contrasena, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }


    public int getId() { return id; }
    public String getUsuario() { return usuario; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public String getNombreCompleto() { return nombreCompleto; }

    public void setId(int id) { this.id = id; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto;}


    public boolean verificarContrasena(String contrasenaAComparar) {
        return this.contrasena.equals(contrasenaAComparar);
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", usuario=" + usuario + ", rol=" + rol + ", nombre=" + (nombreCompleto != null ? nombreCompleto : "N/A") + "]";
    }
}