/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.jpa;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Roberto
 */
@Entity
@Table(name = "Archivo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Archivo.findAll", query = "SELECT a FROM Archivo a"),
    @NamedQuery(name = "Archivo.findByIdArchivo", query = "SELECT a FROM Archivo a WHERE a.archivoPK.idArchivo = :idArchivo"),
    @NamedQuery(name = "Archivo.findByIdCarpeta", query = "SELECT a FROM Archivo a WHERE a.archivoPK.idCarpeta = :idCarpeta"),
    @NamedQuery(name = "Archivo.findByIdUsuario", query = "SELECT a FROM Archivo a WHERE a.archivoPK.idUsuario = :idUsuario"),
    @NamedQuery(name = "Archivo.findByFecha", query = "SELECT a FROM Archivo a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "Archivo.findByNombreArchivo", query = "SELECT a FROM Archivo a WHERE a.nombreArchivo = :nombreArchivo"),
    @NamedQuery(name = "Archivo.findByPermiso", query = "SELECT a FROM Archivo a WHERE a.permiso = :permiso")})
public class Archivo implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ArchivoPK archivoPK;
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Size(max = 100)
    @Column(name = "nombreArchivo")
    private String nombreArchivo;
    @Column(name = "permiso")
    private Integer permiso;
    @JoinColumns({
        @JoinColumn(name = "idCarpeta", referencedColumnName = "idCarpeta", insertable = false, updatable = false),
        @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Carpeta carpeta;

    public Archivo() {
    }

    public Archivo(ArchivoPK archivoPK) {
        this.archivoPK = archivoPK;
    }

    public Archivo(int idArchivo, int idCarpeta, int idUsuario) {
        this.archivoPK = new ArchivoPK(idArchivo, idCarpeta, idUsuario);
    }

    public ArchivoPK getArchivoPK() {
        return archivoPK;
    }

    public void setArchivoPK(ArchivoPK archivoPK) {
        this.archivoPK = archivoPK;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Integer getPermiso() {
        return permiso;
    }

    public void setPermiso(Integer permiso) {
        this.permiso = permiso;
    }

    public Carpeta getCarpeta() {
        return carpeta;
    }

    public void setCarpeta(Carpeta carpeta) {
        this.carpeta = carpeta;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (archivoPK != null ? archivoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Archivo)) {
            return false;
        }
        Archivo other = (Archivo) object;
        if ((this.archivoPK == null && other.archivoPK != null) || (this.archivoPK != null && !this.archivoPK.equals(other.archivoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rall.jpa.Archivo[ archivoPK=" + archivoPK + " ]";
    }
    
}
