package test;

import com.husker.mapbrowser.MapPanel;
import javax.swing.*;
import java.awt.*;


public class Main {

    public static void main(String[] args){
        JFrame frame = new JFrame("Map test");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new MapPanel());
        frame.setVisible(true);
    }

}
