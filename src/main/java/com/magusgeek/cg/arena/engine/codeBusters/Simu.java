package com.magusgeek.cg.arena.engine.codeBusters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.magusgeek.cg.arena.engine.codeBusters.Game.Buster;
import com.magusgeek.cg.arena.engine.codeBusters.Game.Ghost;

public class Simu {


	public static void main(String[] args) {
		long currentTimeMillis = System.currentTimeMillis();
		Random random = new Random(currentTimeMillis);
		int nbGhost = random.nextInt(21) + 8; // entre 8 et 28
		int nbBusters = random.nextInt(4) + 2; // entre 2 et 5
		Game game = new Game();
		game.generate(currentTimeMillis, nbGhost, nbBusters);
		
		
//		DefaultPlayerWrapper w0 = new DefaultPlayerWrapper();
//		w0.init(game, 0);
//		DefaultPlayerWrapper w1 = new DefaultPlayerWrapper();
//		w1.init(game, 1);
		
		JPanel comp = display(game);
		
		for (int turn = 0; turn < 401; turn ++) {
			List<Order> loop = new ArrayList<>();

//			loop.addAll(loop0);
//			loop.addAll(loop1);
			
			game.apply(loop);
			
			repaint(comp);
			pause();
			
			System.out.println("TURN  ---- " + turn +" -----");
		}
		
	}

	private static JPanel display(Game game) {
		JFrame frame = new JFrame("Simu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel comp = new JPanel() {
			{
	            // set a preferred size for the custom panel.
	            setPreferredSize(new Dimension(1600,900));
	            setOpaque(true);
	        }
			@Override
			protected void paintComponent(Graphics g1) {
				final Graphics2D g = (Graphics2D) g1;
		        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g.clearRect(0, 0, 1600, 900);
				g.setColor(lcol(0, 150));
				g.fillOval(-160, -160, 160 * 2, 160 * 2);
				g.setColor(lcol(1, 150));
				g.fillOval(1_600 -160, 900 -160, 160 * 2, 160 * 2);
				
				g.setColor(col(0));
				g.setFont(g.getFont().deriveFont(Font.BOLD));
				g.drawString("" + game.ghosts.stream().filter(x -> x.captured == 0).count(), 20, 20);
				
				g.setColor(col(1));
				g.setFont(g.getFont().deriveFont(Font.BOLD));
				g.drawString("" + game.ghosts.stream().filter(x -> x.captured == 1).count(), 1600 - 20,  900 - 20);
				
				
				for (Buster b : game.busters) {
					if (b.carrying != null) {
						g.setColor(new Color(200,255, 200));
						g.fillOval(b.x / 10 , b.y/10 , 20, 20);
						g.setColor(new Color(0,100,0));
						g.drawOval(b.x / 10 , b.y/10 , 20, 20);
					}
					if (b.state == 2) {
						g.setColor(new Color(200, 200, 255, 100));
						g.fillOval(b.x / 10 - 15, b.y/10 -15, 30, 30);
						g.setColor(new Color(0,0,100));
						g.drawString("" + b.value, b.x / 10 + 15 , b.y/10 + 15);
						g.setColor(new Color(255,200,150));
					}
					else {
						g.setColor(col(b.entityType));
						g.drawOval(b.x / 10 - 220, b.y/10 -220, 220 * 2, 220 *2);
						Area outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
						outside.subtract(new Area(new Ellipse2D.Float(b.x / 10 - 90, b.y /10 - 90, 90*2, 90*2)));
						g.setClip(outside);
						g.setColor(lcol(b.entityType, 100));
						g.fillOval(b.x / 10 - 176, b.y/10 -176, 176 * 2, 176 * 2);
						g.setClip(null);
						g.setColor(lcol(b.entityType));
					}
					g.fillOval(b.x / 10 - 10, b.y/10 -10, 20, 20);
					g.setColor(col(b.entityType));
					g.drawOval(b.x / 10 - 10, b.y/10 -10, 20, 20);
					g.setFont(g.getFont().deriveFont(Font.BOLD));
					g.drawString("" + b.entityId, b.x / 10 - 5 , b.y/10 + 5);
					
					if (b.msg != null) {
						g.setColor(col(b.entityType));
						g.setFont(g.getFont().deriveFont(Font.ITALIC));
						g.drawString(b.msg, b.x/ 10, b.y/10 > 30 ? (b.y/10- 30) : (b.y/10+ 30));
					}
					if (b.freezed <= 0) {
						g.setColor(new Color(100,255, 100));
						g.fillOval(b.x / 10+ 5 , b.y/10 - 5 , 10, 10);
						g.setColor(new Color(0,100, 0));
						g.drawOval(b.x / 10 +5, b.y/10 -5, 10, 10);
						g.setColor(col(b.entityType));
						g.setFont(g.getFont().deriveFont(Font.PLAIN));
						g.drawString("" + b.freezed, b.x/ 10 + 5, b.y/10 - 5);
					}
				}
				for (Ghost b : game.ghosts) {
					if (b.isFree && b.captured == -1) {
						g.setColor(new Color(100, 100, 100, 100));
						g.drawOval(b.x / 10 - 220, b.y/10 -220, 220 * 2, 220 * 2);
						g.setColor(new Color(200,255, 200));
						g.fillOval(b.x / 10 - 10, b.y/10 -10, 20, 20);
						g.setColor(new Color(0,100,0));
						g.drawOval(b.x / 10 - 10, b.y/10 -10, 20, 20);
						g.setFont(g.getFont().deriveFont(Font.BOLD));
						g.drawString("" + b.entityId, b.x / 10 - 5 , b.y/10 + 5);
						g.setColor(new Color(0,100,0));
						g.drawString("" + b.state, b.x / 10 + 15 , b.y/10 + 15);
					}
					for (Buster bust :b.busters) {
						g.setColor(new Color(100, 0, 0, 100));
						g.drawLine(b.x/10, b.y/10, bust.x/10, bust.y/10);
					}
				}
			}
			private Color lcol(int teamId, int i) {
				return teamId == 0 ? new Color(200,200, 255, i) : new Color(255, 200, 200, i);
			}
			private Color col(int teamId) {
				return teamId == 0 ? Color.BLUE : Color.RED;
			}
			private Color lcol(int teamId) {
				return lcol(teamId, 255);
				
			}
		};
		frame.getContentPane().add(comp);
		frame.pack();
		frame.setVisible(true);
		return comp;
	}

	private static void repaint(JPanel comp) {
		try {
			SwingUtilities.invokeAndWait(() -> comp.repaint());
		} catch (InvocationTargetException |InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void pause() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	}

	
}
