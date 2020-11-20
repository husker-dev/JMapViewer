package com.husker.mapbrowser.impl.points;

import com.husker.mapbrowser.MapPoint;

import java.awt.*;
import java.awt.image.BufferedImage;

public class IconPoint extends MapPoint {

    private BufferedImage icon;

    public IconPoint(double latitude, double longitude) {
        super(latitude, longitude);
    }

    public void setIcon(BufferedImage icon){
        this.icon = icon;
    }

    public BufferedImage getIcon(){
        return icon;
    }

    public void draw(Graphics2D g2d){
        if(icon != null){
            Rectangle bounds = getBounds();
            g2d.drawImage(icon, bounds.x, bounds.y, bounds.width, bounds.height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(getX() - icon.getWidth() / 2, getY() - icon.getHeight(), icon.getWidth(), icon.getHeight());
    }
}
