package org.tank.Model;

import rice.p2p.commonapi.Id;

public class ShotMsg extends MyMsg 
{
	private static final long serialVersionUID = 1L;

	public ShotMsg(Id from, Id to) 
	{
		super(from, to, "ShotMsg");
		
	}

}
