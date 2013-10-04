/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.beans;

import org.rall.classes.Clase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author essalud
 */
@ManagedBean
@RequestScoped
public class ExcelBean {

    /**
     * Creates a new instance of ExcelBean
     */
    private List<Clase> hoja;
    private List<String> campos;
    private String archivoExcel;
    private String nombreArchivo;
    
    public ExcelBean() throws FileNotFoundException, IOException {
        FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        archivoExcel = ((LoginBean) session.getAttribute("loginBean")).getArchivoMostrar();
        nombreArchivo = ((LoginBean) session.getAttribute("loginBean")).getNombre();
        hoja = new ArrayList();
        setHoja();
    }

    public List<Clase> getHoja() {
        return hoja;
    }

    public void setHoja(List<Clase> hoja) {
        this.hoja = hoja;
    }
    
    public void setHoja() {
        try
        {
            FileInputStream file = new FileInputStream(new File(archivoExcel));
     
            //Get the workbook instance for XLS file 
            HSSFWorkbook workbook = new HSSFWorkbook(file);
 
            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);
     
            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            while(rowIterator.hasNext())
            {
                Row row = rowIterator.next();

                //For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                Clase elemento = new Clase();
                while(cellIterator.hasNext()) 
                {
             
                    Cell cell = cellIterator.next();
             
                    switch(cell.getCellType())
                    {
                        case Cell.CELL_TYPE_BOOLEAN:
                            elemento.getCampos().add(String.valueOf(cell.getBooleanCellValue()));
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            elemento.getCampos().add(String.valueOf(cell.getNumericCellValue()));
                            break;
                        case Cell.CELL_TYPE_STRING:
                            elemento.getCampos().add(cell.getStringCellValue());
                            break;
                        case Cell.CELL_TYPE_BLANK:
                            elemento.getCampos().add("");
                            break;
                    }
                }
                getHoja().add(elemento);
            }
            reordenaHoja();
            file.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<String> getCampos() {
        List<String> campos = new ArrayList();
        
        try
        {
            FileInputStream file = new FileInputStream(new File(archivoExcel));
     
            //Get the workbook instance for XLS file 
            HSSFWorkbook workbook = new HSSFWorkbook(file);
      
            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);
     
            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            Row row = rowIterator.next();

            //For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            char col = 65;
            
            while(cellIterator.hasNext()) 
            {
                cellIterator.next();
                campos.add(Character.toString(col));
                col++;
            }
            file.close();
        }        
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return campos;
    }

    public void setCampos(List<String> campos) {
        this.campos = campos;
    }

    public String getArchivoExcel() {
        return archivoExcel;
    }

    public void setArchivoExcel(String archivoExcel) {
        this.archivoExcel = archivoExcel;
    }

    private void reordenaHoja() {
        int sizeMax = 0 ;
        for (int i = 0; i < getHoja().size(); i++) {
            Clase elemento = getHoja().get(i);
            if(elemento.getCampos().size() > sizeMax) {
                sizeMax = elemento.getCampos().size();
            }
        }
        
        for (int i = 0; i < getHoja().size(); i++) {
            Clase elemento = getHoja().get(i);
            int faltan = sizeMax - elemento.getCampos().size();
            for (int j = 0; j < faltan; j++) {
                elemento.getCampos().add("");
            }
        }
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
}
