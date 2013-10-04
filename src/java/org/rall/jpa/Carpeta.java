/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.jpa;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Roberto
 */
@Entity
@Table(name = "Carpeta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Carpeta.findAll", query = "SELECT c FROM Carpeta c"),
    @NamedQuery(name = "Carpeta.findByIdCarpeta", query = "SELECT c FROM Carpeta c WHERE c.carpetaPK.idCarpeta = :idCarpeta"),
    @NamedQuery(name = "Carpeta.findByIdUsuario", query = "SELECT c FROM Carpeta c WHERE c.carpetaPK.idUsuario = :idUsuario"),
    @NamedQuery(name = "Carpeta.findByNombre", query = "SELECT c FROM Carpeta c WHERE c.nombre = :nombre"),
    @NamedQuery(name = "Carpeta.findByFecha", query = "SELECT c FROM Carpeta c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Carpeta.findByCarpetaSuper", query = "SELECT c FROM Carpeta c WHERE c.carpetaSuper = :carpetaSuper"),
    @NamedQuery(name = "Carpeta.findRoot", query = "SELECT c FROM Carpeta c WHERE c.usuario = :usuario AND c.carpetaSuper is NULL")})
public class Carpeta implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CarpetaPK carpetaPK;
    @Size(max = 100)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Column(name = "carpetaSuper")
    private Integer carpetaSuper;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "carpeta")
    private List<Archivo> archivoList;
    @JoinColumn(name = "idUsuario", referencedColumnName = "idUsuario", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuario usuario;

    public Carpeta() {
    }

    public Carpeta(CarpetaPK carpetaPK) {
        this.carpetaPK = carpetaPK;
    }

    public Carpeta(int idCarpeta, int idUsuario) {
        this.carpetaPK = new CarpetaPK(idCarpeta, idUsuario);
    }

    public CarpetaPK getCarpetaPK() {
        return carpetaPK;
    }

    public void setCarpetaPK(CarpetaPK carpetaPK) {
        this.carpetaPK = carpetaPK;
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

    public Integer getCarpetaSuper() {
        return carpetaSuper;
    }

    public void setCarpetaSuper(Integer carpetaSuper) {
        this.carpetaSuper = carpetaSuper;
    }

    @XmlTransient
    public List<Archivo> getArchivoList() {
        return archivoList;
    }

    public void setArchivoList(List<Archivo> archivoList) {
        this.archivoList = archivoList;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carpetaPK != null ? carpetaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Carpeta)) {
            return false;
        }
        Carpeta other = (Carpeta) object;
        if ((this.carpetaPK == null && other.carpetaPK != null) || (this.carpetaPK != null && !this.carpetaPK.equals(other.carpetaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rall.jpa.Carpeta[ carpetaPK=" + carpetaPK + " ]";
    }
    
}
