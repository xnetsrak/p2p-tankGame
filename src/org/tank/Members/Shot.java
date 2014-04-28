package org.tank.Members;

public class Shot implements Runnable {
	public int x, y, direct, speed, gameWidth, gameHeight;
	public boolean isLive = true;

	public Shot(int x, int y, int direct, int speed, int gameWidth, int gameHeight) {
		this.x = x;
		this.y = y;
		this.direct = direct;
		this.speed = speed;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
	}

	public void run() {
		while (isLive) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			switch (direct) {
			case 0:
				y -= speed;
				break;
			case 1:
				x += speed;
				break;
			case 2:
				y += speed;
				break;
			case 3:
				x -= speed;
				break;
			}
			if (x < 0 || x > gameWidth || y < 0 || y > gameHeight) {
				isLive = false;
			}
		}
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

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}
}
