package com.csaralameda.agrotrueque.ui.chat;

import android.graphics.Bitmap;

public class ChatObj {
    private int idUsuario;
    private String nombreUser;
    private Bitmap fotoUser;
    private String ultimoMensaje;
    private String ultimoMensajeHora;

    public ChatObj(){
    }


    public ChatObj(int idUsuario, String nombreUser, Bitmap fotoUser, String ultimoMensaje, String ultimoMensajeHora) {
        this.idUsuario = idUsuario;
        this.nombreUser = nombreUser;
        this.fotoUser = fotoUser;
        this.ultimoMensaje = ultimoMensaje;
        this.ultimoMensajeHora = ultimoMensajeHora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUser() {
        return nombreUser;
    }

    public void setNombreUser(String nombreUser) {
        this.nombreUser = nombreUser;
    }

    public Bitmap getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(Bitmap fotoUser) {
        this.fotoUser = fotoUser;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public String getUltimoMensajeHora() {
        return ultimoMensajeHora;
    }

    public void setUltimoMensajeHora(String ultimoMensajeHora) {
        this.ultimoMensajeHora = ultimoMensajeHora;
    }
}
