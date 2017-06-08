/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.seguridad;

import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.security.BrowserSession;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.avbravo.avbravoutils.security.SessionInterface;
import java.util.Date;
import javax.faces.application.FacesMessage;
import org.apache.commons.lang.StringEscapeUtils;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

/**
 *
 * @author avbravo
 */
@Named
@SessionScoped
public class LoginController implements Serializable, SessionInterface {
 private static final long serialVersionUID = 1L;
    String username;
    String password;
    String usernameSelected;
    Boolean recoverSession = false;
    String usernameRecover = "";

    private Boolean loggedIn = false;

    // <editor-fold defaultstate="collapsed" desc="get/set"> 
    public String getUsernameSelected() {
        return usernameSelected;
    }

    public void setUsernameSelected(String usernameSelected) {
        this.usernameSelected = usernameSelected;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
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

    // </editor-fold>
    /**
     * Creates a new instance of Login
     */
    public LoginController() {
    }

    @PostConstruct
    public void init() {

        loggedIn = false;

    }

   
    public void verifySesionLocal() {
        try {

            recoverSession = false;
            usernameRecover = "";
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            if (session == null) {

            } else {
                if (session.getAttribute("username") != null) {
                    usernameRecover = session.getAttribute("username").toString();
//                   
                    recoverSession = true;

                } 
//                else {
//                    System.out.println("verifySesionLocal()()--> no tiene username");
//                }
            }
        } catch (Exception e) {
            JsfUtil.errorMessage("verifySesionLocal() " + e.getLocalizedMessage());
        }

    }

    @PreDestroy
    public void destroy() {
        System.out.println("...................................");
        System.out.println("----===== destroy " + JsfUtil.getTiempo());
        System.out.println("...................................");
    }


    public String doLogin(String type) {
        try {

            loggedIn = false;
            verifySesionLocal();
            if (recoverSession) {
                JsfUtil.warningDialog("Advertencia", "Cierre la otra pestaña o el navegador");
              //  return "";
            }
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();

            if (recoverSession && usernameRecover.equals(username)) {
//                System.out.println("---doLogin().esRecoverSession el mismo usuario no se debe verificar que exista en las sesiones");
            } else {
//                System.out.println("--doLogin() voy a verificar que exista en las sesiones");
                if (isUserLogged(username)) {
                    JsfUtil.warningMessage("(Existe) un usuario logeado en este momento con ese username " + username);
                    return "";
                }
//                System.out.println("---doLogin() No existe en las sesiones");
            }

            if (password.equals("demo")) {
                JsfUtil.addParametersUserNameToSession("username");
                session.setAttribute("username", username);
                //indicar el tiempo de la sesion predeterminado 2100segundos
                session.setMaxInactiveInterval(300);
                addUsername(username, session);

                loggedIn = true;
           return type;
                //return "admin";
            } else {
                JsfUtil.warningMessage("Usuario no valido");
            }
        } catch (Exception ex) {
            JsfUtil.errorMessage("doLogin() " + ex.getLocalizedMessage());
        }
        return "";
    }

//    @Override
//    public String showAllSessions() {
//        browserSessionsList = allBrowserSessionList();
//        return "";
//    }

  
  

  
    public String doLogout() {
        return logout("/seguridad/faces/index.xhtml?faces-redirect=true");

    }
 public String saludar() {
        try {

            JsfUtil.successMessage("Hola " + username +" a las " + JsfUtil.getTiempo());
         
        } catch (Exception e) {
            JsfUtil.errorMessage("saludar() " + e.getLocalizedMessage());
        }
        return "";
    }

   
   

}
