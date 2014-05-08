package org.tank.Model;

import rice.p2p.commonapi.NodeHandle;

public class TankPosUpdateScribeMsg extends MyScribeMsg
{
	private static final long serialVersionUID = 1L;
	int x;
	int y;
	int direction;
	
	public TankPosUpdateScribeMsg(NodeHandle from, int seq) {
		super(from, seq);
		
	}
	
	public void setPosistion(int x, int y, int direction)
	{
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

}
