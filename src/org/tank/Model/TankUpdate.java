package org.tank.Model;

import java.io.Serializable;

public class TankUpdate implements Serializable, Comparable
{
	private static final long serialVersionUID = 796353740189837900L;
	public int x;
	public int y;
	public int w;
	public rice.p2p.commonapi.Id Id;
	public boolean fireShot = false;
	public int points;
	public boolean isCoordinator = false;
	
	public TankUpdate(int x, int y, int w, rice.p2p.commonapi.Id id, boolean shot, int points, boolean isCoordinator)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.Id = id;
		this.fireShot = shot;
		this.points = points;
		this.isCoordinator = isCoordinator;
	}
	
	@Override
	public boolean equals(Object o) {
		
		TankUpdate obj = (TankUpdate)o;
		if(this.x == obj.x &&
		   this.y == obj.y &&
		   this.w == obj.w &&
		   this.Id.equals(obj.Id) &&
		   this.fireShot == obj.fireShot &&
		   this.points == obj.points &&
		   this.isCoordinator == obj.isCoordinator)
				return true;
		
		return false;
		
	}

	public int compareTo(Object o)
	{
		TankUpdate obj = (TankUpdate)o;
		return this.Id.compareTo(obj.Id);
	}
}
