package com.taskmanagement.utils;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;



public class IconManager {
    
    
    public static final String ICON_TABLE = "\u25A0";        
    public static final String ICON_KANBAN = "\u268A";       
    public static final String ICON_LIST = "\u2261";         
    public static final String ICON_ADD = "\u271A";          
    public static final String ICON_REFRESH = "\u27F3";      
    public static final String ICON_DELETE = "\u2717";       
    public static final String ICON_EDIT = "\u270D";         
    public static final String ICON_CLOSE = "\u2715";        
    public static final String ICON_SEARCH = "\u26B2";       
    public static final String ICON_DASHBOARD = "\u2318";    
    public static final String ICON_PROJECT = "\u22A0";      
    public static final String ICON_ADMIN = "\u1F465";       
    public static final String ICON_CHECKMARK = "\u2713";    
    public static final String ICON_CROSSMARK = "\u274C";    
    
    

    public static void setButtonIcon(Button button, String icon) {
        button.setText(icon + " " + button.getText());
    }
    
    

    public static void setLabelIcon(Label label, String icon) {
        label.setText(icon + " " + label.getText());
    }
    
    

    public static String getIcon(String icon, int fontSize) {
        return icon;
    }
}
