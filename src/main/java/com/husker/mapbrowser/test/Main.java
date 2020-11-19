package com.husker.mapbrowser.test;


import com.husker.mapbrowser.MapPanel;
import com.husker.mapbrowser.MapPoint;
import com.husker.mapbrowser.impl.BingMaps;
import com.husker.mapbrowser.impl.CartoDark;


import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;



public class Main {

    public static void main(String[] args){
        System.out.println(BingMaps.toQuad(0, 0, 1));

        JFrame frame = new JFrame("Map test");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        try {
            frame.getContentPane().add(new MapPanel(){{
                //setMap(new BingMaps("AsqSIFnmq7z1cczzNHRBYozTe8idTSkNTC7ZweewIsuyWyJb9VPgzDBXv4a3F4X8"));
                setMap(new CartoDark());

                addPoint(new MapPoint(64.586185, 30.613007) {{
                    try {
                        setIcon(ImageIO.read(getClass().getResource("/point_icon.png")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }});
                setSmoothZoom(false);
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setVisible(true);

        new Timer().schedule(new TimerTask() {

            public void run() {
                //System.gc();
                //System.out.println("RAM: " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " Mb");
            }
        }, 0, 100);
    }

}
