package org.tank.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.tank.Members.EnemyTank;
import org.tank.Members.Tank;
import org.tank.Msg.CoordinatorUpdateMsg;
import org.tank.Msg.JoinScribeMsg;
import org.tank.Msg.JoinScribeResponseMsg;

import rice.p2p.commonapi.Id;
import rice.pastry.PastryNode;

public class Coordinator
{
	public boolean _active = false;
	public boolean _isCoordinator = false;
	private HashMap<Id,Tank> _tanks = new HashMap<Id,Tank>();
	private org.tank.Model.PastryApp _pastryApp;
	private PastryNode _pastryNode;
	private Model _model;
	private int seqNum = 0;
	
	public Coordinator(PastryNode pastryNode, PastryApp pastryApp, Model model, boolean isCoo)
	{
		_isCoordinator = isCoo;
		this._pastryNode = pastryNode;
		this._pastryApp = pastryApp;
		this._model = model;
	}
	
	public void start()
	{
		_active = true;
		Thread ct = new Thread(new CoordinatorThread());
		ct.start();
	}
	
	public void tankJoin(JoinScribeMsg joinMsg)
	{
		//if(joinMsg.from.equals(_pastryApp.endpoint.getLocalNodeHandle()))
		//	return;
		
		if(_tanks.get(joinMsg.from) == null) {
			Tank et = new Tank(joinMsg.x, joinMsg.y, joinMsg.direction, _model.getGameWidth(), _model.getGameHeight());
			_tanks.put(joinMsg.from.getId(), et);
		}
		
		JoinScribeResponseMsg jm = new JoinScribeResponseMsg(_pastryApp.endpoint.getLocalNodeHandle(), seqNum);
		_pastryApp.sendMulticast(jm);
		seqNum++;
		
	}
	
	public void sendFrameUpdate()
	{
		try
		{
			CoordinatorUpdateMsg updateMsg = new CoordinatorUpdateMsg(_pastryApp.endpoint.getLocalNodeHandle(), seqNum);
			updateMsg.setTank(getTanksUpdate());
			_pastryApp.sendMulticast(updateMsg);
			seqNum++;
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
			TankUpdate tUpdate = new TankUpdate(tank.x, tank.y, tank.direct, id);
			tanks.add(tUpdate);
		}
		TankUpdate[] stockArr = new TankUpdate[tanks.size()];
		stockArr = tanks.toArray(stockArr);
		return stockArr;
	}
	
	class CoordinatorThread implements Runnable
	{

		public void run() {
			
			while(_active)
			{
				try {
					_pastryNode.getEnvironment().getTimeSource().sleep(50);
					sendFrameUpdate();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
}