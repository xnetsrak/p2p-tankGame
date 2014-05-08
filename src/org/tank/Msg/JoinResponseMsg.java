package org.tank.Msg;

import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.NodeHandle;

public class JoinResponseMsg extends MyMsg 
{
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int direction;
	public boolean isCoordinator;
	public int frameNumber;
	public NodeHandle fromNodeHandle;

	public JoinResponseMsg(Id from, Id to, NodeHandle fromNodeHandle, boolean isCoordinator, int frameNumber) 
	{
		super(from, to, "JoinResponse");
		this.isCoordinator = isCoordinator;
		this.frameNumber = frameNumber;
		this.fromNodeHandle = fromNodeHandle;
	}
	
	public void setPosistion(int x, int y, int direction)
	{
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

}
