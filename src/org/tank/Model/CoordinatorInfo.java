package org.tank.Model;

import org.tank.Msg.CoordinatorUpdateMsg;

public class CoordinatorInfo 
{
	public int frameNumber;
	public CoordinatorUpdateMsg updateMsg = null;
	
	public CoordinatorInfo(int frameNumber, CoordinatorUpdateMsg msg)
	{
		this.frameNumber = frameNumber;
		this.updateMsg = msg;
	}
	
	public void clearLastMessage()
	{
		updateMsg = null;
	}
	public boolean msgRecived()
	{
		return updateMsg != null;
	}
}
