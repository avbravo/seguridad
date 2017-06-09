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
import com.avbravo.avbravoutils.security.SessionInterface;

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
    String rol="client";
    String usernameSelected;
    Boolean recoverSession = false;
    Boolean userwasLoged=false;
    String usernameRecover = "";
    String mytoken = "";

    private Boolean loggedIn = false;

    // <editor-fold defaultstate="collapsed" desc="get/set"> 

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
        recoverSession=false;
        userwasLoged=false;

    }

    public void verifySesionLocal() {
        try {

            recoverSession = false;
            usernameRecover = "";
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            if (session == null) {
                System.out.println(">>>>> es null");
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

    public String doLogin() {
        try {
userwasLoged=false;
            loggedIn = false;
            verifySesionLocal();
            if (recoverSession) {
                JsfUtil.warningDialog("Advertencia", "Cierre la otra pestaña o el navegador");
                return "";
            }
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();

            if (recoverSession && usernameRecover.equals(username)) {
//                System.out.println("---doLogin().esRecoverSession el mismo usuario no se debe verificar que exista en las sesiones");
            } else {

                if (isUserLogged(username)) {
                    userwasLoged=true;
                    JsfUtil.warningMessage("(Existe) un usuario logeado en este momento con ese username " + username);
                    return "";
                }

            }
            if (isUserValid()) {

                JsfUtil.addParametersUserNameToSession("username");
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

            JsfUtil.successMessage("Hola " + username + " a las " + JsfUtil.getTiempo());

        } catch (Exception e) {
            JsfUtil.errorMessage("saludar() " + e.getLocalizedMessage());
        }
        return "";
    }

    public String sendToken() {
        try {
            System.out.println("paso 1");
            ManagerEmail managerEmail = new ManagerEmail();
            System.out.println("paso 2");
            HttpSession httpSession = getSessionOfUsername(username);
            System.out.println("paso 3");
            if (httpSession != null) {
                System.out.println("paso 4");
                String token = httpSession.getAttribute("token").toString();
                System.out.println("paso 5");
                String texto = "Token para iniciar sesion: " + token;
                System.out.println("paso 6");
                if (managerEmail.send("avbravo@gmail.com", "Token de seguridad", texto, "aristides.netbeans@gmail.com", "coxip180denver$")) {
                    System.out.println("paso 7");
                    JsfUtil.successMessage("Se envio el token a su correo avbravo@gmail.com");

                } else {
                    System.out.println("paso 8");
                    JsfUtil.warningMessage("No se pudo enviar el email con el token");
                }
            } else {
                System.out.println("paso9");
                JsfUtil.warningMessage("No se pudo localizar una sesion activa para el usuario " + username);
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("sendToke() " + e.getLocalizedMessage());
        }
        return "";
    }

// <editor-fold defaultstate="collapsed" desc="nombre_metodo"> 
    public String destroyWithToken() {
        try {
            if (isUserValid()) {
                HttpSession httpSession = getSessionOfUsername(username);
                System.out.println("paso 3");
                if (httpSession != null) {
                    String token = httpSession.getAttribute("token").toString();
                    if(mytoken.equals(token)){
                        System.out.println("voy a inactivar el token "+token);
                        if(inactiveSessionByToken(token)){
                            JsfUtil.successMessage("Se inactivo la sesion para el usuario."+username +"  Intente ingresar ahora");
                            userwasLoged=false;
                            
                            return "";
                        }else{
                            JsfUtil.warningMessage("No se puede inactivar la session para el token");
                            return "";
                        }
                    }else{
                        JsfUtil.warningMessage("El token no coincide con el enviado a su email");
                        return "";
                    }
                }
            } else {
                JsfUtil.warningMessage("Los datos del usuario no son validos");
            }
        } catch (Exception e) {
            JsfUtil.errorMessage("loginWithToken() " + e.getLocalizedMessage());
        }
        return "";
    }
// </editor-fold>
}
