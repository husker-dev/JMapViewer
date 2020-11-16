package com.husker.mapbrowser.test;


import com.husker.mapbrowser.MapPanel;

import javax.swing.*;
import java.awt.*;

import static com.husker.mapbrowser.MapPanel.*;


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

}
