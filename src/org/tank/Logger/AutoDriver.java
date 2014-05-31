package org.tank.Logger;

import java.util.Random;
import org.tank.Members.Hero;
import org.tank.Model.Model;


public class AutoDriver implements Runnable
{
	private Model model;
	private Hero hero;
	private Random myRand = new Random();
	private int randomInt100;			// Used to randomize direction, etc. 
	private int chgDirProb = 10;		// The percent probability of whether the tank must change direction

	private int up = 0;					// Directions...    0 = UP	1 = RIGHT	2 = DOWN	3 = LEFT
	private int right = 1;
	private int down = 2;
	private int left = 3;
	
	public AutoDriver(Model _model) {
		model = _model;
		hero = model.getHero();
	}

	public int newDirection(int curDir) {
		int newDir = 0;
		
		randomInt100 = myRand.nextInt(101);
		switch (curDir) {
			case 0: { // If current direction is UP
				if (randomInt100 < 34) { newDir = right; }
				else if (randomInt100 < 67) { newDir = down; }
				else { newDir = left; }
				break;
			}
			case 1: { // If current direction is RIGHT
				if (randomInt100 < 34) { newDir = up; }
				else if (randomInt100 < 67) { newDir = down; }
				else { newDir = left; }
				break;
			}
			case 2: { // If current direction is DOWN
				if (randomInt100 < 34) { newDir = up; }
				else if (randomInt100 < 67) { newDir = right; }
				else { newDir = left; }
				break;
			}
			case 3: { // If current direction is LEFT
				if (randomInt100 < 34) { newDir = up; }
				else if (randomInt100 < 67) { newDir = right; }
				else { newDir = down; }
				break;
			}
		}
		return newDir;
	}
	
	public void run() {
				
		int currentDirection = up;	// The tank's current direction
		int nextAction;				// The action to invoke next
		int nextDirection;			// The direction to go next (used in case of "move" action)
		
		
		while (true) {
			randomInt100 = myRand.nextInt(101);
			
			// Choose the next action... 0 = no action	1 = move	2 = shoot
			if (randomInt100 < 45) { nextAction = 0; } 
			else if (randomInt100 < 90) { nextAction = 1; }
			else { nextAction = 2; }

			switch (nextAction) {
				case 0: { // No Action
					break;
				}
				case 1: { // Move
					randomInt100 = myRand.nextInt(101);
					if (randomInt100 < chgDirProb) {
						nextDirection = newDirection(currentDirection);
					} else {
						nextDirection = currentDirection;
					}
					currentDirection = nextDirection;
					switch (nextDirection) {
						case 0: { // UP
							if (hero.y > 0) model.changeHeroDirection(0);
							break;
						}
						case 1: { // RIGHT
							if (hero.x < model.getGameWidth()-30) model.changeHeroDirection(1);
							break;
						}
						case 2: { // DOWN
							if (hero.y < model.getGameHeight()-30) model.changeHeroDirection(2);			
							break;
						}
						case 3: { // LEFT
							if (hero.x > 0) model.changeHeroDirection(3);
							break;
						}
					}
					break;
				}
				case 2: { // Shoot
					model.shotEnemy();
					break;
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} // End of While loop

	}
	
}
	
