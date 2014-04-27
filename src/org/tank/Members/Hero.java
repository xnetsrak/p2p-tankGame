package org.tank.Members;

import java.util.Vector;


public class Hero extends Tank {

	public Vector<Shot> s = new Vector<Shot>();

	public Hero(int x, int y, int w) {
		super(x, y, w);
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
