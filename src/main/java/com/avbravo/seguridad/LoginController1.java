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
public class LoginController1 implements Serializable, SessionInterface {

    String username;
    String password;
    String usernameSelected;
    Boolean recoverSession = false;
    String usernameRecover = "";

    private Boolean loggedIn = false;
    
    private BrowserSession browserSessionSelecction = new BrowserSession();

             List<BrowserSession> browserSessionsList = new ArrayList<>();
             List<BrowserSession> browserSessionsFilterList = new ArrayList<>();
    // <editor-fold defaultstate="collapsed" desc="get/set"> 

    public List<BrowserSession> getBrowserSessionsFilterList() {
        return browserSessionsFilterList;
    }

    public void setBrowserSessionsFilterList(List<BrowserSession> browserSessionsFilterList) {
        this.browserSessionsFilterList = browserSessionsFilterList;
    }

             
             
    public BrowserSession getBrowserSessionSelecction() {
        return browserSessionSelecction;
    }

    public void setBrowserSessionSelecction(BrowserSession browserSessionSelecction) {
        this.browserSessionSelecction = browserSessionSelecction;
    }

    

             
             
             
    public List<BrowserSession> getBrowserSessionsList() {
        browserSessionsList = allBrowserSessionList();
        return browserSessionsList;
    }

    public void setBrowserSessionsList(List<BrowserSession> browserSessionsList) {
        this.browserSessionsList = browserSessionsList;
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

    // </editor-fold>
    /**
     * Creates a new instance of Login
     */
    public LoginController1() {
    }

    @PostConstruct
    public void init() {

        loggedIn = false;

    }

   public void verifySesionLocal() {
        try {

            recoverSession = false;
             usernameRecover="";
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpSession session = request.getSession();
            if (session == null) {

            } else {
                if (session.getAttribute("username") != null) {
                    usernameRecover = session.getAttribute("username").toString();
//                   
                    recoverSession = true;

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
                addUsername(username,session);
                
                loggedIn = true;
                  notificarPUSH();
                return "admin";
            } else {
                JsfUtil.warningMessage("Usuario no valido");
            }
        } catch (Exception ex) {
            JsfUtil.errorMessage("doLogin() " + ex.getLocalizedMessage());
        }
        return "";
    }

   
    public String showAllSessions() {
         browserSessionsList = allBrowserSessionList();
        return "";
    }


    public String killAllSessions() {
       cancelAllSesion();
        return "";
    }


    public String cancelSelectedSession(BrowserSession browserSesssion){
        try {
             if (username.equals(browserSesssion.getUsername())) {
                JsfUtil.warningMessage("No se debe eliminar su propia sesion. Use la opcion salir");

                return "";
            }
          if(  inactiveSession(browserSesssion)){
              JsfUtil.successMessage("Se cancelo la sesion");
           browserSessionsList = allBrowserSessionList();
          }else{
              JsfUtil.warningMessage("No se cancelo la sesion");
          }
          
        } catch (Exception e) {
            JsfUtil.errorMessage("cancelSession() "+e.getLocalizedMessage());
        }
        return "";
    }


    public String doLogout() {
        return logout("/seguridad/faces/index.xhtml?faces-redirect=true");

    }

    public String saludar() {
        try {

            JsfUtil.successMessage("Hola " + username + " a las "+JsfUtil.getTiempo());
        } catch (Exception e) {
            JsfUtil.errorMessage("saludar() " + e.getLocalizedMessage());
        }
        return "";
    }

    public String toHour(Long milisegundos){
        return JsfUtil.milisegundosToTiempoString(milisegundos);
    }
public String tiempoRestante(HttpSession session,Integer inactivatePeriodo,Long milisegundos){
    Integer restante=0;
    try {
        Integer limite = JsfUtil.milisegundosToSegundos(session.getCreationTime()) + session.getMaxInactiveInterval();
        Date expiry = new Date(session.getLastAccessedTime() + session.getMaxInactiveInterval()*1000);
         
       restante = inactivatePeriodo- JsfUtil.milisegundosToSegundos(milisegundos);
    } catch (Exception e) {
               JsfUtil.errorMessage("tiempoRestante() " + e.getLocalizedMessage());
    }
  return  JsfUtil.segundosToHoraString(restante);
}
public Date expiracion(HttpSession session,Integer inactivatePeriodo,Long milisegundos){
    Integer restante=0;
    Date expiry= new Date();
    try {
        Integer limite = JsfUtil.milisegundosToSegundos(session.getCreationTime()) + session.getMaxInactiveInterval();
       expiry = new Date(session.getLastAccessedTime() + session.getMaxInactiveInterval()*1000);
         
       restante = inactivatePeriodo- JsfUtil.milisegundosToSegundos(milisegundos);
    } catch (Exception e) {
               JsfUtil.errorMessage("tiempoRestante() " + e.getLocalizedMessage());
    }
  return  expiry;
}
public String tiempoAcceso(Long milisegundos){
  return  JsfUtil.milisegundosToTiempoString(milisegundos);
}

public void notificarPUSH() {

        String summary = "Nuevo Elemento";
        String detail = "Se agrego a la lista";
        String CHANNEL = "/notify";

        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish(CHANNEL, new FacesMessage(StringEscapeUtils.escapeHtml(summary), StringEscapeUtils.escapeHtml(detail)));
    }
    
   
}
