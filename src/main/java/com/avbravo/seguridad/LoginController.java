/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.seguridad;

import com.avbravo.avbravoutils.JsfUtil;
import com.avbravo.avbravoutils.security.LoginInterface;
import com.avbravo.avbravoutils.security.SessionListener;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author avbravo
 */
@Named
@SessionScoped
public class LoginController implements Serializable, LoginInterface {

    String username;
    String password;
    String usernameSelected;
    Boolean recoverSession = false;
    String usernameRecover = "";

    private Boolean loggedIn = false;
    private List<String> usernameList = new ArrayList<>();
    // <editor-fold defaultstate="collapsed" desc="get/set"> 

    public String getUsernameSelected() {
        return usernameSelected;
    }

    public void setUsernameSelected(String usernameSelected) {
        this.usernameSelected = usernameSelected;
    }

    public List<String> getUsernameList() {
        usernameList = _getAllUser();
        return usernameList;
    }

    public void setUsernameList(List<String> usernameList) {
        this.usernameList = usernameList;
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

    private void verifySesionLocal() {
        try {
//            System.out.println("...................................");
//            System.out.println("......verifySesionLocal()() hora:" + JsfUtil.getTiempo());
            recoverSession = false;
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            if (session == null) {
//                System.out.println(".............verifySesionLocal()()----> Session es null");
            } else {
//                System.out.println(".............verifySesionLocal()()----> Session No es null");
                if (session.getAttribute("username") != null) {
                   // session.invalidate();

                    usernameRecover = session.getAttribute("username").toString();
//                    System.out.println(".............verifySesionLocal()()-->username " + usernameRecover);
//                    System.out.println(".............verifySesionLocal()()--> voy a pasarle el username");
//
//                    System.out.println(".............verifySesionLocal()()---> le pase el username");
//                    
                    recoverSession = true;
                    //   usernameRecover=username;
                } else {
                    System.out.println("verifySesionLocal()()--> no tiene username");
                }
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

    @Override
    public String doLogin() {
        try {
    
            loggedIn = false;
            verifySesionLocal();
            if(recoverSession){
                JsfUtil.warningDialog("Advertencia", "Cierre la otra pestaña o el navegador");
                //return "";
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
                SessionListener.addUsername(username);
                loggedIn = true;
                return "admin";
            } else {
                JsfUtil.warningMessage("Usuario no valido");
            }
        } catch (Exception ex) {
            JsfUtil.errorMessage("doLogin() " + ex.getLocalizedMessage());
        }
        return "";
    }

    @Override
    public String showAllSessions() {
        return _showAllSessions();
    }

    @Override
    public String killAllSessions() {
        _killAllSesion();
        return "";
    }
//    public String showAllSessions() {
//        try {
//            System.out.println("Mostrando las sesiones");
//            for (HttpSession h : SessionListener.getSessionList()) {
//                System.out.println("----> voy a mostrar la sesion");
//                System.out.println("h= "+h);
//                System.out.println("id " + h.getId());
//                
//                JsfUtil.successMessage("id " + h.getId() + " username " + h.getAttribute("username") );
//
//            }
//
//            JsfUtil.successMessage("Sesiones mostradas");
//        } catch (Exception e) {
//            JsfUtil.errorMessage("showSessions() " + e.getLocalizedMessage());
//        }
//        return "";
//    }

    public String killSessionByUserName() {
        try {
            if (username.equals(usernameSelected)) {
                JsfUtil.warningMessage("No se debe eliminar su propia sesion. Use la opcion salir");

                return "";
            }
            if (killSesionByUserName(usernameSelected)) {
                JsfUtil.successMessage("Se termino la sesion de" + usernameSelected);
                loadAllUser();

            } else {
                JsfUtil.warningMessage("No se pudo terminar la sesion de " + usernameSelected);
            }

//            if(SessionListener.killSesionByUsername(usernameSelected)){
//                JsfUtil.successMessage("Se termino la sesion de "+usernameSelected);
//               
//                 if(username.equals(usernameSelected)){
//               doLogout();
//            }else{
//                    showUser();  
//                 }
//            }else{
//                JsfUtil.warningMessage("No se pudo terminar la sesion de "+usernameSelected);
//            }
        } catch (Exception e) {
            JsfUtil.errorMessage("killByUserName() " + e.getLocalizedMessage());
        }
        return "";
    }
//    public String killAllSesion() {
//        try {
//            if (SessionListener.killAllSesion()) {
//                JsfUtil.successMessage("Se eliminaron todas las sesiones");
//            } else {
//                JsfUtil.successMessage("(No) se eliminaron todas las sesiones");
//            }
//
//        } catch (Exception e) {
//            JsfUtil.errorMessage("recorrer() " + e.getLocalizedMessage());
//        }
//        return "";
//    }

//    public String showUser() {
//        try {
//            if (SessionListener.getUsernameList().isEmpty()) {
//                JsfUtil.warningMessage("No hay usuarios online registrados");
//                return "";
//            }
////        
//            usuariosList = SessionListener.getUsernameOnline();
//        } catch (Exception e) {
//            JsfUtil.errorMessage("showUser() " + e.getLocalizedMessage());
//        }
//        return "";
//    }
    @Override
    public String doLogout() {
        return logout("/seguridad/faces/index.xhtml?faces-redirect=true");
//        Boolean loggedIn = false;
//        try {
//            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//            if (session != null) {
//                session.invalidate();
//            }
//            String url = ("/seguridad/faces/index.xhtml?faces-redirect=true");
//            FacesContext fc = FacesContext.getCurrentInstance();
//            ExternalContext ec = fc.getExternalContext();
//            ec.redirect(url);
//            return "/seguridad/faces/login.xhtml?faces-redirect=true";
//        } catch (Exception e) {
//            JsfUtil.errorMessage(e, "logout()");
//        }
//        return "/seguridad/faces/login.xhtml?faces-redirect=true";
    }

    public String saludar() {
        try {

            JsfUtil.successMessage("Hola " + username);
        } catch (Exception e) {
            JsfUtil.errorMessage("saludar() " + e.getLocalizedMessage());
        }
        return "";
    }

//    public String validateAllUserInSesion(){
//        try {
//             SessionListener.validateUsernameWithSession();
//        } catch (Exception e) {
//            JsfUtil.errorMessage("validateUserInSesion() "+e.getLocalizedMessage());
//        }
//       
//        return "";
//    }
// <editor-fold defaultstate="collapsed" desc="validateAllSessions()"> 
    @Override
    public String validateAllSessions() {
        _validateAllSesion();
        return "";
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="showUser"> 
    @Override
    public String loadAllUser() {
        usernameList = _getAllUser();
        return "";
    }
// </editor-fold>

}
