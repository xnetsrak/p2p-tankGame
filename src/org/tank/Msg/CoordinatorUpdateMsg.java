package org.tank.Msg;

import org.tank.Model.TankUpdate;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

public class CoordinatorUpdateMsg extends MyScribeMsg
{
	private static final long serialVersionUID = 1L;
	public TankUpdate[] _tanks;

	public CoordinatorUpdateMsg(NodeHandle from, int seq) {
		super(from, seq);
		// TODO Auto-generated constructor stub
	}
	
	public void setTank(TankUpdate[] tanks)
	{
		_tanks = tanks;
	}
}
