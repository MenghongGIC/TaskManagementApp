package com.taskmanagement.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.InputStream;



public class HeroiconsIconManager {
    
    private static final String ICONS_PATH = "/com/taskmanagement/icons/";
    private static final int DEFAULT_ICON_SIZE = 24;
    
    

    public enum Icon {
        CLIPBOARD("clipboard.svg"),
        PLUS("plus.svg"),
        REFRESH("refresh.svg"),
        ELLIPSIS("ellipsis.svg"),
        ARROW_LEFT("arrow-left.svg"),
        DASHBOARD("dashboard.svg"),
        FOLDER("folder.svg"),
        USER("user.svg"),
        COG("cog.svg");
        
        private final String fileName;
        
        Icon(String fileName) {
            this.fileName = fileName;
        }
        
        public String getFileName() {
            return fileName;
        }
    }
    
    

    public static ImageView loadIcon(Icon icon) {
        return loadIcon(icon, DEFAULT_ICON_SIZE);
    }
    
    

    public static ImageView loadIcon(Icon icon, int size) {
        try {
            InputStream inputStream = HeroiconsIconManager.class.getResourceAsStream(ICONS_PATH + icon.getFileName());
            if (inputStream == null) {
                System.err.println("❌ Icon not found: " + icon.getFileName());
                return createPlaceholder();
            }
            
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            return imageView;
        } catch (Exception e) {
            System.err.println("❌ Error loading icon: " + icon.getFileName());
            e.printStackTrace();
            return createPlaceholder();
        }
    }
    
    

    public static void setButtonIcon(Button button, Icon icon) {
        setButtonIcon(button, icon, DEFAULT_ICON_SIZE);
    }
    
    

    public static void setButtonIcon(Button button, Icon icon, int size) {
        try {
            ImageView imageView = loadIcon(icon, size);
            String text = button.getText();
            button.setGraphic(imageView);
            if (text != null && !text.isEmpty()) {
                button.setText(text);
            }
        } catch (Exception e) {
            System.err.println("❌ Error setting button icon: " + e.getMessage());
        }
    }
    
    

    public static void setLabelIcon(Label label, Icon icon) {
        setLabelIcon(label, icon, DEFAULT_ICON_SIZE);
    }
    
    

    public static void setLabelIcon(Label label, Icon icon, int size) {
        try {
            ImageView imageView = loadIcon(icon, size);
            String text = label.getText();
            label.setGraphic(imageView);
            if (text != null && !text.isEmpty()) {
                label.setText(text);
            }
        } catch (Exception e) {
            System.err.println("❌ Error setting label icon: " + e.getMessage());
        }
    }
    
    

    private static ImageView createPlaceholder() {
        ImageView placeholder = new ImageView();
        placeholder.setFitWidth(DEFAULT_ICON_SIZE);
        placeholder.setFitHeight(DEFAULT_ICON_SIZE);
        placeholder.setStyle("-fx-fill: #999;");
        return placeholder;
    }
    
    

    public static String getIconPath(Icon icon) {
        return ICONS_PATH + icon.getFileName();
    }
}
