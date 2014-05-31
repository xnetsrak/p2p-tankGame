package org.tank.Msg;

import org.tank.Model.TankUpdate;

import rice.p2p.commonapi.NodeHandle;

public class CoordinatorUpdateMsg extends MyScribeMsg
{
	private static final long serialVersionUID = 1L;
	public TankUpdate[] _tanks;
	public int oldFrameNumber;
	public int newFrameNumber;

	public CoordinatorUpdateMsg(NodeHandle from, int seq, int oldFrameNumber, int newFrameNumber) {
		super(from, seq);
		this.oldFrameNumber = oldFrameNumber;
		this.newFrameNumber = newFrameNumber;
	}
	
	public void setTank(TankUpdate[] tanks)
	{
		_tanks = tanks;
	}
	
	@Override
	public boolean equals(Object o) {
		
		CoordinatorUpdateMsg obj = (CoordinatorUpdateMsg) o;
		
		if(!(oldFrameNumber == obj.oldFrameNumber && newFrameNumber == obj.newFrameNumber && _tanks.length == obj._tanks.length))
			return false;
		
		for(int i = 0; i < _tanks.length; i++)
		{
			TankUpdate tu1 = _tanks[i];
			TankUpdate tu2 = obj._tanks[i];
			if(!tu1.equals(tu2))
				return false;
		}
		
		return true;
	}
}
