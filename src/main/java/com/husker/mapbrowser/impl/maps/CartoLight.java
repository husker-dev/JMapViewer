package com.husker.mapbrowser.impl.maps;

import com.husker.mapbrowser.Map;

public class CartoLight extends Map {

    public CartoLight() {
        super(0, 20);
    }

    public String getUrl(int zoom, int x, int y) {
        return "https://cartodb-basemaps-c.global.ssl.fastly.net/light_all/" + zoom + "/" + x + "/" + y + ".png";
    }
}
