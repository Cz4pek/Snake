package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Cezary Płatek
 */
public class Panel extends JPanel {

    private Snek head;
    private Listener listener;
    private JPanel gameOverPane;
    private JButton resetButton;
    private boolean initianl = true;
    private int level = 1;

    private int heightAndWidth;
    private int segmentWidthAndHeight;
    private int boardSegments;


    private BufferedImage board;
    private BufferedImage snekSegment;
    private BufferedImage snekHead;
    private BufferedImage pointImg;

    private ArrayList<Snek> segments = new ArrayList<>(100);
    private Point point;


    private Timer timer;
    private int delay = 150;

    /**
     * Sets basic application properties, creates necessary objects to run the game
     * @param frame target frame
     *
     */
    public Panel(JFrame frame) {
        this.loadimages();
        heightAndWidth = board.getWidth();
        segmentWidthAndHeight = snekSegment.getWidth();
        boardSegments = heightAndWidth / segmentWidthAndHeight;
        this.setPreferredSize(new Dimension(heightAndWidth, heightAndWidth));
        double xLocation = (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - heightAndWidth) / 2;
        double yLocation = (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - heightAndWidth) / 2;
        frame.setLocation((int) xLocation, (int) yLocation);
        gameOverPane = new GameOverPane();
        frame.setGlassPane(gameOverPane);
        resetButton = (JButton) gameOverPane.getComponent(0);
        setUpSegments();
        point = new Point();
        setPointsLocation();
        listener = new Listener();
        resetButton.addActionListener((ActionEvent e) -> {
            reset();
            frame.requestFocus();
        });
        frame.addKeyListener(listener);

        timer = new Timer(delay, listener);
        timer.start();
    }

    /**
     * Sets 5 initial snake segments including head in the middle of the pane
     */
    private void setUpSegments() {
        head = new Snek(segmentWidthAndHeight * (boardSegments + 1) / 2, segmentWidthAndHeight * (boardSegments + 1) / 2);
        segments.add(head);
        for (int i = 1; i < 5; i++) {
            segments.add(new Snek(head.getX() - segmentWidthAndHeight * i, head.getY()));
        }
    }

