package org.tank.Members;

public class Tank {

	// horizontal coordinate value for tank
	public int x = 0;
	public int color;
	public boolean isLive = true;
	public int direct = 1;
	public int speed = 6;
	public int shotSpeed = 12;
	public int gameWidth;
	public int gameHeight;
	public boolean hasMoved = false;
	
	// vertical coordinate value
	public int y = 0;
	
	public Tank(int x, int y, int w, int gameWidth, int gameHeight) 
	{
		this.x = x;
		this.y = y;
		this.direct = w;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
	}
	
	public void updatePosistion(int x, int y, int w)
	{
		this.x = x; 
		this.y = y;
		this.direct = w;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
