/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.rall.controllers.UsuarioJpaController;
import org.rall.controllers.exceptions.RollbackFailureException;
import org.rall.jpa.Usuario;
/**
 *
 * @author essalud
 */
@ManagedBean
@ViewScoped
public class RegistrarBean implements Serializable{

    private String usuario;
    private String clave;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private Date fechaNac;
    private String dni;
    private String correoPersonal;
    private String correoInstitucional;
    private UsuarioJpaController ujc;
    
    public RegistrarBean() {
        ujc = new UsuarioJpaController(null, null);
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }
    
    public Date getFechaNac() {
        return fechaNac;
    }

    public void setFechaNac(Date fechaNac) {
        this.fechaNac = fechaNac;
    }
    
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreoPersonal() {
        return correoPersonal;
    }

    public void setCorreoPersonal(String correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
    }
        
    public UsuarioJpaController getUjc() {
        return ujc;
    }

    public void setUjc(UsuarioJpaController ujc) {
        this.ujc = ujc;
    }
    
    public String getFechaNacFormteado() {
        String fechaFormateado = "";
        if (fechaNac != null)
            fechaFormateado = new SimpleDateFormat("dd/MM/YY").format(fechaNac);
        return fechaFormateado;
    }

    public String registrar() {
        try {
            Usuario u = new Usuario(null, usuario, clave);
            u.setNombres(nombres);
            u.setApellidoPaterno(apellidoPaterno);
            u.setApellidoMaterno(apellidoMaterno);
            u.setFechaNac(fechaNac);
            u.setDni(dni);
            u.setCorreoPersonal(correoPersonal);
            u.setCorreoInstitucional(getCorreoInstitucional());
            ujc.create(u);
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Datos salvados correctamente", null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            return "Login";
        } catch (RollbackFailureException ex) {
            Logger.getLogger(RegistrarBean.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            return "";
        } catch (Exception ex) {
            Logger.getLogger(RegistrarBean.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
