/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.classes;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.andesconsulting.dbf.DbfFile;


/**
 *
 * @author Administrador
 */
public class BaseDatos implements Serializable{
    // Conectar a archivo bdf
    private List<Clase> filas;
    private String ubicacion;
    private DbfFile bfile;
    public BaseDatos() throws IOException
    {
        actualizarFile();
        filas = new ArrayList();
        setFilas();
    }
    
    public BaseDatos(String ubicacion) throws IOException
    {
        this.ubicacion = ubicacion;
        actualizarFile();
        filas = new ArrayList();
        setFilas();
    }
    
    private void actualizarFile() throws FileNotFoundException, IOException
    {
        bfile = new DbfFile(getUbicacion());
        bfile.open();
    }
        
    public int getNumCmp()
    {
        return bfile.getFields().length;
    }
    
    public int getNumReg()
    {
        return bfile.getCount();
    }

    public List<Clase> getFilas() {
        return filas;
    }

    public void setFilas(List<Clase> filas) {
        this.filas = filas;
    }
    
    private void setFilas() throws IOException
    {
        for (int i = 1; i <= bfile.getCount(); i++) 
        {
            //colocar el cursor en el registro
            bfile.go(i);
            Map<String, String> map = bfile.scatter();
            Iterator it = map.keySet().iterator();
       
            Clase elemento = new Clase();
            while (it.hasNext()) 
            {
                // Get Clave 
                String clave = (String) it.next();
                String valor = map.get(clave);
                elemento.getCampos().add(valor);
            }
            filas.add(elemento);
        }
    }
    
    public List<String> getCampos() throws IOException
    {
        List<String> campos = new ArrayList();
        
        bfile.go(0);
        Map<String, String> map = bfile.scatter();
        Iterator it = map.keySet().iterator();

        while (it.hasNext()) 
        {
            String clave = (String) it.next();
            campos.add(clave);
        }
        return campos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public String getNombreArchivo() {
        int i = ubicacion.length()-1;
        while(i != 0)
        {
            char c = ubicacion.charAt(i);
            if(c == '\\') break;
            i--;
        }
        return ubicacion.substring(i+1, ubicacion.length());
    }
}
