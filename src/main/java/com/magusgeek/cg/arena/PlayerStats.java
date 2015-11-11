package com.magusgeek.cg.arena;

public class PlayerStats {

    private int[] stats = new int[4];
    
    public PlayerStats() {
        for (int i = 0; i < 4; ++i) {
            stats[i] = 0;
        }
    }
    
    public void add(int position) {
        stats[position] += 1;
    }
    
    public int[] getStats() {
        return stats;
    }
    
    public String toString() {
        String result = " | ";
        
        for (int i = 0; i < 4; ++i) {
            result += " Position " + (i + 1) + " : " + stats[i] + " | ";
        }
        
        return result;
    }
}
