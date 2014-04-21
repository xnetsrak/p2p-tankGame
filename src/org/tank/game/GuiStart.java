package org.tank.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GuiStart extends JFrame implements KeyListener
{
	private static final long serialVersionUID = 1L;
	private DrawPanel drawPanel = new DrawPanel();

	private boolean backwards = false;
	private boolean forward = false;
	private boolean turnRight = false;
	private boolean turnLeft = false;
	private int tankX = 10;
	private int tankY = 10;
	private double tankAngle = 0.0;

	public GuiStart()
	{
		 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 this.setTitle("P2P Tanks");
		 this.setSize(500,500);
	     this.setLocationRelativeTo(null);

		 this.addKeyListener(this);
	     this.getContentPane().add(drawPanel);
	}
	
	
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == 38) //Forward
			forward = true;
		else if(e.getKeyCode() == 40) //back
			backwards = true;
		else if(e.getKeyCode() == 39) //turn right
			turnRight = true;
		else if(e.getKeyCode() == 37) //turn left
			turnLeft = true;
		
		drawPanel.repaint();
	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == 38)
			forward = false;
		else if(e.getKeyCode() == 40)
			backwards = false;
		else if(e.getKeyCode() == 39)
			turnRight = false;
		else if(e.getKeyCode() == 37)
			turnLeft = false;
		
		drawPanel.repaint();
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class DrawPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public DrawPanel()
		{
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
			  
			  g2.setColor(Color.red);
			  
			  if(forward)
				  tankX += 5;
			  else if(backwards)
				  tankX -= 5;
			  else if(turnRight)
				  tankAngle += 1.05;
			  else if(turnLeft)
				  tankAngle -= 1.05;
			  //Rectangle rect2 = new Rectangle(tankX, tankY, 70, 50);
			  Rectangle rect = new Rectangle(tankX,tankY,70, 50);
			  //g2.rotate(tankAngle, tankX+35, tankY+25);
			  
			  g2.translate(rect.x+(rect.width/2), rect.y+(rect.height/2));
			  g2.rotate(Math.toRadians(tankAngle));
			  //g2.rotate(0 - tankAngle, rect.getX(), rect.getY() + 5);

			  //AffineTransform at = new AffineTransform();
              //at.rotate(Math.toRadians(tankAngle), 70, 50);
              //g2.setTransform(at);
			  
			  g2.draw(rect);
			  g2.fill(rect);	
			  
		}
	}
}