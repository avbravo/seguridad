/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.seguridad;

import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.email.ManagerEmail;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.avbravo.avbravoutils.security.SecurityInterface;
import org.primefaces.context.RequestContext;

/**
 *
 * @author avbravo
 */
@Named
@SessionScoped
public class LoginController implements Serializable, SecurityInterface {

    private static final long serialVersionUID = 1L;
    String username;
    String password;
    String rol = "client";
    String usernameSelected;
    Boolean recoverSession = false;
    Boolean userwasLoged = false;
    Boolean tokenwassend = false;
    String usernameRecover = "";
    String myemail="@gmail.com";
    String mytoken = "";

    private Boolean loggedIn = false;

    // <editor-fold defaultstate="collapsed" desc="get/set"> 

    public String getMyemail() {
        return myemail;
    }

    public void setMyemail(String myemail) {
        this.myemail = myemail;
    }
    
    
    public Boolean getTokenwassend() {
        return tokenwassend;
    }

    public void setTokenwassend(Boolean tokenwassend) {
        this.tokenwassend = tokenwassend;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getMytoken() {
        return mytoken;
    }

    public void setMytoken(String mytoken) {
        this.mytoken = mytoken;
    }

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

    public Boolean getUserwasLoged() {
        return userwasLoged;
    }

    public void setUserwasLoged(Boolean userwasLoged) {
        this.userwasLoged = userwasLoged;
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
        recoverSession = false;
        userwasLoged = false;
        tokenwassend = false;

    }


    @PreDestroy
    public void destroy() {

    }

    public String doLogin() {
        try {
            userwasLoged = false;
            tokenwassend = false;
            loggedIn = false;
            //aqui

            //  verifySesionLocal();
            usernameRecover = getUsernameRecoveryOfSession();
            recoverSession = !usernameRecover.equals("");

            if (recoverSession) {
                // JsfUtil.warningDialog("Advertencia", "Cierre la otra pestaña o el navegador");
                RequestContext.getCurrentInstance().execute("PF('sessionDialog').show();");
//session.invalidate();
                return "";
            }

            if (recoverSession && usernameRecover.equals(username)) {
//                System.out.println("---doLogin().esRecoverSession el mismo usuario no se debe verificar que exista en las sesiones");
            } else {

                if (isUserLogged(username)) {
                    userwasLoged = true;
                    JsfUtil.warningMessage("(Existe) un usuario logeado en este momento con ese username " + username);
                    return "";
                }

            }
            if (isUserValid()) {
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                HttpSession session = request.getSession();
//                JsfUtil.addParametersUserNameToSession("username");
                session.setAttribute("username", username);
                String token = JsfUtil.getUUID();
                token = token.substring(0, 6);

                session.setAttribute("token", token);
                //indicar el tiempo de la sesion predeterminado 2100segundos
           session.setMaxInactiveInterval(2100);
           

                addUsername(username, session, token);

                loggedIn = true;
                return rol;
                //return "admin";
            } else {
                JsfUtil.warningMessage("Usuario no valido");
            }
        } catch (Exception ex) {
            JsfUtil.errorMessage("doLogin() " + ex.getLocalizedMessage());
        }
        return "";
    }

// <editor-fold defaultstate="collapsed" desc="isUserValid"> 
    /**
     * verifica si es valido el usuario
     *
     * @return
     */
    private Boolean isUserValid() {
        Boolean isvalid = false;
        try {
            //Aqui se valida contra la base de datos
            if (username.isEmpty() || username.equals("") || username == null) {
                JsfUtil.warningMessage("Ingrese un usuario");
                return false;
            }
            if (password.isEmpty() || password.equals("") || password == null) {
                JsfUtil.warningMessage("Ingrese el password");
                return false;
            }
            if (password.equals("demo")) {
                return true;
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("userValid() " + e.getLocalizedMessage());
        }
        return isvalid;
    }// </editor-fold>

    public String doLogout() {
        return logout("/seguridad/faces/index.xhtml?faces-redirect=true");

    }

// <editor-fold defaultstate="collapsed" desc="sendToken()"> 
//
//    public String sendToken() {
//        try {
//
////            if(!myemail.equals("emailusuario")){
////                //no es el email del usuario
////            }
//            ManagerEmail managerEmail = new ManagerEmail();
//
//            HttpSession httpSession = getSessionOfUsername(username);
//
//            if (httpSession != null) {
//
//                String token = httpSession.getAttribute("token").toString();
//                String texto = "Token para iniciar sesion: " + token + "\r\n "+ "Copie este en el sistema y haga clic en el boton Invalidar Sesion por Token";
//                if (managerEmail.send(myemail, "Token de seguridad", texto, "aristides.netbeans@gmail.com", "coxip180denver$")) {
//                    JsfUtil.successMessage("Se envio el token a su correo "+myemail+ "");
//                    tokenwassend = true;
//                } else {
//                    JsfUtil.warningMessage("No se pudo enviar el email con el token");
//                }
//            } else {
//                JsfUtil.warningMessage("No se pudo localizar una sesion activa para el usuario " + username);
//            }
//
//        } catch (Exception e) {
//            JsfUtil.errorMessage("sendToke() " + e.getLocalizedMessage());
//        }
//        return "";
//    }// </editor-fold>
//    
    
// <editor-fold defaultstate="collapsed" desc="sendToken()"> 

    public String sendToken() {
        try {

//            if(!myemail.equals("emailusuario")){
//                //no es el email del usuario
//            }
            ManagerEmail managerEmail = new ManagerEmail();
            String token = getTokenOfUsername(username);
            if(!token.equals("")){

                String texto = "Token para iniciar sesion: " + token + "\r\n "+ "Copie este en el sistema y haga clic en el boton Invalidar Sesion por Token";
                if (managerEmail.send(myemail, "Token de seguridad", texto, "aristides.netbeans@gmail.com", "coxip180denver$")) {
                    JsfUtil.successMessage("Se envio el token a su correo "+myemail+ "");
                    tokenwassend = true;
                } else {
                    JsfUtil.warningMessage("No se pudo enviar el email con el token");
                }
            } else {
                JsfUtil.warningMessage("No hay token asignado al usuario:  " + username);
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("sendToke() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
    
    
    
    

    // <editor-fold defaultstate="collapsed" desc="destroyWithToken()"> 
    public String destroyWithToken() {
        try {
            if (isUserValid()) {
                userwasLoged = !destroyWithToken(username, mytoken);

            } else {
                JsfUtil.warningMessage("Los datos del usuario no son validos");
            }
        } catch (Exception e) {
            JsfUtil.errorMessage("loginWithToken() " + e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>

    public String saludar() {
        JsfUtil.successMessage("Hola " + username + " a las " + JsfUtil.getTiempo());
        return "";
    }
// <editor-fold defaultstate="collapsed" desc="invalidarActual()"> 
// </editor-fold>
    public String invalidarActual() {
        try {
            if(invalidateMySession()){
                JsfUtil.successMessage("Sesion actual invalidada exitosamente");
            }else{
                JsfUtil.warningMessage("No se pudo invalidar la sesion actual");
            }

        } catch (Exception e) {
            JsfUtil.successMessage("invalidarActual() " + e.getLocalizedMessage());
        }
        return "";
    }
}
