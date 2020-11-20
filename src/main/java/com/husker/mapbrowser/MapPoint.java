package com.husker.mapbrowser;

import java.awt.*;

public class MapPoint {

    private final double latitude, longitude;
    int x, y;

    public MapPoint(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void draw(Graphics2D g2d){
        Rectangle bounds = getBounds();
        g2d.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds(){
        int size = 20;
        return new Rectangle(x - size / 2, y - size / 2, size, size);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
