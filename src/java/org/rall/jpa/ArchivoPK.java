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
public class ArchivoPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "idArchivo")
    private int idArchivo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idCarpeta")
    private int idCarpeta;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idUsuario")
    private int idUsuario;

    public ArchivoPK() {
    }

    public ArchivoPK(int idArchivo, int idCarpeta, int idUsuario) {
        this.idArchivo = idArchivo;
        this.idCarpeta = idCarpeta;
        this.idUsuario = idUsuario;
    }

    public int getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(int idArchivo) {
        this.idArchivo = idArchivo;
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
        hash += (int) idArchivo;
        hash += (int) idCarpeta;
        hash += (int) idUsuario;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArchivoPK)) {
            return false;
        }
        ArchivoPK other = (ArchivoPK) object;
        if (this.idArchivo != other.idArchivo) {
            return false;
        }
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
        return "org.rall.jpa.ArchivoPK[ idArchivo=" + idArchivo + ", idCarpeta=" + idCarpeta + ", idUsuario=" + idUsuario + " ]";
    }
    
}
