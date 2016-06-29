package com.magusgeek.cg.arena.engine.codeBusters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.magusgeek.cg.arena.engine.Engine;
import com.magusgeek.cg.arena.engine.PlayerBase;
import com.magusgeek.cg.arena.engine.codeBusters.Game.Buster;
import com.magusgeek.cg.arena.engine.codeBusters.Game.Ghost;
import com.magusgeek.cg.arena.engine.tron.Tron;

public class CodeBusters extends Engine {

	private static final Log LOG = LogFactory.getLog(Tron.class);

	private PlayerBase[] players = new PlayerBase[2];
	
	private Game game;

	@Override
	protected void play() throws Exception {
		for (int turn = 0; turn < 200; turn ++) {

			List<Order> loop0 = read(game, players[0], 0);
			List<Order> loop1 = read(game, players[0], 1);

			List<Order> loop = new ArrayList<>();
			loop.addAll(loop0);
			loop.addAll(loop1);
			
			game.apply(loop);
			
			System.out.println("TURN  ---- " + turn +" -----");
		}
	}
	
	private List<Order> read(Game g, PlayerBase w, int teamId) throws IOException {
		
		w.cleanErrorStream();
		 
		Set<Object> set = new HashSet<>();
		for (Buster b : g.busters) {
			if (b.entityType == teamId) {
				for (Buster o : g.busters) {
					if (Game.dist(o.x, o.y, b.x, b.y) < Game._2_200) {
						set.add(o);
					}
				}
				for (Ghost o : g.ghosts) {
					if (o.captured == -1 && o.isFree && Game.dist(o.x, o.y, b.x, b.y) < Game._2_200) {
						set.add(o);
					}
				}
			}
		}
		int nbEntities = set.size();
		String inputTurn = nbEntities + "\n";
		for (Object o : set) {
			inputTurn += o.toString() + "\n";
		}
		w.getOut().print(inputTurn);
		w.getOut().flush();
		
		String actions = "";
		while (w.getIn().hasNextLine()) {
			actions += w.getIn().nextLine() + "\n";
		}
		List<Order> orders = new ArrayList<>();
		// MOVE, BUST, RELEASE or STUN
		for (String action: actions.split("\n")) {
			String[] split = action.split(" ");
			Order res;
			if (action.startsWith("MOVE")) {
				res = Order.Factory.move(Integer.parseInt(split[1]), Integer.parseInt(split[2]), f(split, 3));
			}
			else if (action.startsWith("BUST")) {
				res = Order.Factory.bust(Integer.parseInt(split[1]), f(split, 2));
			}
			else if (action.startsWith("STUN")) {
				res = Order.Factory.stun(Integer.parseInt(split[1]), f(split, 2));
			}
			else if (action.startsWith("RELEASE")) {
				res = Order.Factory.release(f(split, 1));
			}
			else {
				res = Order.none(action);
			}
			orders.add(res);
			if (LOG.isDebugEnabled()) {
                LOG.debug("Player "+teamId +"\n" + actions);
                LOG.debug("");
            }
		}
		return orders;
	}

	
	private String f(String[] split, int i) {
		if (split.length > i) {
			String res ="";
			for (int j = i; j< split.length; j++) {
				res += split[j] + " ";
			}
			return res;
		}
		return "";
	}
	
	@Override
	protected void initialize() throws Exception {
		long currentTimeMillis = System.currentTimeMillis();
		Random random = new Random(currentTimeMillis);
		int nbGhost = random.nextInt(21) + 8; // entre 8 et 28
		int nbBusters = random.nextInt(4) + 2; // entre 2 et 5
		game = new Game();
		game.generate(currentTimeMillis, nbGhost, nbBusters);
		
		for (int teamId = 0; teamId < 2; teamId ++) {

			players[teamId] = new PlayerBase(process.get(teamId), teamId);
			int bustersPerPlayer = game.busters.size() / 2;
			int ghostCount = game.ghosts.size();
			int myTeamId = teamId;

			String initString = bustersPerPlayer + " " + ghostCount + " " + myTeamId;
			players[teamId].getOut().println(initString);            
			players[teamId].getOut().flush();            

            if (LOG.isDebugEnabled()) {
                LOG.debug("Init :" + initString);
            }

        }
	}

}
