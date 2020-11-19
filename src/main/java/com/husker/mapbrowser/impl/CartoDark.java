package com.husker.mapbrowser.impl;

import com.husker.mapbrowser.Map;

public class CartoDark extends Map {

    public CartoDark() {
        super(0, 25);
    }

    public String getUrl(int zoom, int x, int y) {
        return "https://cartodb-basemaps-c.global.ssl.fastly.net/dark_all/" + zoom + "/" + x + "/" + y + ".png";
    }
}
