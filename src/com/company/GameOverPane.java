package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameOverPane extends JPanel {

    private BufferedImage gameOver;

    public GameOverPane() {
        try {
            gameOver = ImageIO.read(getClass().getResourceAsStream("/przegranko.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        JButton button = new JButton("Jeszcze raz");
        this.add(button);
        int pad = (636-102)/2;
        layout.putConstraint(SpringLayout.SOUTH, button, -50, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.EAST, button, -pad, SpringLayout.EAST, this);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        int imgWidth = gameOver.getWidth();
        int imgHeight = gameOver.getHeight();
        int x = (width - imgWidth)/2;
        int y = (height - imgHeight)/2;
        g.drawImage(gameOver, x, y, null);
    }
}
