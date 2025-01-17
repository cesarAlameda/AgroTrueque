package com.csaralameda.agrotrueque.ui.anuncios;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;

public class Anuncio implements Serializable {

    private int idAnuncio;
    private String descripcion;
    private String localizacion;
    private String hora;
    private String estado;
    private Bitmap fotoAnuncio;
    private int idUsuario;

    public Anuncio() {
    }

    public Anuncio(int idAnuncio, String descripcion, String localizacion, String hora, String estado, Bitmap fotoAnuncio, int idUsuario) {
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

    public Bitmap getFotoAnuncio() {
        return fotoAnuncio;
    }

    public void setFotoAnuncio(Bitmap fotoAnuncio) {
        this.fotoAnuncio = fotoAnuncio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

}
