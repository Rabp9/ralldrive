/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.beans;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.rall.classes.BaseDatos;

/**
 *
 * @author essalud
 */
@ManagedBean
@RequestScoped
public class DbfBean {

    /**
     * Creates a new instance of DbfBean
     */
    private BaseDatos bd;
    private String archivoDBF;
    private String nombreArchivo;
    /**
     * Creates a new instance of DbfController
     */
    public DbfBean() throws IOException {
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        archivoDBF = ((LoginBean) session.getAttribute("loginBean")).getArchivoMostrar();
        nombreArchivo = ((LoginBean) session.getAttribute("loginBean")).getNombre();
        bd = new BaseDatos(archivoDBF);
    }

    public BaseDatos getBd() {
        return bd;
    }

    public void setBd(BaseDatos bd) {
        this.bd = bd;
    }

    public String getArchivoDBF() {
        return archivoDBF;
    }

    public void setArchivoDBF(String archivoDBF) {
        this.archivoDBF = archivoDBF;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
