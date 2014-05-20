package org.tank.Model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.tank.Members.Bomb;
import org.tank.Members.EnemyTank;
import org.tank.Members.Hero;
import org.tank.Members.Shot;
import org.tank.Members.Tank;
import org.tank.Msg.CoordinatorUpdateMsg;
import org.tank.Msg.JoinResponseMsg;
import org.tank.Msg.JoinScribeMsg;
import org.tank.Msg.LeaveScribeMsg;
import org.tank.Msg.TankPositionUpdateMsg;

import rice.environment.Environment;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
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
	
	private HashMap<Id,EnemyTank> _enemyTanks = new HashMap<Id,EnemyTank>();
	private ArrayList<Bomb> _bombs = new ArrayList<Bomb>();
	private int myPoints = 0;
	private int gameWidth;
	private int gameHeight;
	private Hero _hero;
	private Random random = new Random();
	private int seqNum = 0;
	private int frameNumber = -1;
	private long lastMoveTime = 0;
	private long lastResendTime = 0;
	private boolean hasMoved = false;
	public boolean started = false;
	public TankPositionUpdateMsg previousUpdateRequest = null;
	public boolean leaving = false;

	private Coordinator _coordinator = null;
	private Map<NodeHandle, CoordinatorInfo> _coordinatorIds;
	
	private status currStatus = new status();
	
	public Model(int gameWidth, int gameHeight)
	{
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		
		_coordinatorIds = Collections.synchronizedMap(new HashMap<NodeHandle, CoordinatorInfo>());
		_hero = new Hero((int) (Math.random() * (getGameWidth()-30)),(int) (Math.random() * (getGameHeight()-30)), random.nextInt(4), gameWidth, gameHeight);
		
	}
	
	public void go()
	{
		try { Thread.sleep(2000); } catch(Exception ex) {};
		_pastryApp.subscribe();
		
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
		    PastryApp app = new PastryApp(node, this);    
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
   
	    LeafSet leafSet = _pastryNode.getLeafSet();
	    if(leafSet.size() == 0) {
	    	_coordinator = new Coordinator(_pastryNode, _pastryApp, this, true); //.start()
	    	_coordinator.start();
	    }
	    
	    go();
	    
	    JoinScribeMsg jm = new JoinScribeMsg(_pastryApp.endpoint.getLocalNodeHandle(), seqNum);
	    jm.setPosistion(_hero.getX(), _hero.getY(), _hero.getDirect());
	    _pastryApp.sendMulticast(jm, this.frameNumber);
	    seqNum++;
	    
	    return _pastryNode != null && _pastryApp != null;
	}
	
	public void tankJoinResponse(JoinResponseMsg msg)
	{
		System.out.println("JoinResponse from: " + msg.fromNodeHandle);
		_coordinatorIds.put(msg.fromNodeHandle, new CoordinatorInfo(msg.frameNumber, null));
		frameNumber = msg.frameNumber > frameNumber ? msg.frameNumber : frameNumber;
		if(msg.isCoordinator && _coordinator == null)
			_coordinator = new Coordinator(_pastryNode, _pastryApp, this, true);
	}
	
	public void changeHeroDirection(int direction)
	{
		if(hasMoved || !started)
			return;
		
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
		sendUpdate(false);
	}
	public void shotEnemy()
	{
		if(hasMoved || !started)
			return;
		
		_hero.shotEnemy();
		sendUpdate(true);
	}
	
	public synchronized void sendUpdate(boolean fireShot)
	{
		//setLargestFramenumber();
		TankUpdate tankUpdate = new TankUpdate(_hero.x, _hero.y, _hero.direct, _pastryApp.endpoint.getId(), fireShot, myPoints, _coordinator != null);
	    
	    for(NodeHandle nh : _coordinatorIds.keySet()) {
	    	
	    	try
	    	{
		    	TankPositionUpdateMsg updateMsg = new TankPositionUpdateMsg(_pastryApp.endpoint.getId(), nh.getId(), tankUpdate, this.frameNumber);
		    	previousUpdateRequest = updateMsg;
		    	updateMsg.leave = leaving;
		    	_pastryApp.routeMyMsgDirect(nh, updateMsg);
		    	_coordinatorIds.get(nh).clearLastMessage();
		    	System.out.println("Sending update to Coordinator: " + nh + " fn: " + this.frameNumber);
	    	}
	    	catch(Exception ex)
	    	{
	    		System.out.println("EXCEPTION(Model) - Sending update to Coordinator: " + nh + " fn: " + this.frameNumber);
	    	}
	    	
	    }
		hasMoved = true;
		if(leaving && _coordinator == null)
			System.exit(0);
		
	}
	
	public synchronized void coordinatorUpdateMsg(CoordinatorUpdateMsg msg)
	{		
		
		
		
		//clean dead tanks
		Set<Id> updatedTanks = new TreeSet<Id>(_enemyTanks.keySet());
		
		for(TankUpdate tank : msg._tanks)
		{
			updatedTanks.remove(tank.Id);
			
			if(tank.Id.equals(_pastryApp.endpoint.getId()))
			{
				myPoints = tank.points;
				//Is my tank the right place?
				if(tank.x != _hero.x || tank.y != _hero.y || tank.w != _hero.direct)
					_hero.updatePosistion(tank.x, tank.y, tank.w);
				
				continue;
			}
			
			if(_enemyTanks.containsKey(tank.Id))
			{
				EnemyTank eTank = _enemyTanks.get(tank.Id);
				eTank.points = tank.points;
				eTank.updatePosistion(tank.x, tank.y, tank.w);
				if(tank.fireShot)
					eTank.shotEnemy();
			}
			else
				_enemyTanks.put(tank.Id, new EnemyTank(tank.x, tank.y, tank.w, this.gameWidth, this.gameHeight));
		}
		
		//clean dead tanks
		for(Id id : updatedTanks) {
			_enemyTanks.remove(id);
			System.out.println("Removeing tank: " + id);
			for(NodeHandle nh : _coordinatorIds.keySet())
				if(nh.getId().equals(id)) {
					System.out.println("Removeing coordinator: " + nh);
					_coordinatorIds.remove(nh);
					break;
				}
		}
		
		if(_coordinator != null && !_coordinator._active)
		{
			_coordinator.setTanks(msg._tanks);
			_coordinator.setFrameNumber(msg.newFrameNumber);
			_coordinator.start();
		}
		
		this.frameNumber = msg.newFrameNumber;
		hasMoved = false;
		lastMoveTime = System.currentTimeMillis();
		notifyObserver();
	}
	
	public synchronized void coordinatorMsgRecived(CoordinatorUpdateMsg msg)
	{
		System.out.println("Reciving update from Coordinator: " + msg.from + "  newFn: " + msg.newFrameNumber + " tankCount: " + (msg._tanks != null ? msg._tanks.length : -1));
		//coordinatorUpdateMsg(msg);
		
		//CoordinatorLeave
		if(msg._tanks == null)
			recivedLeaveMsg(msg.from);
		else
		{
			//not started 
			if(msg.newFrameNumber < this.frameNumber) {
				hasMoved = false;
				return;
			}
			else
				started = true;
			
			if(_coordinatorIds.containsKey(msg.from)) {
				CoordinatorInfo cInfo = _coordinatorIds.get(msg.from);
				cInfo.frameNumber = msg.newFrameNumber;
				cInfo.setUpdateMsg(msg);
			}
			else {
				_coordinatorIds.put(msg.from, new CoordinatorInfo(msg.newFrameNumber, msg));
				System.out.println("------ adding new coordinator!");
			}
		}
			
		if(allAnswersRecived()) {
			coordinatorUpdateMsg(chooseCoordinatorAnswer());
		}
	}
	
	public CoordinatorUpdateMsg chooseCoordinatorAnswer()
	{
		writeInfo();
		lastResendTime = System.currentTimeMillis();
		
		NodeHandle tempNh = null;
		int count = -1;
		
		for(NodeHandle nh1 : _coordinatorIds.keySet()) {
			int tempCount = 0;
			CoordinatorUpdateMsg msg = _coordinatorIds.get(nh1).getUpdateMsg();
			for(NodeHandle nh2 : _coordinatorIds.keySet()) {
				if(!nh1.equals(nh2) && msg.equals(_coordinatorIds.get(nh2).getUpdateMsg()))
					tempCount++;
			}
			if(tempCount > count)
				tempNh = nh1;
		}
		return _coordinatorIds.get(tempNh).getUpdateMsg();
	}

	public void recivedLeaveMsg(NodeHandle from)
	{
		if(_coordinatorIds.containsKey(from)) {
			_coordinatorIds.remove(from);
			System.out.println("Removeing coordinator: " + from);
		}
		notifyObserver();
	}
	public void leaveGame()
	{
	    leaving = true;
	}
	
	// function to judge whether a bullet has shot the tank
	public boolean hittank(Shot s, Tank enemyTank) {
		boolean tankHit = false;
		
		try
		{
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
				Bomb newbomb = new Bomb(enemyTank.getX(), enemyTank.getY());
				_bombs.add(newbomb);
				notifyObserver();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return tankHit;
	}

	public void hitmytank(Shot s, Tank et) {
		switch (et.getDirect()) {
		case 0:
		case 2:
			if (s.x >= et.x && s.x <= et.x + 20 && s.y >= et.y && s.y <= et.y + 30) {
				s.isLive = false;

				Bomb newbomb = new Bomb(et.getX(), et.getY());
				_bombs.add(newbomb);
				notifyObserver();
			}
			break;

		case 1:
		case 3:

			if (s.x > et.x && s.x < et.x + 30 && s.y > et.y && s.y < et.y + 20) {
				s.isLive = false;

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
				
				//Tank hits
				for(Id id1 : _enemyTanks.keySet())
				{
					Tank shootingTank = _enemyTanks.get(id1);
					for (int j = 0; j < shootingTank.s.size(); j++)
					{
						for(Id id2 : _enemyTanks.keySet())
						{
							if(id1.equals(id2))
								continue;
							Tank hittingTank = _enemyTanks.get(id2);
							hittank(shootingTank.s.get(j), hittingTank);
						}
					}
					for (int j = 0; j < shootingTank.s.size(); j++)
						hitmytank(shootingTank.s.get(j), _hero);
				}

				for(Id id : _enemyTanks.keySet())
				{
					for (int j = 0; j < _hero.s.size(); j++)
						hittank(_hero.s.get(j), _enemyTanks.get(id));
				}
				
				
				if(frameNumber != -1 && System.currentTimeMillis()-lastMoveTime > 50 && !hasMoved) {
					sendUpdate(false);
					lastMoveTime = System.currentTimeMillis();

				}
				/*
				if(allAnswersRecived())
				{
					for(NodeHandle nh : _coordinatorIds.keySet()) {
						coordinatorUpdateMsg(_coordinatorIds.get(nh).getUpdateMsg());
						break;
					}
					for(NodeHandle nh : _coordinatorIds.keySet()) {
						_coordinatorIds.get(nh).setUpdateMsg(null);
					}
				}*/
				
				if(reSendPreviousFrame() != null)
				{
					NodeHandle nh = reSendPreviousFrame();
					System.out.println("Aksing coodinator to resend frame " + nh.getId() + " fn: " + frameNumber);
					previousUpdateRequest.reSendFrame = true;
					_pastryApp.routeMyMsgDirect(nh, previousUpdateRequest);
					lastResendTime = System.currentTimeMillis();
				}
			}
		}
	}
	
	public boolean allAnswersRecived()
	{
		if(_coordinatorIds.keySet().size() < 1)
			return false;
		
		boolean result = false;
		for(NodeHandle nh : _coordinatorIds.keySet()) {
			if(_coordinatorIds.get(nh).getUpdateMsg() != null)
				result = true;
			else
				return false;
		}
		return result;
	}
	
	public NodeHandle reSendPreviousFrame()
	{
		if(System.currentTimeMillis()-lastResendTime > 3000 && started)
		{
			for(NodeHandle nh : _coordinatorIds.keySet()) {
				if(_coordinatorIds.get(nh).getUpdateMsg() == null)
					return nh; 
			}
		}
		return null;
	}
	
	public void writeInfo()
	{
		System.out.println("-Coordinator-size: " + _coordinatorIds.keySet().size());
		for(NodeHandle nh : _coordinatorIds.keySet()) {
			int fn = _coordinatorIds.get(nh).getUpdateMsg() != null ? _coordinatorIds.get(nh).frameNumber : -2;
			System.out.println("--coor id: " + nh.getId() + "  Framenumber: " + fn);
		}
	}
	public void setLargestFramenumber()
	{
		for(NodeHandle nh : _coordinatorIds.keySet()) {
			int fn = _coordinatorIds.get(nh).frameNumber;
			if(fn > this.frameNumber) this.frameNumber = fn;
		}
	}
	public void setFrameNumber(int number, NodeHandle nh)
	{
		if(this.frameNumber < number && _coordinatorIds.containsKey(nh))
			this.frameNumber = number;
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

	public HashMap<Id,EnemyTank> getEnemyTanks() { return _enemyTanks; }

	public Hero getHero() { return _hero; }

	public void setHero(Hero _hero) { this._hero = _hero; }

	public ArrayList<Bomb> getBombs() { return _bombs; }

	public void setBombs(ArrayList<Bomb> _bombs) { this._bombs = _bombs; }

	public int getMyPoints() { return myPoints; }

	public void setMyPoints(int myPoints) { this.myPoints = myPoints; }
	
	public Coordinator getCoordinator() { return _coordinator; }

	public void setCoordinator(Coordinator _coordinator) { this._coordinator = _coordinator; }

}

/*for(Id id : _enemyTanks.keySet())
{
	EnemyTank t = _enemyTanks.get(id);
	for (int j = 0; j < t.s.size(); j++)
		hitmytank(t.s.get(j), _hero);
}*/

/*public void enemyTankPositionUpdate(TankPosUpdateScribeMsg updateMsg)
{
	EnemyTank et = _enemyTanks.get(updateMsg.from.getId());
	if(et != null)
		et.updatePosistion(updateMsg.x, updateMsg.y, updateMsg.direction);
	notifyObserver();
}*/
/*public void tankShotMsg(ShotScribeMsg shotMsg)
{
	EnemyTank et = _enemyTanks.get(shotMsg.from.getId());
	if(et != null)
		et.shotEnemy();
	notifyObserver();
}*/

/*public void shotEnemy()
{

	if (this._hero.s.size() < 5) {
		
		ShotScribeMsg sm = new ShotScribeMsg(_pastryApp.endpoint.getLocalNodeHandle(), seqNum);
	    _pastryApp.sendMulticast(sm);
	    seqNum++;
		
		_hero.shotEnemy();
	}
}*/

/* 
//Send Join Msgs to leaf
LeafSet leafSet = _pastryNode.getLeafSet();
ArrayList<rice.p2p.commonapi.Id> sentTo = new ArrayList<rice.p2p.commonapi.Id>();
for (int i=-leafSet.ccwSize(); i<=leafSet.cwSize(); i++) {
  if (i != 0) { // don't send to self
    // select the item
    NodeHandle nh = leafSet.get(i);
    if(!sentTo.contains(nh.getId())) {
    	JoinMsg jm = new JoinMsg(_pastryApp.endpoint.getId(), nh.getId(), seqNum);
    	jm.setPosistion(_hero.getX(), _hero.getY(), _hero.getDirect());
    	_pastryApp.routeMyMsgDirect(nh, jm);  
    }
    sentTo.add(nh.getId());
  }
}*/

/*public void tankJoin(JoinMsg joinMsg)
{
	if(_enemyTanks.get(joinMsg.from) == null) {
		EnemyTank et = new EnemyTank(joinMsg.x, joinMsg.y, joinMsg.direction);
		_enemyTanks.put(joinMsg.from, et);
	}
	notifyObserver();
	
	JoinResponseMsg jm = new JoinResponseMsg(_pastryApp.endpoint.getId(), joinMsg.from);
	jm.setPosistion(_hero.getX(), _hero.getY(), _hero.getDirect());
	_pastryApp.routeMyMsg(joinMsg.from, jm);
}	*/

/*LeafSet leafSet = _pastryNode.getLeafSet();
ArrayList<rice.p2p.commonapi.Id> sentTo = new ArrayList<rice.p2p.commonapi.Id>();
for (int i=-leafSet.ccwSize(); i<=leafSet.cwSize(); i++) {
  if (i != 0) { // don't send to self
    // select the item
    NodeHandle nh = leafSet.get(i);
    if(!sentTo.contains(nh.getId())) {
    	
    	TankPositionUpdateMsg jm = new TankPositionUpdateMsg(_pastryApp.endpoint.getId(), nh.getId());
    	jm.setPosistion(_hero.getX(), _hero.getY(), _hero.getDirect());
    	_pastryApp.routeMyMsgDirect(nh, jm);  
    	
    }
    sentTo.add(nh.getId());
  }
}*/
/*public void enemyTankPositionUpdate(TankPositionUpdateMsg updateMsg)
{
	EnemyTank et = _enemyTanks.get(updateMsg.from);
	if(et != null)
		et.updatePosistion(updateMsg.x, updateMsg.y, updateMsg.direction);
	notifyObserver();
}*/
/*LeafSet leafSet = _pastryNode.getLeafSet();
ArrayList<rice.p2p.commonapi.Id> sentTo = new ArrayList<rice.p2p.commonapi.Id>();
for (int i=-leafSet.ccwSize(); i<=leafSet.cwSize(); i++) {
  if (i != 0) { // don't send to self
    // select the item
    NodeHandle nh = leafSet.get(i);
    if(!sentTo.contains(nh.getId())) {
    	
    	ShotMsg sm = new ShotMsg(_pastryApp.endpoint.getId(), nh.getId());
    	_pastryApp.routeMyMsgDirect(nh, sm);  
    	
    }
    sentTo.add(nh.getId());
  }
}
*/
/*public void tankShotMsg(ShotMsg shotMsg)
{
	EnemyTank et = _enemyTanks.get(shotMsg.from);
	if(et != null)
		et.shotEnemy();
	notifyObserver();
}*/
