/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avbravo.seguridad.websocket;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author avbravo
 */
@Named(value = "notifyView")
@RequestScoped
public class NotifyView {
private final static String CHANNEL = "/notify";
     
    private String summary;
     
    private String detail;
     
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
     
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
     
    public void send() {
        EventBus eventBus = EventBusFactory.getDefault().eventBus();
        eventBus.publish(CHANNEL, new FacesMessage(StringEscapeUtils.escapeHtml(summary), StringEscapeUtils.escapeHtml(detail)));
    }
    
}
