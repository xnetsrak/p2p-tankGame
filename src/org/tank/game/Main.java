package org.tank.game;

import org.tank.Logger.Logger;

public class Main 
{

	public static void main(String[] args) 
	{
		Boolean isDummyTank = false;
		if (args[0] != null) isDummyTank = (args[0].toUpperCase() == "TEST");
		
		tankgame myGame = new tankgame(isDummyTank);
		// Logger logger = new Logger(args);
    }
}
