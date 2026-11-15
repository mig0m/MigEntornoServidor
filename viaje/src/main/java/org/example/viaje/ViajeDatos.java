package org.example.viaje;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViajeDatos implements Serializable {
    private String nombre;
    private String edad;
    private String tipo;
    private List<String> destinos = new ArrayList<>();
    private String presupuesto;
    private List<String> actividades = new ArrayList<>();
    private String comentario;

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getEdad() {
        return edad;
    }
    public void setEdad(String edad) {
        this.edad = edad;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public List<String> getDestinos() {
        return destinos;
    }
    public void setDestinos(List<String> destinos) {
        this.destinos = destinos == null ? new ArrayList<>() : new ArrayList<>(destinos);
    }
    public String getPresupuesto() {
        return presupuesto;
    }
    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }
    public List<String> getActividades() {
        return actividades;
    }
    public void setActividades(List<String> actividades) {
        this.actividades = actividades == null ? new ArrayList<>() : new ArrayList<>(actividades);
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}