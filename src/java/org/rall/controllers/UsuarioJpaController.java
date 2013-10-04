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
import org.rall.jpa.Carpeta;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.rall.classes.Recurso;
import org.rall.controllers.exceptions.IllegalOrphanException;
import org.rall.controllers.exceptions.NonexistentEntityException;
import org.rall.controllers.exceptions.RollbackFailureException;
import org.rall.jpa.Usuario;

/**
 *
 * @author essalud
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
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

    public void create(Usuario usuario) throws RollbackFailureException, Exception {
        if (usuario.getCarpetaList() == null) {
            usuario.setCarpetaList(new ArrayList<Carpeta>());
        }
        EntityManager em = null;
        Context initCtx = new InitialContext(); 
        utx = (UserTransaction) initCtx.lookup("java:comp/UserTransaction");
        try {
            utx.begin();
            em = getEntityManager();
            List<Carpeta> attachedCarpetaList = new ArrayList<Carpeta>();
            for (Carpeta carpetaListCarpetaToAttach : usuario.getCarpetaList()) {
                carpetaListCarpetaToAttach = em.getReference(carpetaListCarpetaToAttach.getClass(), carpetaListCarpetaToAttach.getCarpetaPK());
                attachedCarpetaList.add(carpetaListCarpetaToAttach);
            }
            usuario.setCarpetaList(attachedCarpetaList);
            em.persist(usuario);
            for (Carpeta carpetaListCarpeta : usuario.getCarpetaList()) {
                Usuario oldUsuarioOfCarpetaListCarpeta = carpetaListCarpeta.getUsuario();
                carpetaListCarpeta.setUsuario(usuario);
                carpetaListCarpeta = em.merge(carpetaListCarpeta);
                if (oldUsuarioOfCarpetaListCarpeta != null) {
                    oldUsuarioOfCarpetaListCarpeta.getCarpetaList().remove(carpetaListCarpeta);
                    oldUsuarioOfCarpetaListCarpeta = em.merge(oldUsuarioOfCarpetaListCarpeta);
                }
            }
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

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            List<Carpeta> carpetaListOld = persistentUsuario.getCarpetaList();
            List<Carpeta> carpetaListNew = usuario.getCarpetaList();
            List<String> illegalOrphanMessages = null;
            for (Carpeta carpetaListOldCarpeta : carpetaListOld) {
                if (!carpetaListNew.contains(carpetaListOldCarpeta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Carpeta " + carpetaListOldCarpeta + " since its usuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Carpeta> attachedCarpetaListNew = new ArrayList<Carpeta>();
            for (Carpeta carpetaListNewCarpetaToAttach : carpetaListNew) {
                carpetaListNewCarpetaToAttach = em.getReference(carpetaListNewCarpetaToAttach.getClass(), carpetaListNewCarpetaToAttach.getCarpetaPK());
                attachedCarpetaListNew.add(carpetaListNewCarpetaToAttach);
            }
            carpetaListNew = attachedCarpetaListNew;
            usuario.setCarpetaList(carpetaListNew);
            usuario = em.merge(usuario);
            for (Carpeta carpetaListNewCarpeta : carpetaListNew) {
                if (!carpetaListOld.contains(carpetaListNewCarpeta)) {
                    Usuario oldUsuarioOfCarpetaListNewCarpeta = carpetaListNewCarpeta.getUsuario();
                    carpetaListNewCarpeta.setUsuario(usuario);
                    carpetaListNewCarpeta = em.merge(carpetaListNewCarpeta);
                    if (oldUsuarioOfCarpetaListNewCarpeta != null && !oldUsuarioOfCarpetaListNewCarpeta.equals(usuario)) {
                        oldUsuarioOfCarpetaListNewCarpeta.getCarpetaList().remove(carpetaListNewCarpeta);
                        oldUsuarioOfCarpetaListNewCarpeta = em.merge(oldUsuarioOfCarpetaListNewCarpeta);
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
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Carpeta> carpetaListOrphanCheck = usuario.getCarpetaList();
            for (Carpeta carpetaListOrphanCheckCarpeta : carpetaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Carpeta " + carpetaListOrphanCheckCarpeta + " in its carpetaList field has a non-nullable usuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
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

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Usuario> findUsuarioByUsuarioYClave(String usuario, String clave) {
        EntityManager em = getEntityManager();
        return em.createNamedQuery("Usuario.findByUsuarioYClave").setParameter("usuario", usuario).setParameter("clave", clave).getResultList();
    }
    
    public boolean isDniExists(String dni) {
        EntityManager em = getEntityManager();
        return !em.createNamedQuery("Usuario.findByDni").setParameter("dni", dni).getResultList().isEmpty();
    }    
    
    public boolean isUsuarioExists(String usuario) {
        EntityManager em = getEntityManager();
        return !em.createNamedQuery("Usuario.findByUsuario").setParameter("usuario", usuario).getResultList().isEmpty();
    }    
    
    public List<Recurso> findRecursos(int carpeta) {
        EntityManager em = getEntityManager();
        List<Object[]> objs = em.createNativeQuery("call SP_LISTAR(?)").setParameter(1, carpeta).getResultList();
        List<Recurso> recursos = new ArrayList<Recurso>();
        for (Object[] obj : objs)
        {
            Recurso recurso = new Recurso();
            recurso.setId(Integer.parseInt(String.valueOf(obj[0])));
            recurso.setNombre(String.valueOf(obj[1]));
            recurso.setFecha(new Date(String.valueOf(obj[2]).replace('-', '/')));
            recurso.setTipo(String.valueOf(obj[3]));
            recursos.add(recurso);
        }
        return recursos;
    }
    
    public List<Recurso> findRoot(int usuario) {
        EntityManager em = getEntityManager();
        List<Object[]> objs = em.createNativeQuery("call SP_LISTAR_ROOT(?)").setParameter(1, usuario).getResultList();
        List<Recurso> recursos = new ArrayList<Recurso>();
        for (Object[] obj : objs)
        {
            Recurso recurso = new Recurso();
            recurso.setId(Integer.parseInt(String.valueOf(obj[0])));
            recurso.setNombre(String.valueOf(obj[1]));
            recurso.setFecha(new Date(String.valueOf(obj[2]).replace('-', '/')));
            recurso.setTipo(String.valueOf(obj[3]));
            recursos.add(recurso);
        }
        return recursos;
    }  
    
    public List<SelectItem> findFechasPosibles(int usuario) {
        EntityManager em = getEntityManager();
        List<Object[]> objs = em.createNativeQuery("call SP_FECHASPOSIBLES(?)").setParameter(1, usuario).getResultList();
        List<SelectItem> fechasPosibles = new ArrayList<SelectItem>();
        for (Object[] obj : objs)
        {
            String fecha = String.valueOf(obj[0]);
            fechasPosibles.add(new SelectItem(fecha));
        }
        return fechasPosibles;
    }
}
