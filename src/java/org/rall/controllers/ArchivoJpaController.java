/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.controllers;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import org.rall.controllers.exceptions.NonexistentEntityException;
import org.rall.controllers.exceptions.PreexistingEntityException;
import org.rall.controllers.exceptions.RollbackFailureException;
import org.rall.jpa.Archivo;
import org.rall.jpa.ArchivoPK;
import org.rall.jpa.Carpeta;

/**
 *
 * @author essalud
 */
public class ArchivoJpaController implements Serializable {

    public ArchivoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @Resource
    private UserTransaction utx = null;
    @PersistenceUnit(unitName = "RallDrivePU") 
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        if (emf == null) { 
            emf = Persistence.createEntityManagerFactory("RallDrivePU"); 
        }
        return emf.createEntityManager();
    }

    public void create(Archivo archivo) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (archivo.getArchivoPK() == null) {
            archivo.setArchivoPK(new ArchivoPK());
        }
        archivo.getArchivoPK().setIdUsuario(archivo.getCarpeta().getCarpetaPK().getIdUsuario());
        archivo.getArchivoPK().setIdCarpeta(archivo.getCarpeta().getCarpetaPK().getIdCarpeta());
        EntityManager em = null;
        Context initCtx = new InitialContext(); 
        utx = (UserTransaction) initCtx.lookup("java:comp/UserTransaction");
        try {
             utx.begin();
            em = getEntityManager();
            Carpeta carpeta = archivo.getCarpeta();
            if (carpeta != null) {
                carpeta = em.getReference(carpeta.getClass(), carpeta.getCarpetaPK());
                archivo.setCarpeta(carpeta);
            }
            em.persist(archivo);
            if (carpeta != null) {
                carpeta.getArchivoList().add(archivo);
                carpeta = em.merge(carpeta);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findArchivo(archivo.getArchivoPK()) != null) {
                throw new PreexistingEntityException("Archivo " + archivo + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Archivo archivo) throws NonexistentEntityException, RollbackFailureException, Exception {
        archivo.getArchivoPK().setIdUsuario(archivo.getCarpeta().getCarpetaPK().getIdUsuario());
        archivo.getArchivoPK().setIdCarpeta(archivo.getCarpeta().getCarpetaPK().getIdCarpeta());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Archivo persistentArchivo = em.find(Archivo.class, archivo.getArchivoPK());
            Carpeta carpetaOld = persistentArchivo.getCarpeta();
            Carpeta carpetaNew = archivo.getCarpeta();
            if (carpetaNew != null) {
                carpetaNew = em.getReference(carpetaNew.getClass(), carpetaNew.getCarpetaPK());
                archivo.setCarpeta(carpetaNew);
            }
            archivo = em.merge(archivo);
            if (carpetaOld != null && !carpetaOld.equals(carpetaNew)) {
                carpetaOld.getArchivoList().remove(archivo);
                carpetaOld = em.merge(carpetaOld);
            }
            if (carpetaNew != null && !carpetaNew.equals(carpetaOld)) {
                carpetaNew.getArchivoList().add(archivo);
                carpetaNew = em.merge(carpetaNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ArchivoPK id = archivo.getArchivoPK();
                if (findArchivo(id) == null) {
                    throw new NonexistentEntityException("The archivo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ArchivoPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Archivo archivo;
            try {
                archivo = em.getReference(Archivo.class, id);
                archivo.getArchivoPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The archivo with id " + id + " no longer exists.", enfe);
            }
            Carpeta carpeta = archivo.getCarpeta();
            if (carpeta != null) {
                carpeta.getArchivoList().remove(archivo);
                carpeta = em.merge(carpeta);
            }
            em.remove(archivo);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Archivo> findArchivoEntities() {
        return findArchivoEntities(true, -1, -1);
    }

    public List<Archivo> findArchivoEntities(int maxResults, int firstResult) {
        return findArchivoEntities(false, maxResults, firstResult);
    }

    private List<Archivo> findArchivoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Archivo.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Archivo findArchivo(ArchivoPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Archivo.class, id);
        } finally {
            em.close();
        }
    }

    public int getArchivoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Archivo> rt = cq.from(Archivo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
