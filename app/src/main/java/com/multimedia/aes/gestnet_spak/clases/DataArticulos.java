package com.multimedia.aes.gestnet_spak.clases;

public class DataArticulos {
    private String nombre;
    private int id;
///PREUBA GIT
    public DataArticulos(String nombre, int id) {
        this.nombre = nombre;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
