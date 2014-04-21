package org.tank.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;

class status {

	boolean change;

	status() {
		this.change = false;
	}
}

public class tankgame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	MyPanel mp = null;
	JMenuBar jMenuBar = null;
	JMenu jMenu = null;
	JMenuItem jmiNew = null;
	JMenuItem jmiExit = null;
	
	status currStatus = new status();

	public tankgame() {

		// Recorder rec = new Recorder();
		jMenuBar = new JMenuBar();
		jMenu = new JMenu("Game");

		jmiNew = new JMenuItem("start new game");
		jmiExit = new JMenuItem("exit");

		jmiExit.addActionListener(this);
		jmiExit.setActionCommand("exit");

		jMenu.add(jmiNew);
		jMenu.add(jmiExit);
		jMenuBar.add(jMenu);

		jmiNew.addActionListener(this);
		jmiNew.setActionCommand("new");
		this.setJMenuBar(jMenuBar);

		this.setTitle("P2P tankGame");
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("new")) {

			mp = new MyPanel(currStatus);
			Thread t = new Thread(mp);
			this.addKeyListener(mp);
			this.add(mp);
			t.start();
			this.setVisible(true);
		}

		if (e.getActionCommand().equals("exit")) {

			System.exit(0);
		}

	}

}

// my panel
class MyPanel extends JPanel implements java.awt.event.KeyListener, Runnable {

	private static final long serialVersionUID = 1L;
	
	status myStatus;
	final static int width = 400;
	final static int height = 400;
	Hero hero = null;
	Vector<EnemyTank> enemyTanks = new Vector<EnemyTank>();
	int enemyNum = 10;

	// three image makes one bomb
	Image image1, image2, image3 = null;

	Vector<Bomb> bombs = new Vector<Bomb>();

	int mylife = 3;

