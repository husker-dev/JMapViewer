package com.husker.mapbrowser.impl.maps;

import com.husker.mapbrowser.Map;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class BingMaps extends Map {

    private String pattern;

    public BingMaps(String key) throws IOException {
        super(0, 20);

        URLConnection hc = new URL("http://dev.virtualearth.net/REST/V1/Imagery/Metadata/RoadOnDemand?output=json&include=ImageryProviders&key=" + key).openConnection();
        hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        Scanner s = new Scanner(hc.getInputStream()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";

        pattern = result.split("\"imageUrl\":\"")[1].split("\",")[0].replace("\\/", "/").replace("{subdomain}", "t0");
    }

    public String getUrl(int zoom, int x, int y) {
        return pattern.replace("{quadkey}", toQuad(x, y, zoom - 1));
    }

    public static String toQuad(int x, int y, int z) {
        StringBuilder quadkey = new StringBuilder();
        for (int i = z; i >= 0; --i) {
            int bitmask = 1 << i;
            int digit = 0;
            if ((x & bitmask) != 0)
                digit |= 1;
            if ((y & bitmask) != 0)
                digit |= 2;
            quadkey.append(digit);
        }
        return quadkey.toString();
    };
}
