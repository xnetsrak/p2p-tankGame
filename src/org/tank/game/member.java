package org.tank.game;

import java.util.Vector;
import java.util.Random;

public class member {
}

class Tank {

	// horizontal coordinate value for tank
	int x = 0;
	int color;
	boolean isLive = true;
	int direct = 1;
	int speed = 2;

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	// 0 represents up, 1=right, 2=down, 3=left

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

	// vertical coordinate value
	int y = 0;

	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

class Shot implements Runnable {
	int x, y, direct, speed;
	boolean isLive = true;

	Shot(int x, int y, int direct, int speed) {
		this.x = x;
		this.y = y;
		this.direct = direct;
		this.speed = speed;
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
			if (x < 0 || x > 400 || y < 0 || y > 300) {
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

class Hero extends Tank {

	Vector<Shot> s = new Vector<Shot>();

	public Hero(int x, int y) {
		super(x, y);
		this.x = 0;
		this.y = 100;
	}

	public void shotEnemy() {
		switch (this.getDirect()) {

		case 0:
			Shot s1 = new Shot(x + 9, y - 4, 0, this.getSpeed() * 6);
			Thread t1 = new Thread(s1);
			t1.start();
			s.add(s1);
			break;

		case 1:
			Shot s2 = new Shot(x + 40, y + 15, 1, this.getSpeed() * 6);
			Thread t2 = new Thread(s2);
			t2.start();
			s.add(s2);
			break;

		case 2:
			Shot s3 = new Shot(x + 9, y + 28, 2, this.getSpeed() * 6);
			Thread t3 = new Thread(s3);
			t3.start();
			s.add(s3);
			break;

		case 3:
			Shot s4 = new Shot(x - 5, y + 15, 3, this.getSpeed() * 6);
			Thread t4 = new Thread(s4);
			t4.start();
			s.add(s4);
			break;
		}
	}
}

class EnemyTank extends Tank implements Runnable {

	Vector<Shot> s = new Vector<Shot>();

	public EnemyTank(int x, int y) {
		super(x, y);
		Random random = new Random();
		int w = random.nextInt(4);
		this.direct = w;
	}

	public void shotEnemy() {

		switch (this.getDirect()) {
		case 0:
			Shot s1 = new Shot(x + 9, y - 4, 0, this.getSpeed() * 6);
			Thread t1 = new Thread(s1);
			t1.start();
			s.add(s1);
			break;
		case 1:
			Shot s2 = new Shot(x + 40, y + 15, 1, this.getSpeed() * 6);
			Thread t2 = new Thread(s2);
			t2.start();
			s.add(s2);
			break;
		case 2:
			Shot s3 = new Shot(x + 9, y + 28, 2, this.getSpeed() * 6);
			Thread t3 = new Thread(s3);
			t3.start();
			s.add(s3);
			break;
		case 3:
			Shot s4 = new Shot(x - 5, y + 15, 3, this.getSpeed() * 6);
			Thread t4 = new Thread(s4);
			t4.start();
			s.add(s4);
			break;
		}
	}

	public void run() {

		while (true) {

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Random randomnumber = new Random();
			int w = randomnumber.nextInt(4);
			int p;
			this.setDirect(w);

			switch (this.direct) {
			case 0:
				for (int i = 0; i < 30; i++) {

					p = randomnumber.nextInt(100);
					if (p == 0 && this.s.size() < 3) {
						this.shotEnemy();
					}
					if (y > 0)
						this.y -= this.speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				for (int i = 0; i < 30; i++) {

					p = randomnumber.nextInt(100);

					if (p == 0 && this.s.size() < 3)
						this.shotEnemy();
					if (x < 380)
						this.x += this.speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				for (int i = 0; i < 30; i++) {
					p = randomnumber.nextInt(100);
					if (p == 0 && this.s.size() < 3)
						this.shotEnemy();
					if (y < 280)
						this.y += this.speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3:
				for (int i = 0; i < 30; i++) {
					p = randomnumber.nextInt(100);
					if (p == 0 && this.s.size() < 3)
						this.shotEnemy();
					if (x > 0)
						this.x -= this.speed;
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}

class Bomb {

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

	int x, y;
	int life = 9;
	boolean isLive = true;

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
