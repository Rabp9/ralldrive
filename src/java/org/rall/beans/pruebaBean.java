/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Roberto
 */
@ManagedBean
@SessionScoped
public class pruebaBean implements Serializable{

    /**
     * Creates a new instance of pruebaBean
     */
    private String value;
    
    public pruebaBean() {
    }

    public String updateValue() {
        setValue(String.valueOf(System.currentTimeMillis()));
        return null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
