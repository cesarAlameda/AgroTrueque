package com.csaralameda.agrotrueque;

import android.graphics.Bitmap;

public class Usuario {

    //ATRIBUTOS DE USUARIO
    private int idUsuario;
    private Bitmap fotoUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private int nIntercambios;
    private int nAnuncios;
    private Float valoracion;
    private String password;



    //GETTER Y SETTER
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Bitmap getFotoUsuario() {return fotoUsuario;}

    public void setFotoUsuario(Bitmap fotoUsuario) {this.fotoUsuario = fotoUsuario;}

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getnIntercambios() {
        return nIntercambios;
    }

    public void setnIntercambios(int nIntercambios) {
        this.nIntercambios = nIntercambios;
    }

    public int getnAnuncios() {
        return nAnuncios;
    }

    public void setnAnuncios(int nAnuncios) {
        this.nAnuncios = nAnuncios;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Float getValoracion() {
        return valoracion;
    }

    public void setValoracion(Float valoracion) {
        this.valoracion = valoracion;
    }

    //CONSTRUCTOR CON TODOS LOS PARAMETROS
    public Usuario(int idUsuario, String password, Float valoracion, int nAnuncios, Bitmap fotoUsuario, String nombreUsuario, String correoUsuario, int nIntercambios) {
        this.idUsuario = idUsuario;
        this.password = password;
        this.valoracion = valoracion;
        this.nAnuncios = nAnuncios;
        this.fotoUsuario = fotoUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoUsuario = correoUsuario;
        this.nIntercambios = nIntercambios;
    }
    //CONSTRUCTOR VACÍO
    public Usuario(){}

}
