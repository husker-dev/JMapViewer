package com.husker.mapbrowser.impl.points;

import com.husker.mapbrowser.MapPoint;

import java.awt.*;

public class CountPoint extends MapPoint {

    private int count = 100;

    public CountPoint(double latitude, double longitude) {
        super(latitude, longitude);
    }

    public void draw(Graphics2D g2d) {
        g2d.setFont(g2d.getFont().deriveFont(15f));

        Rectangle bounds = getBounds();
        int size = bounds.width;
        int size_small = (int)(size * 0.75d);

        g2d.setColor(Color.orange);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2d.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g2d.fillOval(bounds.x + (size - size_small) / 2, bounds.y + (size - size_small) / 2, size_small, size_small);

        int width = g2d.getFontMetrics().stringWidth(count + "");
        int height = g2d.getFontMetrics().getHeight();

        g2d.setColor(Color.black);
        g2d.drawString(count + "", bounds.x + bounds.width / 2 - width / 2, bounds.y + bounds.height / 2 + height / 4);
    }

    public void setCount(int count){
        this.count = count;
    }

    public Rectangle getBounds() {
        int size = 40;
        return new Rectangle(getX() - size / 2, getY() - size / 2, size, size);
    }
}
