package com.magusgeek.cg.arena.engine.tron;

import com.magusgeek.cg.arena.engine.PlayerBase;

public class Player extends PlayerBase {

    private int y;
    private int x;
    private int startX;
    private int startY;
    private boolean dead;

    public Player(Process process, int id, int x, int y) {
        super(process, id);

        this.x = x;
        this.y = y;
        startX = x;
        startY = y;

        dead = false;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point next(Direction dir) {
        switch (dir) {
        case LEFT:
            return new Point(x - 1, y);
        case RIGHT:
            return new Point(x + 1, y);
        case UP:
            return new Point(x, y - 1);
        case DOWN:
            return new Point(x, y + 1);
        }

        // Logic prevent this
        return null;
    }

}