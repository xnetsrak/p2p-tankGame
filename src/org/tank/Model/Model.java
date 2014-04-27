package org.tank.Model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.tank.Members.Bomb;
import org.tank.Members.EnemyTank;
import org.tank.Members.Hero;
import org.tank.Members.Shot;
import org.tank.Members.Tank;
import rice.environment.Environment;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

class status {
	boolean change;
	public status() { this.change = false; }
}

public class Model 
{
	private ArrayList<Observer> observers = new ArrayList<Observer>();
	private PastryNode _pastryNode;
	private org.tank.Model.PastryApp _pastryApp;
	
	private ArrayList<EnemyTank> _enemyTanks = new ArrayList<EnemyTank>();
	private ArrayList<Bomb> _bombs = new ArrayList<Bomb>();
	private int myPoints = 0;
	private int gameWidth;
	private int gameHeight;
	private Hero _hero;
	
	private status currStatus = new status();
	
	public Model(int gameWidth, int gameHeight)
	{
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		
		setHero(new Hero((int) (Math.random() * getGameWidth()),(int) (Math.random() * getGameHeight())));
		
		Thread t = new Thread(new RunThread(), "RunThread");
		t.start();
	}
	
	public boolean setup(int bindPort, String bootAddress, int bootPort) throws Exception
	{
		 // Loads pastry settings
	    Environment env = new Environment();

	    // disable the UPnP setting (in case you are testing this on a NATted LAN)
	    env.getParameters().setString("nat_search_policy","never");
	    
	    try 
	    {
			// the port to use locally
			int bindport = bindPort;
			  
			// build the bootaddress from the command line args
			InetAddress bootaddr = InetAddress.getByName(bootAddress);
			int bootport = bootPort;
			InetSocketAddress bootaddress = new InetSocketAddress(bootaddr,bootport);
			
			// Generate the NodeIds Randomly
		    NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
		    
		    // construct the PastryNodeFactory, this is how we use rice.pastry.socket
		    PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
		
		    // construct a node
		    PastryNode node = factory.newNode();
		    _pastryNode = node;
		    
		    // construct a new MyApp
		    org.tank.Model.PastryApp app = new org.tank.Model.PastryApp(node, this);    
		    _pastryApp = app;
		    
		    node.boot(bootaddress);
		    
		    // the node may require sending several messages to fully boot into the ring
		    synchronized(node) {
		      while(!node.isReady() && !node.joinFailed()) {
		        // delay so we don't busy-wait
		        node.wait(500);
		        
		        // abort if can't join
		        if (node.joinFailed()) {
		          throw new IOException("Could not join the FreePastry ring.  Reason:"+node.joinFailedReason()); 
		        }
		      }       
		    }
	    
	    } catch (Exception e) {
		      // remind user how to use
		      System.out.println("Usage:"); 
		      System.out.println("java [-cp FreePastry-<version>.jar] rice.tutorial.lesson3.DistTutorial localbindport bootIP bootPort");
		      System.out.println("example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001");
		      throw e; 
	    }
	    
	    System.out.println("Finished creating new node "+_pastryNode);
	    
	    
	    //Send Join Msgs to leaf
	   LeafSet leafSet = _pastryNode.getLeafSet();
	    ArrayList<rice.p2p.commonapi.Id> sentTo = new ArrayList<rice.p2p.commonapi.Id>();
	    for (int i=-leafSet.ccwSize(); i<=leafSet.cwSize(); i++) {
	      if (i != 0) { // don't send to self
	        // select the item
	        NodeHandle nh = leafSet.get(i);
	        if(!sentTo.contains(nh.getId()))
	        	_pastryApp.routeMyMsgDirect(nh, new MyMsg(_pastryApp.endpoint.getId(), nh.getId(), "join"));   
	        sentTo.add(nh.getId());
	      }
	    }
	    
	    return _pastryNode != null && _pastryApp != null;
	}
	
	public void tankJoin()
	{
		EnemyTank et = new EnemyTank((int) (Math.random() * gameWidth), (int) (Math.random() * gameHeight));
		Thread t = new Thread(et);
		t.start();
		_enemyTanks.add(et);
		notifyObserver();
	}	
	
	public void changeHeroDirection(int direction)
	{
		switch (direction) {
		case 0:
			_hero.setDirect(0);
			_hero.setY(_hero.getY() - _hero.getSpeed());
			break;
		case 1:
			_hero.setDirect(1);
			_hero.setX(_hero.getX() + _hero.getSpeed());
			break;
		case 2:
			_hero.setDirect(2);
			_hero.setY(_hero.getY() + _hero.getSpeed());
			break;
		case 3:
			_hero.setDirect(3);
			_hero.setX(_hero.getX() - _hero.getSpeed());
			break;
		default:
			break;
		}
		
	}
	
	public void shotEnemy()
	{
		if (this._hero.s.size() < 5)
			_hero.shotEnemy();
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
			//enemyTank.isLive = false;

			Bomb newbomb = new Bomb(enemyTank.getX(), enemyTank.getY());
			_bombs.add(newbomb);
			myPoints++;
			notifyObserver();
		}
	}

	public void hitmytank(Shot s, Tank et) {
		switch (et.getDirect()) {
		case 0:
		case 2:
			if (s.x >= et.x && s.x <= et.x + 20 && s.y >= et.y && s.y <= et.y + 30) {
				s.isLive = false;
				myPoints--;

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				_bombs.add(newbomb);
				notifyObserver();
			}
			break;

		case 1:
		case 3:

			if (s.x > et.x && s.x < et.x + 30 && s.y > et.y && s.y < et.y + 20) {
				s.isLive = false;
				setMyPoints(getMyPoints() - 1);

				// create a bomb
				Bomb newbomb = new Bomb(et.getX(), et.getY());
				_bombs.add(newbomb);
				notifyObserver();
			}
		}
	}
	
	class RunThread implements Runnable
	{
		public void run() {
			
			while (!currStatus.change) {

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				for (int i = 0; i < _enemyTanks.size(); i++) {
					for (int j = 0; j < _hero.s.size(); j++)
						hittank(_hero.s.get(j), _enemyTanks.get(i));
				}

				for (int i = 0; i < _enemyTanks.size(); i++) {

					EnemyTank t = _enemyTanks.get(i);
					for (int j = 0; j < t.s.size(); j++)
						hitmytank(t.s.get(j), _hero);
				}

			}
		}
	}
	
	
	public void addObserver(Observer obs)
	{
		observers.add(obs);
	}
	public void removeObserver(Observer obs)
	{
		observers.remove(obs);
	}
	public void notifyObserver()
	{
		for(Observer obs : observers)
			obs.update();
	}
	
	public int getGameWidth() { return gameWidth; }
	public void setGameWidth(int gameWidth) { this.gameWidth = gameWidth; }

	public int getGameHeight() { return gameHeight; }

	public void setGameHeight(int gameHeight) { this.gameHeight = gameHeight; }

	public ArrayList<EnemyTank> getEnemyTanks() { return _enemyTanks; }

	public Hero getHero() { return _hero; }

	public void setHero(Hero _hero) { this._hero = _hero; }

	public ArrayList<Bomb> getBombs() { return _bombs; }

	public void setBombs(ArrayList<Bomb> _bombs) { this._bombs = _bombs; }

	public int getMyPoints() { return myPoints; }

	public void setMyPoints(int myPoints) { this.myPoints = myPoints; }
}
