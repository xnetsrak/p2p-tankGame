package org.tank.Msg;

import org.tank.Model.TankUpdate;

import rice.p2p.commonapi.Id;

public class TankPositionUpdateMsg extends MyMsg
{
	private static final long serialVersionUID = 1L;
	public TankUpdate tankUpdate;
	public int frameNumber;
	public boolean leave = false;

	public TankPositionUpdateMsg(Id from, Id to, TankUpdate tankUpdate, int frameNumber) 
	{
		super(from, to, "TankUpdate");
		this.tankUpdate = tankUpdate;
		this.frameNumber = frameNumber;
	}

}
