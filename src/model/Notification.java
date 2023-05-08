/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;
import java.util.Date;
/**
 *
 * @author arzaq
 */

public class Notification {
    private int id;
    private String title;
    private String message;
    private Date timestamp;
    private NotificationAction action;

    public Notification(int id, String title, String message, Date timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void setAction(NotificationAction action) {
        this.action = action;
    }
    
    public class NotificationAction {
    private String label;
    private String actionUrl;

    public NotificationAction(String label, String actionUrl) {
        this.label = label;
        this.actionUrl = actionUrl;
    }

    // getters and setters
}

    // getters and setters
}



