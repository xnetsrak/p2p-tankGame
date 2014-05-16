package org.tank.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.tank.Members.Shot;
import org.tank.Members.Tank;
import org.tank.Msg.CoordinatorUpdateMsg;
import org.tank.Msg.JoinResponseMsg;
import org.tank.Msg.JoinScribeMsg;
import org.tank.Msg.TankPositionUpdateMsg;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;
import rice.pastry.PastryNode;

public class Coordinator
{
	public boolean _active = false;
	public boolean _isCoordinator = false;
	private Map<Id,Tank> _tanks;
	private org.tank.Model.PastryApp _pastryApp;
	private PastryNode _pastryNode;
	private Model _model;
	private int seqNum = 0;
	private int frameNumber = 0;
	private int coordinatorSize = 3;
	
	public Coordinator(PastryNode pastryNode, PastryApp pastryApp, Model model, boolean isCoo)
	{
		_tanks = Collections.synchronizedMap(new HashMap<Id,Tank>());
		
		_isCoordinator = isCoo;
		this._pastryNode = pastryNode;
		this._pastryApp = pastryApp;
		this._model = model;
	}
	
	public void start()
	{
		_active = true;
		sendFrameUpdate();
		Thread ct = new Thread(new CoordinatorThread());
		ct.start();
		
	}
	public void setTanks(TankUpdate[] tanks)
	{
		_tanks.clear();
		for(TankUpdate tank : tanks)
		{
			if(_tanks.get(tank.Id) == null) {
				Tank et = new Tank(tank.x, tank.y, tank.w, _model.getGameWidth(), _model.getGameHeight());
				et.hasMoved = true;
				_tanks.put(tank.Id, et);
			}
		}
	}

	public void tankJoin(JoinScribeMsg joinMsg)
	{
		//if(joinMsg.from.equals(_pastryApp.endpoint.getLocalNodeHandle()))
		//	return;
		
		if(_tanks.get(joinMsg.from) == null) {
			Tank et = new Tank(joinMsg.x, joinMsg.y, joinMsg.direction, _model.getGameWidth(), _model.getGameHeight());
			et.hasMoved = true;
			if(newCoordinator())
				et.isCoordinator = true;
			_tanks.put(joinMsg.from.getId(), et);
		}
		
		JoinResponseMsg jrm = new JoinResponseMsg(_pastryApp.endpoint.getId(), joinMsg.from.getId(),_pastryApp.endpoint.getLocalNodeHandle(), true, frameNumber);
		_pastryApp.routeMyMsgDirect(joinMsg.from, jrm);
		
	}
	/*
	 * Updates received from tanks
	 */
	public void tankUpdateRequest(TankPositionUpdateMsg recivedMsg)
	{
		if(recivedMsg.leave)
			_tanks.remove(recivedMsg.tankUpdate.Id);
		else if(recivedMsg.frameNumber != this.frameNumber) {
			if(recivedMsg.frameNumber > this.frameNumber)
				this.frameNumber = recivedMsg.frameNumber;
			else
				return;
		}
		
		Tank tank = _tanks.get(recivedMsg.tankUpdate.Id);		
		if(tank != null && !tank.hasMoved) {
			if(recivedMsg.tankUpdate.fireShot)
				tank.shotEnemy();
			else
				if(tankPositionUpdateOk(tank, recivedMsg.tankUpdate.x, recivedMsg.tankUpdate.y, recivedMsg.tankUpdate.w))
					tank.updatePosistion(recivedMsg.tankUpdate.x, recivedMsg.tankUpdate.y, recivedMsg.tankUpdate.w);
			tank.hasMoved = true;
		}
	}
	
	public void sendFrameUpdate()
	{
		try
		{
			CoordinatorUpdateMsg updateMsg = new CoordinatorUpdateMsg(_pastryApp.endpoint.getLocalNodeHandle(), seqNum, frameNumber, frameNumber+1);
			updateMsg.setTank(getTanksUpdate());
			_pastryApp.sendMulticast(updateMsg);
			seqNum++;
			frameNumber++;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private TankUpdate[] getTanksUpdate()
	{
		ArrayList<TankUpdate> tanks = new ArrayList<TankUpdate>();
		for(Id id : _tanks.keySet())
		{
			Tank tank = _tanks.get(id);
			TankUpdate tUpdate = new TankUpdate(tank.x, tank.y, tank.direct, id, tank.hasNotFiredShots(), tank.points, tank.isCoordinator);
			tanks.add(tUpdate);
		}
		TankUpdate[] stockArr = new TankUpdate[tanks.size()];
		stockArr = tanks.toArray(stockArr);
		return stockArr;
	}
	
	public boolean newCoordinator()
	{
		int cooSize = 0;
		for(Id id : _tanks.keySet())
			if(_tanks.get(id).isCoordinator)
				cooSize++;
		return cooSize < coordinatorSize;
	}
	
	private boolean tankPositionUpdateOk(Tank tank, int newX, int newY, int newDirection)
	{
		if(		(tank.x == newX && tank.y == newY) || 
				(tank.x == newX && tank.y-tank.speed == newY) ||
				(tank.x == newX && tank.y+tank.speed == newY) ||
				(tank.x-tank.speed == newX && tank.y == newY) ||
				(tank.x+tank.speed == newX && tank.y == newY))
				return true;
		
		return false;
	}
	
	private boolean recivedAllUpdates() 
	{
		for(Id id : _tanks.keySet()) {
			Tank t = _tanks.get(id);
			if(!t.hasMoved)
				return false;
		}
		return _tanks.size() > 0 ? true : false;
	}
	
	public boolean hittank(Shot s, Tank tank) {
		boolean tankHit = false;
		
		switch (tank.getDirect()) 
		{
			case 0:
			case 2:
				if (s.x >= tank.x && s.x <= tank.x + 20 && s.y >= tank.y && s.y <= tank.y + 30)
					tankHit = true;
				break;
			case 1:
			case 3:
				if (s.x > tank.x && s.x < tank.x + 30 && s.y > tank.y && s.y < tank.y + 20)
					tankHit = true;
				
		}
		if(tankHit)
			s.isLive = false;
		return tankHit;
	}
	
	class CoordinatorThread implements Runnable
	{

		public void run() {
			
			while(_active)
			{
				try {
					
					if(recivedAllUpdates()) {
						sendFrameUpdate();
						
						for(Id id : _tanks.keySet()) {
							Tank t = _tanks.get(id);
							t.hasMoved = false;
						}
					}
					
					for (Id id : _tanks.keySet()) {
						Tank tank = _tanks.get(id);
						for(int j = 1; j <= tank.s.size(); j++)
							if(!tank.s.get(j-1).isLive)
								tank.s.remove(j - 1);
					}
					
					//Tank hits
					for(Id id1 : _tanks.keySet())
					{
						Tank shootingTank = _tanks.get(id1);
						for (int j = 0; j < shootingTank.s.size(); j++)
						{
							for(Id id2 : _tanks.keySet())
							{
								if(id1.equals(id2))
									continue;
								Tank hittingTank = _tanks.get(id2);
								if(hittank(shootingTank.s.get(j), hittingTank))
								{
									hittingTank.points--;
									shootingTank.points++;
								}
							}
						}
					}
					
					Thread.sleep(10);
					//_pastryNode.getEnvironment().getTimeSource().sleep(10);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	public int getFrameNumber() {
		return frameNumber;
	}

	public void setFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
	}
	
}