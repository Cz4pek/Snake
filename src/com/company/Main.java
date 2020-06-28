package com.company;

import javax.swing.*;
import java.awt.*;

/**
 * @author Cezary Płatek
 */
public class Main extends JFrame {

    /**
     *
     * @param title  windows title
     * @throws HeadlessException when title is not specified
     */
    public Main(String title) throws HeadlessException {
        super(title);
        Panel panel = new Panel(this);
        this.setContentPane(panel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    public static void main(String[] args) {
        Main frame = new Main("Snek");
        frame.pack();
        frame.setVisible(true);
    }

}

