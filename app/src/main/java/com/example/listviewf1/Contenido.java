package com.example.listviewf1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Contenido {
    private int id;
    private String imagen;
    private String titulo;
    private String contenido;
    public Contenido(int id,String  imagen, String titulo, String contenido) {
        this.id = id;
        this.imagen = imagen;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public int getId() {
        return id;
    }
    public String getImagen() {
        return imagen;
    }
    public String getTitulo() {
        return titulo;
    }
    public String getContenido() {
        return contenido;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }




}
