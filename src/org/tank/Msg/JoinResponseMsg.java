package org.tank.Msg;

import rice.p2p.commonapi.Id;

public class JoinResponseMsg extends MyMsg 
{
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int direction;
	public boolean isCoordinator;

	public JoinResponseMsg(Id from, Id to, boolean isCoordinator) 
	{
		super(from, to, "JoinResponse");
		this.isCoordinator = isCoordinator;
	}
	
	public void setPosistion(int x, int y, int direction)
	{
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

}
