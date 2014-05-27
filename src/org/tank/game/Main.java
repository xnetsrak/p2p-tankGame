package org.tank.game;

public class Main 
{

	public static void main(String[] args) 
	{
		Boolean isDummyTank = false;
		int localPort = 0;
		String bootIPAddress = "";
		int remotePort = 0;
		
		if (args.length == 0) {
			tankgame myGame = new tankgame(isDummyTank, localPort, bootIPAddress, remotePort);
		}
		else if ((args.length > 3) && (args[0].toUpperCase().equals("TEST"))) 
		{ 
			// Start autodriven player
			localPort = Integer.parseInt(args[1]);
			bootIPAddress = args[2];
			remotePort = Integer.parseInt(args[3]);
			System.out.println();
			System.out.println("========== Local Port..... " + localPort);
			System.out.println("========== Boot IP adr.... " + bootIPAddress);
			System.out.println("========== Boot Port...... " + remotePort);
			System.out.println();
			isDummyTank = true;
			tankgame myGame = new tankgame(isDummyTank, localPort, bootIPAddress, remotePort);
		} 
    }
}
