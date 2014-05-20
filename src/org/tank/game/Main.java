package org.tank.game;

import org.tank.Logger.Logger;

public class Main 
{

	public static void main(String[] args) 
	{
		Boolean isDummyTank = false;
		int localPort = 0;
		String bootIPAddress = "";
		int remotePort = 0;
		
		if (args.length > 3) {
			isDummyTank = (args[0].toUpperCase().equals("TEST"));
			localPort = Integer.parseInt(args[1]);
			bootIPAddress = args[2];
			remotePort = Integer.parseInt(args[3]);
			System.out.println("========== Local Port..... " + localPort);
			System.out.println("========== Boot IP adr.... " + bootIPAddress);
			System.out.println("========== Boot Port...... " + remotePort);
		}
		
		tankgame myGame = new tankgame(isDummyTank, localPort, bootIPAddress, remotePort);
		// Logger logger = new Logger(args);
    }
}
