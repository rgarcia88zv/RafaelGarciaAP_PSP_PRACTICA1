package com.example.practica1;

public class Contacto {
    int id;
    String nombre;
    String numTelf;

    public Contacto(int id, String nombre, String numTelf) {
        this.id = id;
        this.nombre = nombre;
        this.numTelf = numTelf;
    }
    public Contacto(String nombre, String numTelf) {
        this.nombre = nombre;
        this.numTelf = numTelf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumTelf() {
        return numTelf;
    }

    public void setNumTelf(String numTelf) {
        this.numTelf = numTelf;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", numTelf='" + numTelf + '\'' +
                '}';
    }

}
