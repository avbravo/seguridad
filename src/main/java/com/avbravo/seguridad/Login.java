/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.seguridad;

import com.avbravo.avbravoutils.JsfUtil;

import com.avbravo.avbravoutils.security.SessionListener;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author avbravo
 */
@Named(value = "login")
@SessionScoped
public class Login implements Serializable {

    String username;
    String password;
    String usernameSelected;
     private List<String> usuariosList = new ArrayList<>();

    public String getUsernameSelected() {
        return usernameSelected;
    }

    public void setUsernameSelected(String usernameSelected) {
        this.usernameSelected = usernameSelected;
    }

    public List<String> getUsuariosList() {
        return usuariosList;
    }

    public void setUsuariosList(List<String> usuariosList) {
        this.usuariosList = usuariosList;
    }

  

   


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Creates a new instance of Login
     */
    public Login() {
    }

    public String validar() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            SessionListener.setMaximosSegundosInactividad(80);
            if (SessionListener.isUserLoged(username)) {
                JsfUtil.warningMessage("(Existe) un usuario logeado en este momento con ese username " + username);
                return "";
            }
            if (password.equals("demo")) {
                session.setAttribute("username", username);
                SessionListener.addUsername(username);
                return "welcome";
            } else {
                JsfUtil.warningMessage("Usuario no valido");
            }
        } catch (Exception ex) {
            JsfUtil.errorMessage("validar() " + ex.getLocalizedMessage());
        }
        return "";
    }

    public String showSessions() {
        try {
            System.out.println("Mostrando las sesiones");
            for (HttpSession h : SessionListener.getSessionList()) {
                System.out.println("id " + h.getId());
                
                JsfUtil.successMessage("id " + h.getId() + " username " + h.getAttribute("username") );

            }

            JsfUtil.successMessage("Sesiones mostradas");
        } catch (Exception e) {
            JsfUtil.errorMessage("recorrer() " + e.getLocalizedMessage());
        }
        return "";
    }

    public String killByUserName()
    {
        try {
            if(SessionListener.killSesionByUsername(usernameSelected)){
                JsfUtil.successMessage("Se termino la sesion de "+usernameSelected);
            }else{
                JsfUtil.warningMessage("No se pudo terminar la sesion de "+usernameSelected);
            }
        } catch (Exception e) {
             JsfUtil.errorMessage("killByUserName() " + e.getLocalizedMessage());
        }
        return "";
    }
    public String kill() {
        try {
            if (SessionListener.killAllSesion()) {
                JsfUtil.successMessage("Se eliminaron todas las sesiones");
            } else {
                JsfUtil.successMessage("(No) se eliminaron todas las sesiones");
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("recorrer() " + e.getLocalizedMessage());
        }
        return "";
    }

    public String showUser() {
        try {
            if (SessionListener.getUsernameList().isEmpty()) {
                JsfUtil.warningMessage("No hay usuarios online registrados");
                return "";
            }
//            for (String s : SessionListener.getUsernameList()) {
//                JsfUtil.successMessage(s);
//            }
            usuariosList = SessionListener.getUsernameOnline();
        } catch (Exception e) {
            JsfUtil.errorMessage("showUser() " + e.getLocalizedMessage());
        }
        return "";
    }

    public String doLogout() {
        Boolean loggedIn = false;
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            if (session != null) {
                session.invalidate();
            }
            String url = ("/seguridad/faces/index.xhtml?faces-redirect=true");
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.redirect(url);
            return "/seguridad/faces/login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            JsfUtil.errorMessage(e, "logout()");
        }
        return "/seguridad/faces/login.xhtml?faces-redirect=true";
    }
    
    public String saludar(){
        try {
            
       
        JsfUtil.successMessage("Hola "+username);
         } catch (Exception e) {
             JsfUtil.errorMessage("saludar() "+e.getLocalizedMessage());
        }
        return "";
    }
}
