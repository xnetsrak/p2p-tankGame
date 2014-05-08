package org.tank.Msg;

import rice.p2p.commonapi.NodeHandle;

public class JoinScribeMsg extends MyScribeMsg
{
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int direction;

	public JoinScribeMsg(NodeHandle from, int seq) {
		super(from, seq);
		// TODO Auto-generated constructor stub
	}

	public void setPosistion(int x, int y, int direction)
	{
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
}
