package org.tank.Model;

import rice.p2p.commonapi.Id;

public class TankPositionUpdateMsg extends MyMsg
{
	private static final long serialVersionUID = 1L;
	
	int x;
	int y;
	int direction;

	public TankPositionUpdateMsg(Id from, Id to) 
	{
		super(from, to, "PosUpdate");
		
	}
	
	public void setPosistion(int x, int y, int direction)
	{
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

}
