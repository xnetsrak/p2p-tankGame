package org.tank.Model;

import org.tank.Msg.CoordinatorUpdateMsg;

public class CoordinatorInfo 
{
	public int frameNumber;
	private CoordinatorUpdateMsg updateMsg = null;
	
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
	public void setUpdateMsg(CoordinatorUpdateMsg msg)
	{
		this.updateMsg = msg;
	}
	public CoordinatorUpdateMsg getUpdateMsg()
	{
		return updateMsg;
	}
}
