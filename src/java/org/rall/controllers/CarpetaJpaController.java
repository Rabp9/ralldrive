/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.rall.jpa.Usuario;
import org.rall.jpa.Archivo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.rall.controllers.exceptions.IllegalOrphanException;
import org.rall.controllers.exceptions.NonexistentEntityException;
import org.rall.controllers.exceptions.PreexistingEntityException;
import org.rall.controllers.exceptions.RollbackFailureException;
import org.rall.jpa.Carpeta;
import org.rall.jpa.CarpetaPK;

/**
 *
 * @author essalud
 */
public class CarpetaJpaController implements Serializable {

    public CarpetaJpaController(UserTransaction utx, EntityManagerFactory emf) {
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

    public void create(Carpeta carpeta) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (carpeta.getCarpetaPK() == null) {
            carpeta.setCarpetaPK(new CarpetaPK());
        }
        if (carpeta.getArchivoList() == null) {
            carpeta.setArchivoList(new ArrayList<Archivo>());
        }
        carpeta.getCarpetaPK().setIdUsuario(carpeta.getUsuario().getIdUsuario());
        EntityManager em = null;
        Context initCtx = new InitialContext(); 
        utx = (UserTransaction) initCtx.lookup("java:comp/UserTransaction");
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario = carpeta.getUsuario();
            if (usuario != null) {
                usuario = em.getReference(usuario.getClass(), usuario.getIdUsuario());
                carpeta.setUsuario(usuario);
            }
            List<Archivo> attachedArchivoList = new ArrayList<Archivo>();
            for (Archivo archivoListArchivoToAttach : carpeta.getArchivoList()) {
                archivoListArchivoToAttach = em.getReference(archivoListArchivoToAttach.getClass(), archivoListArchivoToAttach.getArchivoPK());
                attachedArchivoList.add(archivoListArchivoToAttach);
            }
            carpeta.setArchivoList(attachedArchivoList);
            em.persist(carpeta);
            if (usuario != null) {
                usuario.getCarpetaList().add(carpeta);
                usuario = em.merge(usuario);
            }
            for (Archivo archivoListArchivo : carpeta.getArchivoList()) {
                Carpeta oldCarpetaOfArchivoListArchivo = archivoListArchivo.getCarpeta();
                archivoListArchivo.setCarpeta(carpeta);
                archivoListArchivo = em.merge(archivoListArchivo);
                if (oldCarpetaOfArchivoListArchivo != null) {
                    oldCarpetaOfArchivoListArchivo.getArchivoList().remove(archivoListArchivo);
                    oldCarpetaOfArchivoListArchivo = em.merge(oldCarpetaOfArchivoListArchivo);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCarpeta(carpeta.getCarpetaPK()) != null) {
                throw new PreexistingEntityException("Carpeta " + carpeta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Carpeta carpeta) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        carpeta.getCarpetaPK().setIdUsuario(carpeta.getUsuario().getIdUsuario());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Carpeta persistentCarpeta = em.find(Carpeta.class, carpeta.getCarpetaPK());
            Usuario usuarioOld = persistentCarpeta.getUsuario();
            Usuario usuarioNew = carpeta.getUsuario();
            List<Archivo> archivoListOld = persistentCarpeta.getArchivoList();
            List<Archivo> archivoListNew = carpeta.getArchivoList();
            List<String> illegalOrphanMessages = null;
            for (Archivo archivoListOldArchivo : archivoListOld) {
                if (!archivoListNew.contains(archivoListOldArchivo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Archivo " + archivoListOldArchivo + " since its carpeta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioNew != null) {
                usuarioNew = em.getReference(usuarioNew.getClass(), usuarioNew.getIdUsuario());
                carpeta.setUsuario(usuarioNew);
            }
            List<Archivo> attachedArchivoListNew = new ArrayList<Archivo>();
            for (Archivo archivoListNewArchivoToAttach : archivoListNew) {
                archivoListNewArchivoToAttach = em.getReference(archivoListNewArchivoToAttach.getClass(), archivoListNewArchivoToAttach.getArchivoPK());
                attachedArchivoListNew.add(archivoListNewArchivoToAttach);
            }
            archivoListNew = attachedArchivoListNew;
            carpeta.setArchivoList(archivoListNew);
            carpeta = em.merge(carpeta);
            if (usuarioOld != null && !usuarioOld.equals(usuarioNew)) {
                usuarioOld.getCarpetaList().remove(carpeta);
                usuarioOld = em.merge(usuarioOld);
            }
            if (usuarioNew != null && !usuarioNew.equals(usuarioOld)) {
                usuarioNew.getCarpetaList().add(carpeta);
                usuarioNew = em.merge(usuarioNew);
            }
            for (Archivo archivoListNewArchivo : archivoListNew) {
                if (!archivoListOld.contains(archivoListNewArchivo)) {
                    Carpeta oldCarpetaOfArchivoListNewArchivo = archivoListNewArchivo.getCarpeta();
                    archivoListNewArchivo.setCarpeta(carpeta);
                    archivoListNewArchivo = em.merge(archivoListNewArchivo);
                    if (oldCarpetaOfArchivoListNewArchivo != null && !oldCarpetaOfArchivoListNewArchivo.equals(carpeta)) {
                        oldCarpetaOfArchivoListNewArchivo.getArchivoList().remove(archivoListNewArchivo);
                        oldCarpetaOfArchivoListNewArchivo = em.merge(oldCarpetaOfArchivoListNewArchivo);
                    }
                }
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
                CarpetaPK id = carpeta.getCarpetaPK();
                if (findCarpeta(id) == null) {
                    throw new NonexistentEntityException("The carpeta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(CarpetaPK id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Carpeta carpeta;
            try {
                carpeta = em.getReference(Carpeta.class, id);
                carpeta.getCarpetaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The carpeta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Archivo> archivoListOrphanCheck = carpeta.getArchivoList();
            for (Archivo archivoListOrphanCheckArchivo : archivoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Carpeta (" + carpeta + ") cannot be destroyed since the Archivo " + archivoListOrphanCheckArchivo + " in its archivoList field has a non-nullable carpeta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario usuario = carpeta.getUsuario();
            if (usuario != null) {
                usuario.getCarpetaList().remove(carpeta);
                usuario = em.merge(usuario);
            }
            em.remove(carpeta);
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

    public List<Carpeta> findCarpetaEntities() {
        return findCarpetaEntities(true, -1, -1);
    }

    public List<Carpeta> findCarpetaEntities(int maxResults, int firstResult) {
        return findCarpetaEntities(false, maxResults, firstResult);
    }

    private List<Carpeta> findCarpetaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Carpeta.class));
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

    public Carpeta findCarpeta(CarpetaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Carpeta.class, id);
        } finally {
            em.close();
        }
    }

    public int getCarpetaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Carpeta> rt = cq.from(Carpeta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Carpeta> findCarpetaRoot(Usuario usuario) {
        EntityManager em = getEntityManager();
        return em.createNamedQuery("Carpeta.findRoot").setParameter("usuario", usuario).getResultList();
    }

    public List<Carpeta> findCarpetaBySuperCarpeta(Carpeta carpeta, Usuario usuario) {
        if(carpeta == null)
            return findCarpetaRoot(usuario);
        else
        {
            EntityManager em = getEntityManager();
            return em.createNamedQuery("Carpeta.findByCarpetaSuper").setParameter("carpetaSuper", carpeta.getCarpetaPK().getIdCarpeta()).getResultList();
        }
    }
    
    public Carpeta findSuperCarpeta(Carpeta carpeta) {
        EntityManager em = getEntityManager();
        if(carpeta.getCarpetaSuper() == null)   return null;
        return (Carpeta) em.createNamedQuery("Carpeta.findByIdCarpeta").setParameter("idCarpeta", carpeta.getCarpetaSuper()).getResultList().get(0);
    }
    
}
