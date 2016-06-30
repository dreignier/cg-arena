package com.magusgeek.cg.arena.engine.codeBusters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.magusgeek.cg.arena.engine.codeBusters.Order.Stun;

public class Game {

	
	public static final int _2_200 = 2200 * 2200;
	public static final int _900 = 900 * 900;
	public static final int _1_760 = 1_760 * 1_760;
	public static final int _1_600 = 1_600 * 1_600;
	
	static class Base {
		int x, y;

		public Base(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}
		
	}
	public static Base[] base = new Base[]{new Base(0,0), new Base(16_001, 9_001)};
	
	static class Ghost {
		int x, y;
		int entityId;
		int entityType;
		int state;
		int value;
		public boolean isFree = true;
		public int captured = -1;
		public List<Buster> busters = new ArrayList<>();
		public int nx;
		public int ny;
		
		public Ghost(int x, int y, int entityId, int entityType, int state, int value) {
			super();
			this.x = x;
			this.y = y;
			this.entityId = entityId;
			this.entityType = entityType;
			this.state = state;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return entityId + " " + x + " " + y + " " + entityType + " " + state + " " + value;
		}

		public void addTrap(Buster b) {
			busters.add(b);
		}
		
	}
	
	static class Buster {
		int x, y;
		int entityId;
		int entityType;
		int state;
		int value;
		public Ghost carrying;
		public int freezed;
		public String msg;
		
		public Buster(int x, int y, int entityId, int entityType, int state, int value) {
			super();
			this.x = x;
			this.y = y;
			this.entityId = entityId;
			this.entityType = entityType;
			this.state = state;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return entityId + " " + x + " " + y + " " + entityType + " " + state + " " + value;
		}
	}

	final static int[] STAMINAS = new int[]{3, 15, 40};
	
	int[] px2 = new int[]{1176, 2024, 14824, 13976};
	int[] py2 = new int[]{2024, 1176, 6976, 7824};

	int[] px3 = new int[]{1600, 2449, 751, 14400, 13551, 15249};
	int[] py3 = new int[]{1600, 751, 2449, 7400, 8249, 6551};
	
	int[] px4 = new int[]{1176, 2024, 327, 2873, 14824, 13976, 15673, 13127};
	int[] py4 = new int[]{2024, 1176, 2873, 327, 6976, 7824, 6127, 8673};
	
	int[] px5 = new int[]{1600, 2449, 751, 3297, -97, 14400, 13551, 15249, 12703, 16097};
	int[] py5 = new int[]{1600, 751, 2449, -97, 3297, 7400, 8249, 6551, 9097, 5703};
	
	int[][] pxs = new int[][]{px2, px3, px4, px5}; 
	int[][] pys = new int[][]{py2, py3, py4, py5};
	
	public List<Ghost> ghosts = new ArrayList<>();
	public List<Buster> busters = new ArrayList<>();
	
	public void generate(long seed, int nbGhost, int nbBusters) {
		Random random = new Random(seed);
		generateGhosts(random, nbGhost);
		generateBusters(nbBusters);
	}

	public void apply(List<Order> orders) {
		// Clean bust
		for (Ghost g: ghosts) {
			g.busters.clear();
		}
		
		// Fuite des fantomes
		for (Ghost g : ghosts) {
			g.nx = g.x;
			g.ny = g.y;
			if (g.captured != -1 || !g.isFree || !g.busters.isEmpty()) {
				continue;
			}
			int nbNear = 0;
			int bx = 0;
			int by = 0;
			for (Buster b : busters) {
				int dist = dist(b.x, b.y, g.x, g.y);
				if (dist < _2_200) {
					nbNear ++;
					bx += b.x;
					by += b.y;
				}
			}
			if (nbNear > 0) {
				bx = bx / nbNear;
				by = by / nbNear;

				int dx = g.x - bx;
				int dy = g.y - by;
				int d = dx * dx + dy * dy;
				int x = (int) (g.x + (dx * 400.) / Math.sqrt(d));
				int y = (int) (g.y + (dy * 400.) / Math.sqrt(d));

				if (x < 0) x =0;
				if (x > 16_001) x = 16_001;

				if (y < 0) y = 0;
				if (y > 9_001) y = 9_001;

				g.nx = x;
				g.ny = y;
			}


		}
				
		// Apply stun
		for (int i = 0; i < busters.size(); i++) {
			busters.get(i).freezed --;
			if (busters.get(i).freezed <0)
				busters.get(i).freezed = 0;
			if (busters.get(i).state == 2) {
				busters.get(i).value --;
				if (busters.get(i).value <= 0) {
					busters.get(i).state = 0;
				}
			}
			
			if (orders.get(i) instanceof Stun) {
				orders.get(i).applyOrder(busters.get(i), this);
			}
		}
		// Apply others
		for (int i = 0; i < busters.size(); i++) {
			if (!(orders.get(i) instanceof Stun)) {
				orders.get(i).applyOrder(busters.get(i), this);
			}
		}
		// Resolve Trap
		for (Ghost g : ghosts) {
			if (g.state <= 0) {
				List<Buster> trappers = g.busters;
				int team0 = 0;
				int team1 = 0;
				for (Buster b : trappers) {
					if (b.entityType == 0) team0++; else team1++;
				}
				int team = -1;
				if (team0 > team1) {
					team = 0;
				}
				else if (team0 < team1) {
					team = 1;
				}
				int minDist = Integer.MAX_VALUE;
				Buster nearest = null;
				for (Buster b : trappers) {
					if (b.entityType == team) {
						int dist = dist(b.x, b.y, g.x, g.y);
						if (dist < minDist) {
							nearest = b;
							minDist = dist;
						}
					}
				}
				if (nearest != null) {
					nearest.carrying = g;
					nearest.state = 1;
					g.isFree = false;
				}
				
			}
		}
		
		// Fuite des fantomes
		for (Ghost g : ghosts) {
			if (g.captured != -1 || !g.isFree || !g.busters.isEmpty()) {
				continue;
			}
			g.x = g.nx;
			g.y = g.ny;

		}
		
	}
	
	
	private void generateBusters(int nbBusters) {
		for (int i = 0; i < nbBusters * 2; i ++) {
			
			int x = pxs[nbBusters - 2][i];
			int y = pys[nbBusters - 2][i];
			busters.add(new Buster(x, y, i, i < nbBusters ? 0 : 1, 0, 0));
		}
	}

	private void generateGhosts(Random random, int nbGhost) {
		int i = 0;
		int j = 0;
		if (nbGhost % 2 == 1) {
			ghosts.add(new Ghost(8_000, 4_500, i, -1, STAMINAS[random.nextInt(3)], 0));
			j++;
		}
		for (; i < nbGhost / 2; ) {
			int x1 = random.nextInt(16_001);
			int y1 = random.nextInt(9_001);
			ghosts.add(new Ghost(x1, y1, j++, -1, STAMINAS[random.nextInt(3)], 0));
			ghosts.add(new Ghost(16_001 - x1, 9_001 - y1, j++, -1, STAMINAS[random.nextInt(3)], 0));
			i++;
		}
	}
	
	public static int dist(int x, int y, int ox, int oy) {
		int dx = x - ox;
		int dy = y - oy;
		int d = dx * dx + dy * dy;
		return d;
	}

}
