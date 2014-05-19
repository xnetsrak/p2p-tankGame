package org.tank.Logger;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.tank.Model.Model;


public class AutoDriver implements Runnable
{
	private Model model;
	private Random myRand = new Random();
	private int randomInt100;			// Used to randomize direction, etc. 
	private int chgDirProb = 20;		// The percent probability of whether the tank must change direction

	private int up = 0;					// Directions...    0 = UP	1 = RIGHT	2 = DOWN	3 = LEFT
	private int right = 1;
	private int down = 2;
	private int left = 3;
	
	public AutoDriver(Model _model) {
		model = _model;
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
				
		int currentDirection = up;	// The tanks current direction
		int nextAction;				// The action to invoke next
		int nextDirection;			// The direction to go next (used in case of "move" action)
		
		
		while (true) {
			randomInt100 = myRand.nextInt(101);
			
			// Choose the next action... 0 = no action	1 = move	2 = shoot
			if (randomInt100 < 34) { nextAction = 0; } 
			else if (randomInt100 < 67) { nextAction = 1; }
			else { nextAction = 2; }

			switch (nextAction) {
				case 0: { // No Action
					break;
				}
				case 1: { // Move
					if (randomInt100 < chgDirProb) {
						nextDirection = newDirection(currentDirection);
					} else {
						nextDirection = currentDirection;
					}
					switch (nextDirection) {
					case 0: { // UP
						if (hero.y < model.getGameHeight()-30) model.changeHeroDirection(2);			
						break;
					}
					case 1: { // RIGHT
						break;
					}
					case 2: { // DOWN
						
					}
					case 3: { // LEFT
						
					}
				}
				case 2: { // Shoot
					break;
				}

		}
			
			
			
			if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && _hero.y < _model.getGameHeight()-30) 
				_model.changeHeroDirection(2);
			
			else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && _hero.y > 0) 
				_model.changeHeroDirection(0);
			
			else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && _hero.x > 0) 
				_model.changeHeroDirection(3);
			
			else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && _hero.x < _model.getGameWidth()-30) 
				_model.changeHeroDirection(1);
			
			
			//this.repaint();
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE)
				_model.shotEnemy();
	}
}
	
