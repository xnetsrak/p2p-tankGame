package org.tank.Model;

import rice.p2p.commonapi.NodeHandle;
import rice.p2p.scribe.ScribeContent;

public class MyScribeMsg implements ScribeContent
{
	private static final long serialVersionUID = 1L;

	NodeHandle from;

	int seq;

	public MyScribeMsg(NodeHandle from, int seq) 
	{
		this.from = from;
		this.seq = seq;
	}

	public String toString() {
		return "MyScribeContent #"+seq+" from "+from;
	} 
}
