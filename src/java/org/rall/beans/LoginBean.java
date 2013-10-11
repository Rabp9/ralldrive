/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.rall.classes.Recurso;
import org.rall.controllers.ArchivoJpaController;
import org.rall.controllers.CarpetaJpaController;
import org.rall.controllers.UsuarioJpaController;
import org.rall.controllers.exceptions.PreexistingEntityException;
import org.rall.controllers.exceptions.RollbackFailureException;
import org.rall.jpa.Archivo;
import org.rall.jpa.Carpeta;
import org.rall.jpa.Usuario;

/**
 *
 * @author essalud
 */
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable{
    private StreamedContent file;  
    private String usuario;
    private String clave;
    private Usuario u;
    private TreeNode root;
    private TreeNode selectedNode;
    private List<Recurso> recursos;
    private Recurso selectedRow;
    private String destination;
    private char delimitador;
    private UsuarioJpaController ujc;
    private CarpetaJpaController cjc;
    private ArchivoJpaController ajc;
    private String nuevaCarpeta;
    private String archivoMostrar;
    private String nombre;
    
    public LoginBean() {
        destination = "/home/essaludrall/ficheros/";
        delimitador = '/';
        ujc = new UsuarioJpaController(null, null);
        cjc = new CarpetaJpaController(null, null);
        ajc = new ArchivoJpaController(null, null);
    }
    
    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
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
            
    public Usuario getU() {
        return u;
    }

    public void setU(Usuario u) {
        this.u = u;
    }
    
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }
        
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
        
    public List<Recurso> getRecursos() {
        if(selectedNode == null && selectedRow == null) 
            recursos = ujc.findRoot(u.getIdUsuario());
        return recursos;
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos = recursos;
    }
        
    public Recurso getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(Recurso selectedRow) {
        this.selectedRow = selectedRow;
    }
        
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
        
    public char getDelimitador() {
        return delimitador;
    }

    public void setDelimitador(char delimitador) {
        this.delimitador = delimitador;
    }
    
    public UsuarioJpaController getUjc() {
        return ujc;
    }

    public void setUjc(UsuarioJpaController ujc) {
        this.ujc = ujc;
    }
    
    public CarpetaJpaController getCjc() {
        return cjc;
    }

    public void setCjc(CarpetaJpaController cjc) {
        this.cjc = cjc;
    }

    public ArchivoJpaController getAjc() {
        return ajc;
    }

    public void setAjc(ArchivoJpaController ajc) {
        this.ajc = ajc;
    }

    public String getNuevaCarpeta() {
        return nuevaCarpeta;
    }

    public void setNuevaCarpeta(String nuevaCarpeta) {
        this.nuevaCarpeta = nuevaCarpeta;
    }
   
    public String getArchivoMostrar() {
        return archivoMostrar;
    }

    public void setArchivoMostrar(String archivoMostrar) {
        this.archivoMostrar = archivoMostrar;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String ingresar() {
        List<Usuario> usuarios = getUjc().findUsuarioByUsuarioYClave(usuario, clave);
        if(!usuarios.isEmpty()) 
        {
            setU(usuarios.get(0));
            setCarpetas();
            return "Home?faces-redirect=true";
        }
        else
        {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "El usuario o clave es incorrecto", null);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            return "";
        }
    }

    private void setCarpetas() {
        setRoot(new DefaultTreeNode("root", null)); 
        crearArbol(null, getRoot(), u);
    }
    
    private String getExtension(String nombre) {
        nombre = new StringBuilder(nombre).reverse().toString();
        String ex = "";
        for(char c : nombre.toCharArray()) {
            if(c == '.')
                break;
            ex = c + ex;
        }
        return ex.trim();
    }
    
    private void crearArbol(Carpeta carpeta, TreeNode padre, Usuario usuario) {
        List<Carpeta> carpetas = getCjc().findCarpetaBySuperCarpeta(carpeta, usuario);
        for(Carpeta subCarpeta : carpetas)
        {
            TreeNode tn = new DefaultTreeNode(subCarpeta, padre);
            if(!cjc.findCarpetaBySuperCarpeta(carpeta, usuario).isEmpty()) crearArbol(subCarpeta, tn, usuario);
        }
    }
 
    private TreeNode findNode(TreeNode tn, Recurso r) {
        int id;
        try {
            id = ((Carpeta) tn.getData()).getCarpetaPK().getIdCarpeta();
        }
        catch(ClassCastException cce) {
            id = -1;
        }
        if(id == r.getId()) {
            return tn;
        }
        else {
            for(TreeNode aux : tn.getChildren()) {
                if(findNode(aux, r) != null)
                    return findNode(aux, r);
            }
        }
        return null;
    }
    
    private void actualizarRecursos(Carpeta data) {
        recursos = ujc.findRecursos(((Carpeta) data).getCarpetaPK().getIdCarpeta());
    }

    private void crearCarpetas(String ubicacion) {
        String carpeta = "";
        for(char c : ubicacion.toCharArray()) {
            carpeta = carpeta + c;
            if (c == delimitador) {
                File directorio = new File(carpeta);
                directorio.mkdir();
            }
        }
    }
        
    private String calcularUbicacion(Carpeta carpeta) {
        String calculo = "";
        do {
            calculo = carpeta.getNombre() + delimitador + calculo;
            carpeta = getCjc().findSuperCarpeta(carpeta);
        }   while(carpeta != null);       
        return calculo;
    }   
    
    public void onNodeSelect(NodeSelectEvent event) {
        selectedNode = event.getTreeNode();
        actualizarRecursos((Carpeta) selectedNode.getData());
    }  
    
    public void download(Recurso recurso) throws FileNotFoundException {
        String archivo = destination + delimitador + usuario + delimitador + calcularUbicacion((Carpeta) selectedNode.getData()) + recurso.getNombre();
        InputStream stream = new FileInputStream(archivo);
        setFile(new DefaultStreamedContent(stream, "application/txt", recurso.getNombre())); 
    }
    
    public void onRowSelect(SelectEvent event) {
        Recurso recurso = (Recurso) event.getObject();
        selectedRow = recurso;
        if(recurso.getTipo().equals("Carpeta")) {
            selectedNode = findNode(root, selectedRow);
            recursos = ujc.findRecursos(selectedRow.getId());
        }
        if(recurso.getTipo().equals("Archivo")) {
            
        }
    }
    
    public void upload(FileUploadEvent event) throws PreexistingEntityException, RollbackFailureException, Exception {
        try {
            System.out.println("filename: " + event.getFile().getFileName());
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            actualizarRecursos((Carpeta) selectedNode.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void copyFile(String fileName, InputStream in) throws PreexistingEntityException, RollbackFailureException, Exception {
        try {
            String ubicacion = getDestination() + delimitador + usuario;
            ubicacion = ubicacion + delimitador + calcularUbicacion((Carpeta) selectedNode.getData());
            crearCarpetas(ubicacion);
            File directorio = new File(ubicacion);
            directorio.mkdir();
            OutputStream out = new FileOutputStream(new File(directorio, fileName));
            int read = 0;
            byte[] bytes = new byte[1024];
	 
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
	 
            in.close();
            out.flush();
            out.close();
            
            Archivo archivo = new Archivo();
            archivo.setCarpeta((Carpeta) selectedNode.getData());
            archivo.setNombreArchivo(fileName);
            archivo.setFecha(new Date());
            archivo.setPermiso(0);
            ajc.create(archivo);
            
            System.out.println("El nuevo fichero fue creado con éxito!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void crearCarpeta() throws PreexistingEntityException, RollbackFailureException, Exception {
        Carpeta carpeta = new Carpeta();
        carpeta.setNombre(nuevaCarpeta);
        carpeta.setUsuario(u);
        carpeta.setFecha(new Date());
        if(selectedNode != null)
            carpeta.setCarpetaSuper(((Carpeta) selectedNode.getData()).getCarpetaPK().getIdCarpeta());
        getCjc().create(carpeta);
        setCarpetas();
        nuevaCarpeta = "";
        if(selectedNode != null)
            actualizarRecursos((Carpeta) selectedNode.getData());
    }
    
    public void subirCarpeta() {
        Carpeta carpeta = (Carpeta) selectedNode.getData();
        if(carpeta.getCarpetaSuper() != null) {
            selectedNode = selectedNode.getParent();
            setCarpetas();
            actualizarRecursos((Carpeta) selectedNode.getData());
        }
        else {
            selectedNode = null;
            selectedRow = null;
            setCarpetas();
        }
    }
    
    public void open(Recurso recurso) {
        if(recurso.getTipo().equals("Carpeta")) {
            selectedRow = recurso;
            selectedNode = findNode(root, selectedRow);
            recursos = ujc.findRecursos(selectedRow.getId());
        }
    }
    
    public String mostrar(Recurso recurso) {
        String extension = getExtension(recurso.getNombre());
        setArchivoMostrar(getDestination() + delimitador + usuario + delimitador + calcularUbicacion((Carpeta) selectedNode.getData()) + recurso.getNombre());
        setNombre(recurso.getNombre());
        if(extension.toLowerCase().equals("xls")) {
            return "/Views/ExcelView?faces-redirect=true";
        }
        if(extension.toLowerCase().equals("dbf")) {
            return "/Views/DBFView?faces-redirect=true";
        }
        return "";
    }
    
    public String m() {
        return "hola";
    }
    /*
    public  List<SelectItem> getFechasPosibles() {
        List<SelectItem> fechasPosibles = ujc.getFechasPosibles(u.getIdUsuario()); 
        return fechasPosibles;
    }
    */
}


/*
System.out.println("usuario " + usuario);
System.out.println("Clave " + clave);
System.out.println("u " + u);
System.out.println("root " + root);
System.out.println("selectedNode " + selectedNode);
System.out.println("recursos " + recursos);
System.out.println("selectedRow " + selectedRow);
System.out.println("destination " + destination);
System.out.println("ujc " + ujc);
System.out.println("cjc " + cjc);
System.out.println("ajc " + ajc);
System.out.println("nuevaCarpeta " + nuevaCarpeta);
*/
