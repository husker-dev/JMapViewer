package com.husker.mapbrowser;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapPoint {

    private final double latitude, longitude;
    private BufferedImage icon;

    public MapPoint(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setIcon(BufferedImage icon){
        this.icon = icon;
    }

    public BufferedImage getIcon(){
        return icon;
    }

    public void draw(Graphics2D g2d, int x, int y){
        if(icon != null) {
            g2d.drawImage(icon, x - icon.getWidth() / 2, y - icon.getHeight(), null);
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
