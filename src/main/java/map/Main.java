package map;

import net.http.Get;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static map.Main.MapPanel.*;

public class Main {

    public static void main(String[] args){
        JFrame frame = new JFrame("Map test");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new MapPanel(PATTERN_OPEN_STREET_MAP));
        frame.setVisible(true);
    }

    public static class MapPanel extends JPanel{

        public static final String PATTERN_OPEN_STREET_MAP = "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png";
        public static final String PATTERN_CARTO_LIGHT = "https://cartodb-basemaps-c.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png";
        public static final String PATTERN_CARTO_DARK = "https://cartodb-basemaps-c.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png";

        private final int chunk_size = 128;
        private final int levels = 20;

        private double oldZoom = -1;
        private int oldX = -1;
        private int oldY = -1;

        private double to_zoom = 2;
        private double zoom = 0;
        private Point zoomPoint;
        private double x = 0;
        private double y = 0;

        private final ArrayList<ChunkLoader> loaders = new ArrayList<>();

        private String pattern = PATTERN_OPEN_STREET_MAP;

        public MapPanel(String pattern){
            this();
            setURLPattern(pattern);
        }

        public MapPanel(){
            new Timer().schedule(new TimerTask() {
                public void run() {
                    zoom += (to_zoom - zoom) / 10d;

                    if(Math.abs(zoom - to_zoom) < 0.000001) {
                        zoom = to_zoom;
                    }else{
                        updateZoom();
                        repaint();
                    }
                }
            }, 0, 10);
            addMouseWheelListener(new MouseAdapter() {
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if(e.getWheelRotation() < 0)
                        to_zoom += 0.1;
                    else
                        to_zoom -= 0.1;
                    to_zoom = Math.max(0, to_zoom);
                    to_zoom = Math.min(levels, to_zoom);

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

        public void setURLPattern(String pattern){
            this.pattern = pattern;
        }

        public void paint(Graphics g) {
            super.paint(g);
            updateTiles();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2d.setColor(new Color(0, 0, 0, 100));
            for(int level = Math.max(0, (int)zoom - 1); level < Math.max(1, zoom); level++){
                for(int x = getChunkX(level); x < getChunkX(level) + getChunkWidth(level); x++) {
                    for (int y = getChunkY(level); y < getChunkY(level) + getChunkHeight(level); y++) {
                        int r_x = (int) (this.x + x * getCurrentChunkSize(level));
                        int r_y = (int) (this.y + y * getCurrentChunkSize(level));
                        int mapSize = (int)Math.pow(2, level);

                        if(x >= 0 & x < mapSize && y >= 0 & y < mapSize) {
                            ChunkLoader loader = getChunkLoader(level, x, y);

                            if (loader != null && loader.getImage() != null)
                                g2d.drawImage(loader.getImage(), r_x - 1, r_y - 1, (int)getCurrentChunkSize(level) + 2, (int)getCurrentChunkSize(level) + 2, null);
                        }
                    }
                }
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
            for(int level = Math.max(0, (int)zoom - 1); level < Math.max(1, zoom); level++) {
                int mapSize = (int) Math.pow(2, level);

                for (int x = getChunkX(level); x < getChunkX(level) + getChunkWidth(level); x++) {
                    for (int y = getChunkY(level); y < getChunkY(level) + getChunkHeight(level); y++) {
                        if (x < 0 || x >= mapSize)
                            continue;
                        if (y < 0 || y >= mapSize)
                            continue;

                        if (getChunkLoader(level, x, y) == null)
                            loaders.add(new ChunkLoader(this, level, x, y));
                    }
                }
            }

            try {
                loaders.removeIf(loader -> !isVisibleChunk(loader));
            }catch (Exception ignored){
            }

        }

        private ChunkLoader getChunkLoader(int zoom, int x, int y){
            for(ChunkLoader loader : loaders)
                if(loader != null && loader.zoom == zoom && loader.x == x && loader.y == y)
                    return loader;
            return null;
        }

        private int getChunkX(int level){
            int startX = (int)(-x / getCurrentChunkSize(level));
            if(x > 0)
                startX -= 1;
            return startX;
        }

        private int getChunkY(int level){
            int startY = (int)(-y / getCurrentChunkSize(level));
            if(y > 0)
                startY -= 1;
            return startY;
        }

        private int getChunkWidth(int level){
            return (int)((float)getWidth() / getCurrentChunkSize(level)) + 2;
        }

        private int getChunkHeight(int level){
            return (int)((float)getHeight() / getCurrentChunkSize(level)) + 2;
        }

        private double getCurrentChunkSize(int level){
            return chunk_size * Math.pow(2, zoom) / Math.pow(2, level);
        }

        private boolean isVisibleChunk(ChunkLoader loader){
            if(loader == null || !loaders.contains(loader))
                return false;
            return isVisibleChunk(loader.zoom, loader.x, loader.y);
        }

        private boolean isVisibleChunk(int zoom, int x, int y){
            boolean onScreen = x >= getChunkX(zoom) && y >= getChunkY(zoom) && x < getChunkX(zoom) + getChunkWidth(zoom) && y < getChunkY(zoom) + getChunkHeight(zoom);
            if(!onScreen)
                return false;
            if(zoom == (int)this.zoom)
                return true;
            if(zoom == (int)this.zoom - 1){
                ChunkLoader[] child = new ChunkLoader[]{
                        getChunkLoader(zoom + 1, x * 2, y * 2),
                        getChunkLoader(zoom + 1, x * 2 + 1, y * 2),
                        getChunkLoader(zoom + 1, x * 2, y * 2 + 1),
                        getChunkLoader(zoom + 1, x * 2 + 1, y * 2 + 1)
                };
                boolean allVisible = true;
                for(ChunkLoader loader : child)
                    if (loader == null || loader.getImage() == null) {
                        allVisible = false;
                        break;
                    }
                return !allVisible;
            }

            return false;
        }


        private static class ChunkLoader {

            private BufferedImage image;
            private static final List<ChunkLoader> loadingQueue = Collections.synchronizedList(new ArrayList<ChunkLoader>());

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
                                ChunkLoader loader = loadingQueue.remove(0);
                                if(loader == null)
                                    continue;
                                if (loader.panel.isVisibleChunk(loader)) {
                                    loader.load();
                                    loader.panel.repaint();
                                }else
                                    loader.panel.loaders.remove(loader);
                            }catch (Exception ignored){
                            }
                        }
                    }
                }).start();
            }

            private final int zoom, x, y;
            private final MapPanel panel;

            public ChunkLoader(MapPanel panel, int zoom, int x, int y){
                this.zoom = zoom;
                this.x = x;
                this.y = y;
                this.panel = panel;

                loadingQueue.add(this);
            }

            public void load() throws Exception{
                image = loadImageFromURL(panel.pattern.replace("{x}", x + "").replace("{y}", y + "").replace("{z}", zoom + ""));
            }

            public BufferedImage getImage(){
                return image;
            }

            private static BufferedImage loadImageFromURL(String url) throws Exception {
                URLConnection hc = new URL(url).openConnection();
                hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                return ImageIO.read(hc.getInputStream());
            }

            public String toString() {
                return "ChunkLoader{" +
                        "zoom=" + zoom +
                        ", x=" + x +
                        ", y=" + y +
                        '}';
            }
        }
    }
}
