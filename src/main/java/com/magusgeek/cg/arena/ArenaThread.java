package com.magusgeek.cg.arena;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.magusgeek.cg.arena.engine.Engine;
import com.magusgeek.cg.arena.util.Mutable;

public class ArenaThread extends Thread {
    private static final Log LOG = LogFactory.getLog(ArenaThread.class);

    private int id;
    private List<String> commandLines;
    private Mutable<Integer> count;
    private PlayerStats[] playerStats;
    private int n;
    private Class<Engine> clazz;

    public ArenaThread(int id, List<String> commandLines, Mutable<Integer> count, PlayerStats[] playerStats, Class<Engine> clazz, int n) {
        this.id = id;
        this.commandLines = commandLines;
        this.count = count;
        this.playerStats = playerStats;
        this.n = n;
        this.clazz = clazz;
    }

    @Override
    public void run() {
        while (true) {
            int game = 0;
            synchronized (count) {
                if (count.getValue() < n) {
                    game = count.getValue() + 1;
                    count.setValue(game);
                }
            }

            if (game == 0) {
                // End of the program
                break;
            }

            try {
                Engine engine = clazz.newInstance();
                engine.setCommandLines(commandLines);
                GameResult result = engine.start();

                List<Integer> positions = result.getPositions();
                Collections.reverse(positions);

                synchronized (playerStats) {
                    for (int j = 0; j < positions.size(); ++j) {
                        playerStats[positions.get(j)].add(j);
                    }
                }

                LOG.info("Thread " + id + " : end of the game " + game);

                if (game == n) {
                    for (int i = 0; i < playerStats.length; ++i) {
                        LOG.info("Player " + (i + 1) + " stats : " + playerStats[i]);
                    }
                }
            } catch (Exception exception) {
                LOG.error("Exception in an ArenaThread " + id, exception);
            }
        }
    }

}
