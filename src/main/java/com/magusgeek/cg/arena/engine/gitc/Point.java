package com.magusgeek.cg.arena.engine.gitc;

public class Point {

  public final int y;
  public final int x;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int distance(Point position) {
    return distance(position.x, position.y);
  }

  public int distance(int x2, int y2) {
    return (int)Math.sqrt((x2-x)*(x2-x) + (y2-y)*(y2-y));
  }

}
