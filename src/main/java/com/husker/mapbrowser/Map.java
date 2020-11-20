package com.husker.mapbrowser;

import java.awt.geom.Point2D;

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

    public Point2D.Double getCoordinates(double lat, double lon){
        return new Point2D.Double(WGS84.lon2merc(lon), WGS84.lat2merc(lat));
    }

    public Point2D.Double toMerc(double x, double y){
        return new Point2D.Double(WGS84.merc2lat(y), WGS84.merc2lon(x));
    }
}
