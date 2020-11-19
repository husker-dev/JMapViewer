package com.husker.mapbrowser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chunk {
    private BufferedImage image;
    private static final List<Chunk> loadingQueue = Collections.synchronizedList(new ArrayList<Chunk>());

    static {
        for(int i = 0; i < Runtime.getRuntime().availableProcessors(); i++)
            startLoader();
    }

    private static void startLoader(){
        new Thread(() -> {
            while(true){
                if(loadingQueue.size() == 0){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {}
                }else {
                    try {
                        Chunk chunk = loadingQueue.remove(0);
                        if(chunk == null)
                            continue;
                        if (chunk.isValid()) {
                            //System.out.println("Loaded: " + chunk);
                            chunk.load();
                            chunk.panel.repaint();
                        }
                    }catch (Exception ignored){

                    }
                }
            }
        }).start();
    }

    private static BufferedImage loadImageFromURL(String url) throws Exception {
        URLConnection hc = new URL(url).openConnection();
        hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        return ImageIO.read(hc.getInputStream());
    }

    private final int zoom, x, y;
    private final MapPanel panel;
    private double alpha = 0;

    public Chunk(MapPanel panel, int zoom, int x, int y){
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.panel = panel;

        loadingQueue.add(this);
    }

    public void load() throws Exception{
        image = loadImageFromURL(panel.getMap().getUrl(zoom, x, y));
    }

    public BufferedImage getImage(){
        return image;
    }

    public int getZoom(){
        return zoom;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean isValid(){
        try {
            if (!panel.getChunks().contains(this) || !panel.isOnScreen(this))
                return false;
            long curZoom = panel.getCurrentZoom();

            if (zoom == curZoom)
                return true;

            if (zoom > curZoom) {
                if (getImage() == null)
                    return false;

                Chunk chunk = this;
                while ((chunk = chunk.getParent()) != null)
                    if (chunk.getImage() != null && chunk.getAlpha() == 1)
                        return false;
                return true;
            }

            if (zoom < curZoom)
                return !areChildrenLoaded();
        }catch (Exception ex){
        }
        return false;
    }

    boolean areChildrenLoaded(){
        if(zoom + 1 == panel.getCurrentZoom()){
            // Last layer
            for(Chunk child : getChildrenChunks())
                if(panel.isOnScreen(child) && (child.getImage() == null || child.getAlpha() < 1))
                    return false;
        }else{
            for(Chunk child : getChildrenChunks())
                if(panel.isOnScreen(child) && !child.areChildrenLoaded())
                    return false;
        }
        return true;
    }

    public void addAlpha(double alpha){
        if(getImage() == null)
            return;
        this.alpha += alpha;
        if(this.alpha > 1)
            this.alpha = 1;
    }


    public double getAlpha(){
        return alpha;
    }

    public Chunk getParent(){
        if(zoom == panel.getMinimumZoom())
            return null;
        int x = this.x - this.x % 2;
        int y = this.y - this.y % 2;
        return getOrCreate(zoom - 1, x / 2, y / 2);
    }

    public Chunk[] getChildrenChunks(){
        if(zoom == panel.getMaximumZoom())
            return null;
        int x = this.x * 2;
        int y = this.y * 2;
        return new Chunk[]{
                getOrCreate(zoom + 1, x, y),
                getOrCreate(zoom + 1, x + 1, y),
                getOrCreate(zoom + 1, x, y + 1),
                getOrCreate(zoom + 1, x + 1, y + 1)
        };
    }

    private Chunk getOrCreate(int zoom, int x, int y){
        Chunk chunk = panel.getChunk(zoom, x, y);
        if(chunk == null)
            chunk = new Chunk(panel, zoom, x, y);
        return chunk;
    }

    public String toString() {
        return "ChunkLoader{" +
                "zoom=" + zoom +
                ", x=" + x +
                ", y=" + y +
                ", loaded=" + (getImage() != null) +
                '}';
    }
}
