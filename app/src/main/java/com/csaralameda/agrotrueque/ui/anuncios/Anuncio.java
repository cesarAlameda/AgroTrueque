package com.csaralameda.agrotrueque.ui.anuncios;

public class Anuncio {

    private int idAnuncio;
    private String descripcion;
    private String localizacion;
    private String hora;
    private String estado;
    private String fotoAnuncio;
    private int idUsuario;

    public Anuncio() {
    }

    public Anuncio(int idAnuncio, String descripcion, String localizacion, String hora, String estado, String fotoAnuncio, int idUsuario) {
        this.idAnuncio = idAnuncio;
        this.descripcion = descripcion;
        this.localizacion = localizacion;
        this.hora = hora;
        this.estado = estado;
        this.fotoAnuncio = fotoAnuncio;
        this.idUsuario = idUsuario;
    }

    public int getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(int idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFotoAnuncio() {
        return fotoAnuncio;
    }

    public void setFotoAnuncio(String fotoAnuncio) {
        this.fotoAnuncio = fotoAnuncio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

}