	public MyPanel(status parentStatus) {

		myStatus = parentStatus;

		image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
		image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
		image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));

		hero = new Hero((int) (Math.random() * width),(int) (Math.random() * height));

		for (int i = 0; i < enemyNum; i++) {

			EnemyTank et = new EnemyTank((int) (Math.random() * width), (int) (Math.random() * height));
			Thread t = new Thread(et);
			t.start();
			enemyTanks.add(et);
		}
	}

	public void showinfo(Graphics g) {

		this.drawTank(20, 320, g, 0, 1);
		g.setColor(Color.black);
		g.drawString(enemyNum + " ", 45, 340);
		this.drawTank(100, 320, g, 0, 0);
		g.setColor(Color.black);
		g.drawString(mylife + " ", 130, 340);
	}

	public void paint(Graphics g) 
	{

		super.paint(g);
		this.showinfo(g);

		if (mylife > 0)
			this.drawTank(hero.getX(), hero.getY(), g, hero.getDirect(), 0);

		for (int i = 1; i <= hero.s.size(); i++) {

			if (hero.s.get(i - 1).isLive) {
				g.setColor(Color.red);
				g.draw3DRect(hero.s.get(i - 1).x, hero.s.get(i - 1).y, 1, 1,
						false);
			} else {
				hero.s.remove(i - 1);
				i--;
			}
		}

		for (int i = 0; i < enemyTanks.size(); i++) {

			if (enemyTanks.get(i).isLive) {
				this.drawTank(enemyTanks.get(i).getX(), enemyTanks.get(i).getY(), g, enemyTanks.get(i).getDirect(), 1);

				for (int j = 1; j <= enemyTanks.get(i).s.size(); j++) {
					if (enemyTanks.get(i).s.get(j - 1).isLive == true) {
						g.setColor(Color.black);
						g.draw3DRect(enemyTanks.get(i).s.get(j - 1).x, enemyTanks.get(i).s.get(j - 1).y, 1, 1, false);
					} else {
						enemyTanks.get(i).s.remove(j - 1);
						j--;
					}
				}
			} else {
				enemyTanks.remove(i);
				i--;
			}
		}

		for (int i = 0; i < bombs.size(); i++) {

			Bomb b = bombs.get(i);

			if (b.life > 6)
				g.drawImage(image1, b.x, b.y, 30, 30, this);

			else if (b.life > 3)
				g.drawImage(image2, b.x, b.y, 30, 30, this);

			else
				g.drawImage(image3, b.x, b.y, 30, 30, this);

			b.lifeDown();
			if (b.life == 0) {
				bombs.remove(i);
				i--;
			}
		}
	}

	// function to judge whether a bullet has shot the tank
	public void hittank(Shot s, Tank enemyTank) {
		boolean tankHit = false;
		
		switch (enemyTank.getDirect()) 
		{
			case 0:
			case 2:
				if (s.x >= enemyTank.x && s.x <= enemyTank.x + 20 && s.y >= enemyTank.y && s.y <= enemyTank.y + 30)
					tankHit = true;
				break;
			case 1:
			case 3:
				if (s.x > enemyTank.x && s.x < enemyTank.x + 30 && s.y > enemyTank.y && s.y < enemyTank.y + 20)
					tankHit = true;
				
		}
		if(tankHit)
		{
			s.isLive = false;
			enemyTank.isLive = false;
			enemyNum--;

			Bomb newbomb = new Bomb(enemyTank.getX(), enemyTank.getY());
			bombs.add(newbomb);
			mylife++;
		}
	}

	public void hitmytank(Shot s, Tank et) {
		switch (et.getDirect()) {
		case 0:
		case 2:
			if (s.x >= et.x && s.x <= et.x + 20 && s.y >= et.y && s.y <= et.y + 30) {
				s.isLive = false;
				mylife--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				bombs.add(newbomb);
			}
			break;

		case 1:
		case 3:

			if (s.x > et.x && s.x < et.x + 30 && s.y > et.y && s.y < et.y + 20) {
				s.isLive = false;
				mylife--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				bombs.add(newbomb);
			}
		}
	}

	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		switch (type) {
		case 0:
			g.setColor(Color.yellow);
			break;
		case 1:
			g.setColor(Color.blue);
		}

		switch (direct) {
		case 0:
			g.fill3DRect(x, y, 5, 30, false);
			g.fill3DRect(x + 15, y, 5, 30, false);
			g.fill3DRect(x + 5, y + 5, 10, 20, false);
			g.fillOval(x + 4, y + 10, 10, 10);
			g.drawLine(x + 9, y + 15, x + 9, y - 4);
			break;

		case 1:
			g.fill3DRect(x + 5, y + 5, 30, 5, false);
			g.fill3DRect(x + 5, y + 20, 30, 4, false);
			g.fill3DRect(x + 10, y + 10, 20, 10, false);
			g.fillOval(x + 15, y + 10, 10, 10);
			g.drawLine(x + 20, y + 15, x + 40, y + 15);
			break;

		case 2:
			g.fill3DRect(x, y, 5, 30, false);
			g.fill3DRect(x + 15, y, 5, 30, false);
			g.fill3DRect(x + 5, y + 5, 10, 20, false);
			g.fillOval(x + 4, y + 10, 10, 10);
			g.drawLine(x + 9, y + 15, x + 9, y + 28);
			break;

		case 3:
			g.fill3DRect(x + 5, y + 5, 30, 5, false);
			g.fill3DRect(x + 5, y + 20, 30, 4, false);
			g.fill3DRect(x + 10, y + 10, 20, 10, false);
			g.fillOval(x + 15, y + 10, 10, 10);
			g.drawLine(x + 20, y + 15, x - 5, y + 15);
			break;
		}
	}

	public void keyPressed(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && hero.y < 280) 
		{
			hero.setDirect(2);
			hero.setY(hero.getY() + hero.getSpeed());
		}

		else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && hero.y > 0) 
		{
			hero.setDirect(0);
			hero.setY(hero.getY() - hero.getSpeed());
		}

		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && hero.x > 0) 
		{
			hero.setDirect(3);
			hero.setX(hero.getX() - hero.getSpeed());
		}
		else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && hero.x < 380) 
		{
			hero.setDirect(1);
			hero.setX(hero.getX() + hero.getSpeed());
		}

		if (e.getKeyCode() == KeyEvent.VK_J) {
			if (this.hero.s.size() < 5) {
				hero.shotEnemy();
			}
		}
		this.repaint();
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	}

	public void run() {

		while (!this.myStatus.change) {

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < enemyTanks.size(); i++) {
				for (int j = 0; j < hero.s.size(); j++)
					this.hittank(hero.s.get(j), enemyTanks.get(i));
			}

			for (int i = 0; i < enemyTanks.size(); i++) {

				EnemyTank t = enemyTanks.get(i);
				for (int j = 0; j < t.s.size(); j++)
					this.hitmytank(t.s.get(j), hero);
			}

			this.repaint();

		}
	}
}




