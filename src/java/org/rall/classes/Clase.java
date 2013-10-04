/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.classes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrador
 */
public class Clase {
    private List<String> campos;
    public Clase()
    {
        campos = new ArrayList();
    }

    public List<String> getCampos() {
        return campos;
    }

    public void setCampos(List<String> campos) {
        this.campos = campos;
    }
}
