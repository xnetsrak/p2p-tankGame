package org.tank.Members;

public class Bomb {

	public int x, y;
	public int life = 9;
	public boolean isLive = true;
	
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

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}


	public Bomb(int x, int y) {
		this.x = x;
		this.y = y;
		this.life = 9;
	}

	public void lifeDown() {
		if (life > 0)
			life--;
		else
			this.isLive = false;
	}
}