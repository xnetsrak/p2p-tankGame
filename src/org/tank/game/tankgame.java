package org.tank.game;

import javax.swing.*;

import org.tank.Members.*;
import org.tank.Model.Model;
import org.tank.Logger.AutoDriver;

import rice.p2p.commonapi.Id;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;

public class tankgame extends JFrame implements ActionListener, WindowListener, org.tank.Model.Observer
{
	private static final long serialVersionUID = 1L;
	MyPanel mp = null;
	JMenuBar jMenuBar = null;
	JMenu jMenu = null;
	JMenuItem jmiNew = null;
	JMenuItem jmiExit = null;
	JMenuItem jmiJoinCreate = null;

	private int width = 400;
	private int height = 400;
	
	private Model _model;

	public tankgame(Boolean isDummyTank, int localPort, String bootIPAddress, int remotePort) {

		jMenuBar = new JMenuBar();
		jMenu = new JMenu("Game");

		jmiExit = new JMenuItem("exit");
		jmiJoinCreate = new JMenuItem("join/create game");

		jmiExit.addActionListener(this);
		jmiExit.setActionCommand("exit");
		
		jmiJoinCreate.addActionListener(this);
		jmiJoinCreate.setActionCommand("joincreate");

		jMenu.add(jmiJoinCreate);
		jMenu.add(jmiExit);
		jMenuBar.add(jMenu);

		this.setJMenuBar(jMenuBar);

		this.setTitle("P2P tankGame");
		this.setSize(width+60, height+130);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(this);
		this.setVisible(true);
		
		_model = new Model(width, height);
		_model.addObserver(this);
		
		if (isDummyTank) {
			try {
				joinCreateGame(localPort, bootIPAddress, remotePort);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread autoDriverThread = new Thread(new AutoDriver(_model), "AutoDriverThread");
			autoDriverThread.start();
		}
		
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("exit")) {
			leavegame();
		}
		if (e.getActionCommand().equals("joincreate")) {
			new CreateJoinPastryDialog(this).setVisible(true);
		}
	}
	
	public void joinCreateGame(int myPort, String bootIp, int bootPort) throws Exception
	{
		System.out.println(myPort + "; " + bootIp + "; " + bootPort);
		 
		boolean started = _model.setup(myPort, bootIp, bootPort);
	    
	    if(started)
    	{
			mp = new MyPanel(_model);
			Thread t = new Thread(mp);
			t.start();
			this.addKeyListener(mp);
			this.add(mp);
			this.setVisible(true);
    	}
	    
	}
	
	public void leavegame()
	{
		if(!_model.started)
			System.exit(0);
		else _model.leaveGame();
	}

	public void update() {		
	}

	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0) {
		leavegame();
	}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}
	
}


// my panel
class MyPanel extends JPanel implements java.awt.event.KeyListener, Runnable,  org.tank.Model.Observer {

	private static final long serialVersionUID = 1L;
	private HashMap<Id,EnemyTank> _enemyTanks = new HashMap<Id,EnemyTank>();
	private Hero _hero = null;
	private ArrayList<Bomb> _bombs = new ArrayList<Bomb>();

	private Model _model;

	// three image makes one bomb
	Image image1, image2, image3 = null;

	int _myPoints = 0;

	public MyPanel(Model model) {

		_model = model;
		_model.addObserver(this);

		image1 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
		image2 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
		image3 = Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));

		_hero = _model.getHero();
		/*for (int i = 0; i < enemyNum; i++) {

			EnemyTank et = new EnemyTank((int) (Math.random() * width), (int) (Math.random() * height));
			Thread t = new Thread(et);
			t.start();
			enemyTanks.add(et);
		}*/
	}
	
	public void update() 
	{
		_enemyTanks = _model.getEnemyTanks();
		_hero = _model.getHero();
		_bombs = _model.getBombs();
		_myPoints = _model.getMyPoints();
		this.repaint();
	}

	public void showinfo(Graphics g) {

		//this.drawTank(20, 320, g, 0, 1);
		//g.setColor(Color.black);
		//g.drawString(enemyNum + " ", 45, 340);
		this.drawTank(20, _model.getGameHeight()+20, g, 0, _hero.color);
		g.setColor(Color.black);
		g.drawString(_myPoints + " ", 45, _model.getGameHeight()+40);
		
		int x = 100;
		for(Id id : _enemyTanks.keySet())
		{
			Tank tank = _enemyTanks.get(id);
			this.drawTank(x, _model.getGameHeight()+20, g, 0, tank.color);
			g.setColor(Color.black);
			g.drawString(tank.points + " ", x+25, _model.getGameHeight()+40);
			x = x+55;
		}
	}

	public void paint(Graphics g) 
	{

		super.paint(g);
		this.showinfo(g);
		
		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(0, 0, _model.getGameWidth(), _model.getGameHeight());

		//if (_myPoints > 0)
		this.drawTank(_hero.getX(), _hero.getY(), g, _hero.getDirect(), _hero.color);

		for (int i = 1; i <= _hero.s.size(); i++) {

			if (_hero.s.get(i - 1).isLive) {
				g.setColor(Color.red);
				g.draw3DRect(_hero.s.get(i - 1).x, _hero.s.get(i - 1).y, 1, 1,
						false);
			} else {
				_hero.s.remove(i - 1);
				i--;
			}
		}


		for(Id id : _enemyTanks.keySet())
		{
			if (_enemyTanks.get(id).isLive) {
				this.drawTank(_enemyTanks.get(id).getX(), _enemyTanks.get(id).getY(), g, _enemyTanks.get(id).getDirect(), _enemyTanks.get(id).color);

				for (int j = 1; j <= _enemyTanks.get(id).s.size(); j++) {
					if (_enemyTanks.get(id).s.get(j - 1).isLive == true) {
						g.setColor(Color.black);
						g.draw3DRect(_enemyTanks.get(id).s.get(j - 1).x, _enemyTanks.get(id).s.get(j - 1).y, 1, 1, false);
					} else {
						_enemyTanks.get(id).s.remove(j - 1);
						j--;
					}
				}
			} else {
				_enemyTanks.remove(id);
			}
		}

		for (int i = 0; i < _bombs.size(); i++) {

			Bomb b = _bombs.get(i);

			if (b.life > 6)
				g.drawImage(image1, b.x, b.y, 30, 30, this);

			else if (b.life > 3)
				g.drawImage(image2, b.x, b.y, 30, 30, this);

			else
				g.drawImage(image3, b.x, b.y, 30, 30, this);

			b.lifeDown();
			if (b.life == 0) {
				_bombs.remove(i);
				i--;
			}
		}
	}

	public void drawTank(int x, int y, Graphics g, int direct, Color color) {

		g.setColor(color);

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

		if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && _hero.y < _model.getGameHeight()-30) 
			_model.changeHeroDirection(2);
		else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && _hero.y > 0) 
			_model.changeHeroDirection(0);
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && _hero.x > 0) 
			_model.changeHeroDirection(3);
		else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && _hero.x < _model.getGameWidth()-30) 
			_model.changeHeroDirection(1);
		
		
		//this.repaint();
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			_model.shotEnemy();
		
		//this.repaint();
	}

	public void keyTyped(KeyEvent e) {

	}

	public void run() {
		
		while (true) {

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.repaint();

		}
	}
}
