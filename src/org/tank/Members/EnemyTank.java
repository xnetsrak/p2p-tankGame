package org.tank.Members;

import java.awt.Color;

public class EnemyTank extends Tank {

	public EnemyTank(int x, int y, int direction, int gameWidth, int gameHeight) {
		super(x, y, direction, gameWidth, gameHeight);
		
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		this.color = new Color(r, g, b);
	}
}
