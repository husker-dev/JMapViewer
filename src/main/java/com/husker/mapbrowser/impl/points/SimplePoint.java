package com.husker.mapbrowser.impl.points;

import javax.imageio.ImageIO;
import java.io.IOException;

public class SimplePoint extends IconPoint{

    public SimplePoint(double latitude, double longitude) {
        super(latitude, longitude);

        try {
            setIcon(ImageIO.read(getClass().getResource("/point_icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
