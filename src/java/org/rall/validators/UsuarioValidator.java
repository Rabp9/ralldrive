/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.validators;

/**
 *
 * @author Roberto
 */
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.rall.controllers.UsuarioJpaController;

@FacesValidator("usuarioValidator")
public class UsuarioValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent uIComponent, Object value) throws ValidatorException 
    {
        HtmlInputText htmlInputText = (HtmlInputText) uIComponent;
        String label;
        
        if (htmlInputText.getLabel() == null || htmlInputText.getLabel().trim().equals(""))
            label = htmlInputText.getId();
        else
            label = htmlInputText.getLabel();
        UsuarioJpaController ujc = new UsuarioJpaController(null, null);
        
        if (String.valueOf(value).contains(" "))
        {
            FacesMessage facesMessage = new FacesMessage(label + ": el nombre de usuario no puede contener espacios en blanco.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw  new ValidatorException(facesMessage);
        }
        
        if (ujc.isUsuarioExists((String) value))
        {
            FacesMessage facesMessage = new FacesMessage(label + ": ya existe un usuario con este nombre de usuario.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw  new ValidatorException(facesMessage);
        }
    }
}
