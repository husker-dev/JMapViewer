package com.husker.mapbrowser.impl.maps;

import com.husker.mapbrowser.Map;

public class OpenStreetMap extends Map {

    public OpenStreetMap() {
        super(0, 20);
    }

    public String getUrl(int zoom, int x, int y) {
        return "https://c.tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png";
    }
}
