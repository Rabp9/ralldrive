/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rall.validators;

/**
 *
 * @author Roberto
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.rall.controllers.UsuarioJpaController;

@FacesValidator("dniValidator")
public class DniValidator implements Validator {

    @Override
    public void validate(FacesContext facesContext, UIComponent uIComponent, Object value) throws ValidatorException 
    {
        Pattern pattern = Pattern.compile("^[0-9]{8}");
        Matcher matcher = pattern.matcher((CharSequence) value);
        HtmlInputText htmlInputText = (HtmlInputText) uIComponent;
        String label;
        
        if (htmlInputText.getLabel() == null || htmlInputText.getLabel().trim().equals(""))
            label = htmlInputText.getId();
        else
            label = htmlInputText.getLabel();
        
        UsuarioJpaController ujc = new UsuarioJpaController(null, null);
        if (ujc.isDniExists((String) value))
        {
            FacesMessage facesMessage = new FacesMessage(label + ": ya existe un usuario registrado con este D.N.I.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw  new ValidatorException(facesMessage);
        }
        if (!matcher.matches())
        {
            FacesMessage facesMessage = new FacesMessage(label + ": no es un D.N.I. válido");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw  new ValidatorException(facesMessage);
        }
    }
}
