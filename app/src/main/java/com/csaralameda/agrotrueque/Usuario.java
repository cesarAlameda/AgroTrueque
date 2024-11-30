package com.csaralameda.agrotrueque;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Usuario implements Parcelable, Serializable {

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



    //ALL LO RELACIONADO CON LO PARCELABLE VA AQUÍ
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(idUsuario);
        parcel.writeParcelable(fotoUsuario, i);
        parcel.writeString(nombreUsuario);
        parcel.writeString(correoUsuario);
        parcel.writeInt(nIntercambios);
        parcel.writeInt(nAnuncios);
        if (valoracion == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(valoracion);
        }
        parcel.writeString(password);
    }


    protected Usuario(Parcel in) {
        idUsuario = in.readInt();
        fotoUsuario = in.readParcelable(Bitmap.class.getClassLoader());
        nombreUsuario = in.readString();
        correoUsuario = in.readString();
        nIntercambios = in.readInt();
        nAnuncios = in.readInt();
        if (in.readByte() == 0) {
            valoracion = null;
        } else {
            valoracion = in.readFloat();
        }
        password = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };
}
