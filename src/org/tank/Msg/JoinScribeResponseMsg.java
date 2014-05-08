package org.tank.Msg;

import rice.p2p.commonapi.NodeHandle;

public class JoinScribeResponseMsg extends MyScribeMsg
{
	private static final long serialVersionUID = 1L;

	public JoinScribeResponseMsg(NodeHandle from, int seq) {
		super(from, seq);
		// TODO Auto-generated constructor stub
	}

}
