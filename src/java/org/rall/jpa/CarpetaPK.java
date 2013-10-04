/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.jpa;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Roberto
 */
@Embeddable
public class CarpetaPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "idCarpeta")
    private int idCarpeta;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idUsuario")
    private int idUsuario;

    public CarpetaPK() {
    }

    public CarpetaPK(int idCarpeta, int idUsuario) {
        this.idCarpeta = idCarpeta;
        this.idUsuario = idUsuario;
    }

    public int getIdCarpeta() {
        return idCarpeta;
    }

    public void setIdCarpeta(int idCarpeta) {
        this.idCarpeta = idCarpeta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idCarpeta;
        hash += (int) idUsuario;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CarpetaPK)) {
            return false;
        }
        CarpetaPK other = (CarpetaPK) object;
        if (this.idCarpeta != other.idCarpeta) {
            return false;
        }
        if (this.idUsuario != other.idUsuario) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rall.jpa.CarpetaPK[ idCarpeta=" + idCarpeta + ", idUsuario=" + idUsuario + " ]";
    }
    
}