    /**
     * loads necessary images form res folder
     */
    private void loadimages() {
        try {
            board = ImageIO.read(getClass().getResourceAsStream("/board2.png"));
            snekSegment = ImageIO.read(getClass().getResourceAsStream("/snek_segment.png"));
            snekHead = ImageIO.read(getClass().getResourceAsStream("/snek_head.png"));
            pointImg = ImageIO.read(getClass().getResourceAsStream("/point.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Picks new coordinates for point object
     */
    private void setPointsLocation() {
        point.setX((int) (Math.random() * boardSegments) * segmentWidthAndHeight);
        point.setY((int) (Math.random() * boardSegments) * segmentWidthAndHeight);
        for (Snek s : segments) {
            if (s.getX() == point.getX() && s.getY() == point.getY()) setPointsLocation();
        }

    }

    private void reset(){
        segments.clear();
        setUpSegments();
        setPointsLocation();
        timer.setDelay(150);
        level = 1;
        initianl = true;
        gameOverPane.setVisible(false);
        timer.start();
    }

    /**
     * Overridden method, prints the board, the snake, the point and the statistics
     * @param g inherited from JPanel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(board, 0, 0, null);
        g2.drawImage(snekHead, head.getX(), head.getY(), null);
        for (int i = 1; i < segments.size(); i++) {
            Snek s = segments.get(i);
            g2.drawImage(snekSegment, s.getX(), s.getY(), null);
        }
        g2.drawImage(pointImg, point.getX(), point.getY(), null);
        g2.setStroke(new BasicStroke(4.0f));
        g2.setFont(new Font("TimesRoman", Font.BOLD, 20));
        g2.drawString("Długość: " + segments.size(), 20, 20);
        g2.drawString("Poziom: " + level, 20, 40);

    }


    /**
     * Handles keyPressed action, creates new Turn objects and adds it to the turn queue in each segment.
     * Turn is added only if there is no other Turn object with the same x and y coordinates in the segments queue
     * @param x speed to be set in turn in x axis
     * @param y speed to be set in turn in y axis
     */
    private void arrowKeyPressed(int x, int y) {
        if (initianl) {
            for (Snek s : segments) {
                s.setxSpeed(segmentWidthAndHeight);
                initianl = false;
            }
        } else {
            Turn turn = new Turn(head.getX(), head.getY(), x, y);
            Snek s = segments.get(segments.size() - 1);
            if (!turn.equals(s.getTurns().peekLast())) {
                head.setxSpeed(x);
                head.setySpeed(y);
                for (int i = 1; i < segments.size(); i++) {
                    Snek senk = segments.get(i);
                    senk.offer(turn);
                }
            }
            else{
                turn.setX(turn.getX()+head.getxSpeed());
                turn.setY(turn.getY()+head.getySpeed());
                for (Snek senk : segments) {
                    senk.offer(turn);
                }
            }
        }

    }


    private void checkForTurns() {
        for (int i = segments.size() - 1; i >= 0; i--) {
            if (!segments.get(i).getTurns().isEmpty() && segments.get(i).getX() == segments.get(i).peek().getX() && segments.get(i).getY() == segments.get(i).peek().getY()) {
                Turn tempTrun = segments.get(i).poll();
                segments.get(i).setxSpeed(tempTrun.getxSpeed());
                segments.get(i).setySpeed(tempTrun.getySpeed());
            }
        }
    }

    private void checkForCollision() {
        for (int i = 1; i < segments.size(); i++) {
            if (segments.get(i).equals(head)) handleCollision();
        }
    }

    private void checkForPoint() {
        if (point.getX() == head.getX() && point.getY() == head.getY()) {
            Snek tail = segments.get(segments.size() - 1);
            int x = 0;
            int y = 0;
            if (tail.getxSpeed() == segmentWidthAndHeight) {
                x = tail.getX() - segmentWidthAndHeight;
                y = tail.getY();
            } else if (tail.getxSpeed() == -segmentWidthAndHeight) {
                x = tail.getX() + segmentWidthAndHeight;
                y = tail.getY();
            } else if (tail.getySpeed() == segmentWidthAndHeight) {
                x = tail.getX();
                y = tail.getY() - segmentWidthAndHeight;
            } else if (tail.getySpeed() == -segmentWidthAndHeight) {
                x = tail.getX();
                y = tail.getY() + segmentWidthAndHeight;
            }
            Snek addedSegment = new Snek(x, y);
            addedSegment.setySpeed(tail.getySpeed());
            addedSegment.setxSpeed(tail.getxSpeed());
            addedSegment.init(tail.getTurns());
            segments.add(addedSegment);
            setPointsLocation();
        }
    }

    private void handleCollision() {
        segments.forEach((Snek s) -> {
            s.setySpeed(0);
            s.setxSpeed(0);
            timer.stop();
            gameOverPane.setVisible(true);
        });

    }

    private void handleWallCollision(Snek s) {
        if (s.getX() == heightAndWidth) s.setX(0);
        if (s.getX() == -segmentWidthAndHeight) s.setX(heightAndWidth);
        if (s.getY() == heightAndWidth) s.setY(0);
        if (s.getY() == -segmentWidthAndHeight) s.setY(heightAndWidth);
    }

    private void levels() {
        switch (segments.size()) {
            case 15:
                timer.setDelay(125);
                level = 2;
                break;
            case 25:
                timer.setDelay(100);
                level = 3;
                break;
            case 35:
                timer.setDelay(75);
                level = 4;
                break;
            case 45:
                timer.setDelay(50);
                level = 5;
                break;
        }
    }



    private class Listener implements KeyListener, ActionListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    if (head.getySpeed() != 0) return;
                   arrowKeyPressed(0, -segmentWidthAndHeight);
                    break;
                case KeyEvent.VK_DOWN:
                    if (head.getySpeed() != 0) return;
                    arrowKeyPressed(0, segmentWidthAndHeight);
                    break;
                case KeyEvent.VK_LEFT:
                    if (head.getxSpeed() != 0) return;
                    arrowKeyPressed(-segmentWidthAndHeight, 0);
                    break;
                case KeyEvent.VK_RIGHT:
                    if (head.getxSpeed() != 0) return;
                    arrowKeyPressed(segmentWidthAndHeight, 0);
                    break;
                case KeyEvent.VK_R:
                    reset();
                    break;
            }

            repaint();

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Snek s : segments) {
                s.update();
                handleWallCollision(s);

            }
            checkForTurns();
            checkForCollision();
            checkForPoint();
            levels();
            repaint();
        }
    }
}

