package org.tank.Msg;

import rice.p2p.commonapi.NodeHandle;

public class LeaveScribeMsg extends MyScribeMsg {

	private static final long serialVersionUID = 1L;

	public LeaveScribeMsg(NodeHandle from, int seq) {
		super(from, seq);
	}

}
