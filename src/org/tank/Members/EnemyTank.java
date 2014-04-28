package org.tank.Members;

import java.util.Random;
import java.util.Vector;

public class EnemyTank extends Tank {

	public Vector<Shot> s = new Vector<Shot>();

	public EnemyTank(int x, int y, int direction, int gameWidth, int gameHeight) {
		super(x, y, direction, gameWidth, gameHeight);
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

	/*public void run() {

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
					if (p < 40 && this.s.size() < 3) {
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

					if (p < 40 && this.s.size() < 3)
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
					if (p < 40 && this.s.size() < 3)
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
					if (p < 40 && this.s.size() < 3)
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
	}*/
}
