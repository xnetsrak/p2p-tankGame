package org.tank.Members;

import java.awt.Color;

public class Hero extends Tank {

	public Hero(int x, int y, int w, int gameWidth, int gameHeight) {
		super(x, y, w, gameWidth, gameHeight);
		this.color = Color.yellow;
	}
}
