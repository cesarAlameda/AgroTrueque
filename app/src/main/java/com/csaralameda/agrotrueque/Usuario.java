package com.csaralameda.agrotrueque;

public class Usuario {

    //ATRIBUTOS DE USUARIO
    private int idUsuario;
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
    public Usuario(int idUsuario, String nombreUsuario, String correoUsuario, int nIntercambios, Float valoracion, String password, int nAnuncios) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoUsuario = correoUsuario;
        this.nIntercambios = nIntercambios;
        this.valoracion = valoracion;
        this.password = password;
        this.nAnuncios = nAnuncios;
    }

    //CONSTRUCTOR VACÍO
    public Usuario(){}

}
