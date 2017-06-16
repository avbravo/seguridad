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
    String myemail = "@gmail.com";
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
// <editor-fold defaultstate="collapsed" desc="doLogin"> 

    public String doLogin() {
        try {
            userwasLoged = false;
            tokenwassend = false;
            loggedIn = false;
            //aqui

            usernameRecover = usernameRecoveryOfSession();
            recoverSession = !usernameRecover.equals("");
            if (recoverSession) {
                RequestContext.getCurrentInstance().execute("PF('sessionDialog').show();");
                return "";
            }

            if (recoverSession && usernameRecover.equals(username)) {

            } else {

                if (isUserLogged(username)) {
                    userwasLoged = true;
                    JsfUtil.warningMessage("(Existe) un usuario logeado en este momento con ese username " + username);
                    return "";
                }

            }
            if (isUserValid()) {

                saveUserInSession(username, 300);
                // saveUserInSession(username, 2100);

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
    }// </editor-fold>


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
// <editor-fold defaultstate="collapsed" desc="doLogout"> 

    public String doLogout() {
        return logout("/seguridad/faces/index.xhtml?faces-redirect=true");

    }// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="sendToken()"> 
    public String sendToken() {
        try {

//            if(!myemail.equals("emailusuario")){
//                //no es el email del usuario
//            }
            ManagerEmail managerEmail = new ManagerEmail();
            String token = tokenOfUsername(username);
            if (!token.equals("")) {

                String texto = "Token para iniciar sesion: " + token + "\r\n " + "Copie este en el sistema y haga clic en el boton Invalidar Sesion por Token";
                if (managerEmail.send(myemail, "Token de seguridad", texto, "aristides.netbeans@gmail.com", "coxip180denver$")) {
                    JsfUtil.successMessage("Se envio el token a su correo " + myemail + "");
                    tokenwassend = true;
                } else {
                    JsfUtil.warningMessage("No se pudo enviar el email con el token");
                }
            } else {
                JsfUtil.warningMessage("No hay token asignado al usuario:  " + username);
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("sendToken() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="destroyByUser()"> 
    public String destroyByUser() {
        try {
            if (isUserValid()) {
                userwasLoged = !destroyByUsername(username);
                if (!userwasLoged) {
                    JsfUtil.successMessage("Fue destruida la sesion puede ingresar al sistema");
                } else {
                    JsfUtil.successMessage("No fue destruida la sesion no puede ingresar al sistema");
                }
            } else {
                JsfUtil.warningMessage("Los datos del usuario no son validos");
            }
        } catch (Exception e) {
            JsfUtil.errorMessage("destroyByUser() " + e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="destroyWithToken()"> 

    public String destroyByToken() {
        try {
            if (isUserValid()) {
                userwasLoged = !destroyByToken(username, mytoken);

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

    public String invalidarActual() {
        try {
            if (invalidateMySession()) {
                JsfUtil.successMessage("Sesion actual invalidada exitosamente");
            } else {
                JsfUtil.warningMessage("No se pudo invalidar la sesion actual");
            }

        } catch (Exception e) {
            JsfUtil.successMessage("invalidarActual() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>
}
