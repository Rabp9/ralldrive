/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.classes;

import java.util.Date;

/**
 *
 * @author essalud
 */
public class Recurso {
    private int id;
    private String nombre;
    private Date fecha;
    private String tipo;

    public Recurso() {
    }

    public Recurso(int id, String nombre, Date fecha, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.tipo = tipo;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
