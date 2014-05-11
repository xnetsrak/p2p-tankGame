package org.tank.Model;

import java.io.Serializable;

public class TankUpdate implements Serializable
{
	private static final long serialVersionUID = 796353740189837900L;
	public int x;
	public int y;
	public int w;
	public rice.p2p.commonapi.Id Id;
	public boolean fireShot = false;
	public int points;
	
	public TankUpdate(int x, int y, int w, rice.p2p.commonapi.Id id, boolean shot, int points)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.Id = id;
		this.fireShot = shot;
		this.points = points;
	}
}
