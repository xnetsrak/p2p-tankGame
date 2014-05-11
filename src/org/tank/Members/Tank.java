package org.tank.Members;

import java.awt.Color;
import java.util.Random;
import java.util.Vector;

public class Tank {

	// horizontal coordinate value for tank
	public int x = 0;
	public Color color;
	public boolean isLive = true;
	public int direct = 1;
	public int speed = 6;
	public int shotSpeed = 12;
	public int gameWidth;
	public int gameHeight;
	public boolean hasMoved = false;
	public int points = 0;
	Random rand = new Random();
	
	// vertical coordinate value
	public int y = 0;
	
	public Vector<Shot> s = new Vector<Shot>();
	
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
	
	public boolean hasNotFiredShots()
	{
		for(Shot shot : s)
		{
			if(!shot.isFired)
			{
				shot.isFired = true;
				return true;
			}
		}
		return false;
	}
	
	public void shotEnemy() {
		switch (this.getDirect()) {

		case 0:
			Shot s1 = new Shot(x + 9, y - 4, 0, this.shotSpeed, this.gameWidth, this.gameHeight);
			Thread t1 = new Thread(s1);
			t1.start();
			s.add(s1);
			break;

		case 1:
			Shot s2 = new Shot(x + 40, y + 15, 1, this.shotSpeed, this.gameWidth, this.gameHeight);
			Thread t2 = new Thread(s2);
			t2.start();
			s.add(s2);
			break;

		case 2:
			Shot s3 = new Shot(x + 9, y + 28, 2, this.shotSpeed, this.gameWidth, this.gameHeight);
			Thread t3 = new Thread(s3);
			t3.start();
			s.add(s3);
			break;

		case 3:
			Shot s4 = new Shot(x - 5, y + 15, 3, this.shotSpeed, this.gameWidth, this.gameHeight);
			Thread t4 = new Thread(s4);
			t4.start();
			s.add(s4);
			break;
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
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
