package com.husker.mapbrowser;

import java.awt.*;

public abstract class Map {

    private final int minZoom, maxZoom;

    public Map(int minZoom, int maxZoom){
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
    }

    public int getMinimumZoom(){
        return minZoom;
    }

    public int getMaximumZoom(){
        return maxZoom;
    }

    public abstract String getUrl(int zoom, int x, int y);

    public Point getCoordinates(double lat, double lon, double size){
        return new Point((int)WGS84.lon2merc(lon, size), (int)WGS84.lat2merc(lat, size));
    }
}
