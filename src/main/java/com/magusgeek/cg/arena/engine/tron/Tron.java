package com.magusgeek.cg.arena.engine.tron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.magusgeek.cg.arena.GameResult;
import com.magusgeek.cg.arena.engine.Engine;

public class Tron extends Engine {
    private static final Log LOG = LogFactory.getLog(Tron.class);

    private static final int W = 30;
    private static final int H = 20;

    private int[][] map = new int[H][W];
    private List<Player> players;
    private int playersCount;

    protected void initialize() throws Exception {
        for (int y = 0; y < H; ++y) {
            for (int x = 0; x < W; ++x) {
                map[y][x] = -1;
            }
        }

        players = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < process.size(); ++i) {
            int x, y;

            boolean free = true;
            do {
                x = random.nextInt(W);
                y = random.nextInt(H);

                for (int j = 0; free && j < players.size(); ++j) {
                    Player p = players.get(j);
                    free = p.getX() != x || p.getY() != y;
                }
            } while (!free);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Player " + (i + 1) + " position : " + x + " " + y);
            }

            players.add(new Player(process.get(i), i, x, y));
            map[y][x] = i;
        }

        playersCount = players.size();
    }

    private void kill(Player player) {
        player.setDead(true);
        playersCount -= 1;

        // If we have only 1 player left, it's the winner !
        if (playersCount == 1) {
            int id = players.stream().filter(p -> !p.isDead()).findFirst().get().getId();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Winner : " + id);
            }

            destroyAll();
            end = true;
            result = new GameResult(id);
        } else {
            // Clean the map
            for (int y = 0; y < H; ++y) {
                for (int x = 0; x < W; ++x) {
                    if (map[y][x] == player.getId()) {
                        map[y][x] = -1;
                    }
                }
            }
        }
    }

    protected void play() throws Exception {
        final boolean debug = LOG.isDebugEnabled();

        for (Player player : players) {
            if (player.isDead()) {
                continue;
            }

            player.getOut().println(players.size() + " " + player.getId());
            for (Player p : players) {
                player.getOut().println(p.getStartX() + " " + p.getStartY() + " " + p.getX() + " " + p.getY());
            }
            player.getOut().flush();

            player.cleanErrorStream();

            try {
                Direction dir = Direction.valueOf(player.getIn().nextLine());
                Point next = player.next(dir);
                int x = next.getX();
                int y = next.getY();

                if (x < 0 || x > W || y < 0 || y > H) {
                    if (debug) {
                        LOG.debug("Player " + (player.getId() + 1) + " dead at turn " + turn + " for moving outside of the map");
                    }

                    kill(player);
                    continue;
                }

                if (map[y][x] != -1) {
                    if (debug) {
                        LOG.debug("Player " + (player.getId() + 1) + " dead at turn " + turn + " for moving on a wall");
                    }

                    kill(player);
                    continue;
                }

                map[y][x] = player.getId();
                player.move(x, y);
            } catch (IllegalArgumentException exception) {
                if (debug) {
                    LOG.debug("Player " + (player.getId() + 1) + " dead at turn " + turn + " for throwing a bad response");
                }

                kill(player);
            }

            if (end) {
                break;
            }
        }
    }

}
