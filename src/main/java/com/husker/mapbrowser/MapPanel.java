package com.husker.mapbrowser;


import com.husker.mapbrowser.impl.maps.OpenStreetMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

public class MapPanel extends JPanel {

    private final int chunk_size = 256;

    private double oldZoom = -1;
    private int oldX = -1;
    private int oldY = -1;

    private double to_zoom = 2;
    private double zoom = 0;
    private Point zoomPoint;
    private double x = 0;
    private double y = 0;

    private final List<Chunk> chunks = Collections.synchronizedList(new ArrayList<Chunk>());
    private final ArrayList<MapPoint> points = new ArrayList<>();

    private boolean smoothZoom = false;

    private Map map = new OpenStreetMap();

    public MapPanel(Map map){
        this();
        setMap(map);
    }

    public MapPanel(){
        new java.util.Timer().schedule(new TimerTask() {
            long lastTime = System.currentTimeMillis();
            public void run() {
                long currentTime = System.currentTimeMillis();
                int delta = (int)(currentTime - lastTime);
                lastTime = currentTime;

                try {
                    for (Chunk chunk : chunks)
                        chunk.addAlpha(delta / 200d);
                } catch (Exception ignored) {
                }

                zoom += (to_zoom - zoom) / 10d;

                if (Math.abs(zoom - to_zoom) < 0.01)
                    zoom = to_zoom;

                updateZoom();
                repaint();
            }
        }, 0, 10);
        addMouseWheelListener(new MouseAdapter() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() < 0)
                    to_zoom += smoothZoom ? 0.1 : 1;
                else
                    to_zoom -= smoothZoom ? 0.1 : 1;
                to_zoom = Math.max(map.getMinimumZoom(), to_zoom);
                to_zoom = Math.min(map.getMaximumZoom() - 1, to_zoom);

                Point point = getMousePosition();
                if(point != null)
                    zoomPoint = point;

                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    to_zoom += 1;
                    Point point = getMousePosition();
                    if(point != null)
                        zoomPoint = point;
                    repaint();
                }
            }

            public void mousePressed(MouseEvent e) {
                oldX = e.getX();
                oldY = e.getY();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                x += e.getX() - oldX;
                y += e.getY() - oldY;

                oldX = e.getX();
                oldY = e.getY();
                repaint();
            }
        });
    }

    public List<Chunk> getChunks(){
        return chunks;
    }

    public void setMap(Map map){
        this.map = map;
        repaint();
    }

    public Map getMap(){
        return map;
    }

    public void setSmoothZoom(boolean smoothZoom){
        this.smoothZoom = smoothZoom;
    }

    public void addPoint(MapPoint point){
        points.add(point);
        repaint();
    }

    public Point2D.Double getMouseMapPoint(){
        Point mouse = getMousePosition();
        if(mouse == null)
            return null;
        double x = -(this.x - mouse.x) / getChunkSize(0);
        double y = -(this.y - mouse.y) / getChunkSize(0);

        return map.toMerc(x, y);
    }

    public void paint(Graphics g) {
        super.paint(g);

        updateTiles();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(0, 0, 0, 100));
        for (int level = map.getMinimumZoom(); level < map.getMaximumZoom(); level++) {
            try {
                for (Chunk loader : chunks) {
                    if (loader.getZoom() == level && loader.getImage() != null) {
                        // BigDecimal used for accurate calculations
                        int r_x = new SimpleBigDecimal(loader.getX()).multiply(getChunkSize(level)).add(this.x).intValue();
                        int r_y = new SimpleBigDecimal(loader.getY()).multiply(getChunkSize(level)).add(this.y).intValue();

                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)loader.getAlpha()));
                        g2d.drawImage(loader.getImage(), r_x, r_y, (int) getChunkSize(level) + 1, (int) getChunkSize(level) + 1, null);
                    }
                }
            } catch (Exception ignored) { }
        }

        g2d.setColor(Color.black);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        for(MapPoint point : points) {
            Point2D.Double coordinates = map.getCoordinates(point.getLatitude(), point.getLongitude());
            point.x = (int)(coordinates.x * getChunkSize(0) + this.x);
            point.y = (int)(coordinates.y * getChunkSize(0) + this.y);

            point.draw(g2d);
        }
    }

    private void updateZoom(){
        if(zoomPoint != null) {

            double oldChunkSize = chunk_size * Math.pow(2, oldZoom) / Math.pow(2, zoom);
            double chunkSize = chunk_size;

            double percentX = (x - zoomPoint.x) / oldChunkSize;
            double percentY = (y - zoomPoint.y) / oldChunkSize;

            x = zoomPoint.x + chunkSize * percentX;
            y = zoomPoint.y + chunkSize * percentY;
        }

        oldZoom = zoom;
    }

    private void updateTiles(){
        try {
            int zoom = getCurrentZoom();

            int centerX = (int)(getChunkX(zoom) + getChunkWidth(zoom) / 2d);
            int centerY = (int)(getChunkY(zoom) + getChunkHeight(zoom) / 2d);

            for(double radius = 0; radius < Math.max(getChunkWidth(zoom), getChunkHeight(zoom)) / 2d + 1; radius += 0.3) {
                for (double angle = 0; angle < Math.PI * 2; angle += 0.1) {
                    int x = (int)(centerX + radius * Math.cos(angle));
                    int y = (int)(centerY + radius * Math.sin(angle));

                    if(y < 0 || y >= Math.pow(2, zoom))
                        continue;
                    if(x < 0 || x >= Math.pow(2, zoom))
                        continue;

                    if(isOnScreen(zoom, x, y) && getChunk(zoom, x, y) == null)
                        chunks.add(new Chunk(this, zoom, x, y));
                }
            }

            for (int level = map.getMaximumZoom(); level >= map.getMinimumZoom(); level--) {
                final int LEVEL = level;
                try {
                    chunks.removeIf(chunk -> chunk.getZoom() == LEVEL && !chunk.isValid());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

        }catch (Exception ignored){ }
    }

    public int getMinimumZoom(){
        return map.getMinimumZoom();
    }

    public int getMaximumZoom(){
        return map.getMaximumZoom();
    }

    public int getCurrentZoom(){
        int zoom = Math.max(0, (int) this.zoom);
        if(!smoothZoom && to_zoom > this.zoom)
            zoom = zoom + 1;
        return zoom;
    }

    Chunk getChunk(int zoom, int x, int y){
        for(Chunk loader : chunks)
            if(loader != null && loader.getZoom() == zoom && loader.getX() == x && loader.getY() == y)
                return loader;
        return null;
    }

    private int getChunkX(int level){
        int startX = (int)(-x / getChunkSize(level));
        if(x > 0)
            startX -= 1;
        return startX;
    }

    private int getChunkY(int level){
        int startY = (int)(-y / getChunkSize(level));
        if(y > 0)
            startY -= 1;
        return startY;
    }

    private int getChunkWidth(int level){
        return (int)((float)getWidth() / getChunkSize(level)) + 2;
    }

    private int getChunkHeight(int level){
        return (int)((float)getHeight() / getChunkSize(level)) + 2;
    }

    private double getChunkSize(int level){
        return chunk_size * Math.pow(2, zoom) / Math.pow(2, level);
    }

    private boolean isOnScreen(int zoom, int x, int y){
        return x >= getChunkX(zoom) && y >= getChunkY(zoom) && x < getChunkX(zoom) + getChunkWidth(zoom) && y < getChunkY(zoom) + getChunkHeight(zoom);
    }

    boolean isOnScreen(Chunk chunk){
        return isOnScreen(chunk.getZoom(), chunk.getX(), chunk.getY());
    }

    private static class SimpleBigDecimal extends BigDecimal{

        public SimpleBigDecimal(double val) {
            super(val);
        }

        public SimpleBigDecimal(BigDecimal bigDecimal) {
            super(bigDecimal.toPlainString());
        }

        public SimpleBigDecimal add(double d){
            return new SimpleBigDecimal(super.add(BigDecimal.valueOf(d)));
        }

        public SimpleBigDecimal multiply(double d){
            return new SimpleBigDecimal(super.multiply(BigDecimal.valueOf(d)));
        }
    }

}
