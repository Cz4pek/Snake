package com.company;

import java.awt.event.KeyEvent;
import java.util.*;

public class Snek {

    private int x;
    private int y;


    private int xSpeed = 0;
    private int ySpeed = 0;
    private Deque<Turn> turns = new LinkedList<>();

    public void init(Deque<Turn> turns){
        this.turns = new LinkedList<>(turns);
    }

    public int getX() {
        return x;
    }

    public int getxSpeed() {
        return xSpeed;
    }

    public int getySpeed() {
        return ySpeed;
    }

    public Deque<Turn> getTurns() {
        return turns;
    }

    public void offer(Turn turn) {
        turns.offer(turn);
    }

    public Turn peek() {
        return turns.peek();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snek snek = (Snek) o;
        return x == snek.getX() &&
                y == snek.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Turn poll() {
        return turns.poll();
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setxSpeed(int xSpeed) {
        this.xSpeed = xSpeed;
    }

    public void setySpeed(int ySpeed) {
        this.ySpeed = ySpeed;
    }

    public Snek(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void update(){
        x += xSpeed;
        y += ySpeed;
    }
}
