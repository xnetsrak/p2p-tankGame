package org.tank.Msg;

import org.tank.Model.TankUpdate;

import rice.p2p.commonapi.Id;

public class TankPositionUpdateMsg extends MyMsg
{
	private static final long serialVersionUID = 1L;
	public TankUpdate tankUpdate;

	public TankPositionUpdateMsg(Id from, Id to, TankUpdate tankUpdate) 
	{
		super(from, to, "TankUpdate");
		this.tankUpdate = tankUpdate;
	}

}
