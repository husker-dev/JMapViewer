package com.husker.mapbrowser.impl;

import com.husker.mapbrowser.Map;

public class OpenStreetMap extends Map {

    public OpenStreetMap() {
        super(0, 16);
    }

    public String getUrl(int zoom, int x, int y) {
        return "https://b.tile.openstreetmap.org/" + zoom + "/" + x + "/" + y + ".png";
    }
}
