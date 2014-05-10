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
}
