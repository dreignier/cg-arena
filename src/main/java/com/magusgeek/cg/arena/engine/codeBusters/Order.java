package com.magusgeek.cg.arena.engine.codeBusters;

import com.magusgeek.cg.arena.engine.codeBusters.Game.Buster;
import com.magusgeek.cg.arena.engine.codeBusters.Game.Ghost;

public interface Order {

	public static final int _2_200 = 2200 * 2200;
	public static final int _900 = 900 * 900;
	public static final int _1_760 = 1_760 * 1_760;
	public static final int _1_600 = 1_600 * 1_600;
	
	
	public static class Factory {
		public static Move move(int x, int y, String msg) {
			return new Move(x,y,msg);
		}
		
		public static Bust bust(int ghostId, String msg) {
			return new Bust(ghostId, msg);
		}
		
		public static Release release(String msg) {
			return new Release(msg);
		}
		
		public static Stun stun(int busterId, String msg) {
			return new Stun(busterId, msg);
		}
	}
	
	public static class Move implements Order {
		int x, y;
		String msg;
		
		public Move(int x, int y, String msg) {
			super();
			this.x = x;
			this.y = y;
			this.msg = msg;
		}
		
		public boolean apply(Buster b, Game g) {
			if (b.state == 2)
				return true;
			int dx = b.x - x;
			int dy = b.y - y;
			int d = dx * dx + dy * dy;
			if (d == 0)
				d= 1;
			int x = (int) (b.x - (dx * 800.) / Math.sqrt(d));
			int y = (int) (b.y - (dy * 800.) / Math.sqrt(d));
			
			if (x < 0) x =0;
			if (x > 16_001) x = 16_001;
			
			if (y < 0) y = 0;
			if (y > 9_001) y = 9_001;
			
			b.x = x;
			b.y = y;
			
			return true;
		}

		@Override
		public String msg() {
			return msg;
		}
		
	}

	public static class Bust implements Order {
		int ghostId;
		String msg;
		
		public Bust(int ghostId, String msg) {
			super();
			this.ghostId = ghostId;
			this.msg = msg;
		}
		
		public boolean apply(Buster b, Game g) {
			
			if (b.state == 2)
				return true;
			
			if (ghostId >= g.ghosts.size() || ghostId < 0) {
				System.err.println("Incorrect ghostId");
				return false;
			}
			Ghost ghost = g.ghosts.get(ghostId);
			if (!ghost.isFree) {
				System.err.println("Ghost is not free");
				return false;
			}
			int dist = Game.dist(b.x, b.y, ghost.x, ghost.y);
			if (dist < _1_760 && dist > _900) {
				ghost.state--;
				ghost.addTrap(b);
				return true;
			}
			else {
				System.err.println("Unable to trap");
				return false;
			}
		}
		
		@Override
		public String msg() {
			return msg;
		}
	}

	public static class Release implements Order {
		String msg;
		
		public Release(String msg) {
			super();
			this.msg = msg;
		}
		
		public boolean apply(Buster b, Game g) {
			if (b.state == 2)
				return true;
			
			if (b.carrying == null) {
				System.err.println("No ghost to release");
				return false;
			}
			int dist = Game.dist(b.x, b.y, Game.base[b.entityType].x, Game.base[b.entityType].y);
			if (dist < _1_600) {
				b.carrying.captured = b.entityType;
				b.carrying = null;
				b.state = 0;
				return true;
			}
			else {
				b.carrying.isFree = true;
				b.carrying = null;
				b.state = 0;
				return true;
			}
		}
		
		@Override
		public String msg() {
			return msg;
		}
		
	}
	public static class Stun implements Order {
		int busterId;
		String msg;
		
		public Stun(int busterId, String msg) {
			super();
			this.busterId = busterId;
			this.msg = msg;
		}
		
		public boolean apply(Buster b, Game g) {
			if (b.freezed > 0)
				return true;
			
			if (b.carrying != null) {
				b.carrying.isFree = true;
				b.carrying.x = b.x;
				b.carrying.y = b.y;
				b.carrying.nx = b.x;
				b.carrying.ny = b.y;
				b.carrying = null;
				b.state = 0;
			}
			
			if (busterId >= g.busters.size() || busterId < 0) {
				System.err.println("Incorrect busterId");
				return false;
			}
			
			int dist = Game.dist(b.x, b.y, g.busters.get(busterId).x, g.busters.get(busterId).y);
			if (dist < _1_760) {
				b.freezed = 20;
				g.busters.get(busterId).state = 2;
				g.busters.get(busterId).value = 10;
				if (g.busters.get(busterId).carrying != null) {
					g.busters.get(busterId).carrying.isFree = true;
					g.busters.get(busterId).carrying.x = g.busters.get(busterId).x;
					g.busters.get(busterId).carrying.y = g.busters.get(busterId).y;
					g.busters.get(busterId).carrying.nx = g.busters.get(busterId).x;
					g.busters.get(busterId).carrying.ny = g.busters.get(busterId).y;
					g.busters.get(busterId).carrying = null;
				}
				return true;
			}
			else {
				System.err.println("Too far");
				return false;
			}
		}
		
		@Override
		public String msg() {
			return msg;
		}
	}
	
	public boolean apply(Buster b, Game g);
	public default boolean applyOrder(Buster b, Game g) {
		b.msg = msg();
		return apply(b, g);
	}

	public String msg();
	
	public static Order none(String action) {
		return new Order() {
			
			@Override
			public String msg() {
				return action;
			}
			
			@Override
			public boolean apply(Buster b, Game g) {
				return false;
			}
		};
	}
}
