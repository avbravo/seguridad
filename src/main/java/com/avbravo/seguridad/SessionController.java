/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.seguridad;
// <editor-fold defaultstate="collapsed" desc="import"> 

import com.avbravo.avbravosecurity.BrowserSession;
import com.avbravo.avbravosecurity.SecurityInterface;
import com.avbravo.avbravoutils.JsfUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

// </editor-fold>

/**
 *
 * @author avbravo
 */
@Named
@ViewScoped
public class SessionController implements Serializable, SecurityInterface {

// <editor-fold defaultstate="collapsed" desc="atributos"> 
    private static final long serialVersionUID = 1L;
    private Integer segundosRefresh = 3;
    @Inject
    LoginController loginController;
    private BrowserSession browserSessionSelecction = new BrowserSession();
    List<BrowserSession> browserSessionsList = new ArrayList<>();
    List<BrowserSession> browserSessionsFilterList = new ArrayList<>();

// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="setget"> 
    public Integer getSegundosRefresh() {
        return segundosRefresh;
    }

    public void setSegundosRefresh(Integer segundosRefresh) {
        this.segundosRefresh = segundosRefresh;
    }

    public BrowserSession getBrowserSessionSelecction() {
        return browserSessionSelecction;
    }

    public void setBrowserSessionSelecction(BrowserSession browserSessionSelecction) {
        this.browserSessionSelecction = browserSessionSelecction;
    }

    public List<BrowserSession> getBrowserSessionsList() {
        return browserSessionsList;
    }

    public void setBrowserSessionsList(List<BrowserSession> browserSessionsList) {
        this.browserSessionsList = browserSessionsList;
    }

    public List<BrowserSession> getBrowserSessionsFilterList() {
        return browserSessionsFilterList;
    }

    public void setBrowserSessionsFilterList(List<BrowserSession> browserSessionsFilterList) {
        this.browserSessionsFilterList = browserSessionsFilterList;
    }
// </editor-fold>

    /**
     * Creates a new instance of SessionController
     */
    public SessionController() {
    }

    // <editor-fold defaultstate="collapsed" desc="init"> 
    @PostConstruct
    public void init() {

        showAllSessions();

    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="destroy"> 
    @PreDestroy
    public void destroy() {

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="showAllSessions"> 
    public String showAllSessions() {
        try {
            browserSessionsList = allBrowserSessionList();
            browserSessionsFilterList = browserSessionsList;
            if (browserSessionsList.isEmpty()) {
                JsfUtil.warningMessage("No hay sesiones registradas");
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("showAllSessions() " + e.getLocalizedMessage());
        }

        return "";
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="killAllSessions"> 
    public String killAllSessions() {
        try {
            if (cancelAllSesion()) {
                showAllSessions();
                JsfUtil.successMessage("Se eliminaron todas las sesiones");

            } else {
                JsfUtil.successMessage("(No) se eliminaron todas las sesiones");
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("KillAllSessions() " + e.getLocalizedMessage());
        }

        return "";
    }// </editor-fold>

    
    
// <editor-fold defaultstate="collapsed" desc="cancelSelectedSession"> 
    public String cancelSelectedSession(BrowserSession browserSesssion) {
        try {
            if (loginController.getUsername().equals(browserSesssion.getUsername())) {
                JsfUtil.warningMessage("No se debe eliminar su propia sesion. Use la opcion salir");

                return "";
            }
            if (inactiveSession(browserSesssion)) {
                JsfUtil.successMessage("Se cancelo la sesion");
                browserSessionsList = allBrowserSessionList();
                browserSessionsFilterList = browserSessionsList;
            } else {
                JsfUtil.warningMessage("No se cancelo la sesion");
            }

        } catch (Exception e) {
            JsfUtil.errorMessage("cancelSession() " + e.getLocalizedMessage());
        }
        return "";
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dateofEpirtation"> 
    public Date dateOfExpiration(HttpSession session) {
        return getDateTiemExpiration(session);
    }
// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="secondsForInactivity"> 
    public String secondsForInactivity(HttpSession session) {

        return JsfUtil.milisegundosToTiempoString(milisegundosForInactivate(session));
    }// </editor-fold>
    
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="timeOfConnection"> 

    public Date timeOfCreation(HttpSession session) {

        return new Date(session.getCreationTime());

    }

    public String timeOfConnection(HttpSession session) {
        try {
            return JsfUtil.milisegundosToTiempoString(miliSecondsOfConnection(session));
        } catch (Exception e) {
            JsfUtil.errorMessage("timeOfConecction() " + e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>
// <editor-fold defaultstate="collapsed" desc="lastConnection"> 

    public Date lastConnection(HttpSession session) {
        //return JsfUtil.milisegundosToTiempoString(session.getLastAccessedTime());
        return new Date(session.getLastAccessedTime());

    }
    // </editor-fold>
    
// <editor-fold defaultstate="collapsed" desc="saludar"> 

    public String saludar() {
        try {

            JsfUtil.successMessage("Hola " + loginController.getUsername() + " a las " + JsfUtil.getTiempo());
            browserSessionsList = allBrowserSessionList();
            browserSessionsFilterList = browserSessionsList;
        } catch (Exception e) {
            JsfUtil.errorMessage("saludar() " + e.getLocalizedMessage());
        }
        return "";
    }
    // </editor-fold>
}
