package org.tank.game;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;


public class CreateJoinPastryDialog extends JFrame
  {
	  private static final long serialVersionUID = 1L;
	  private JTextField txtMyPort;
	  private JTextField txtBootIp;
	  private JTextField txtBootPort;
	  
	  private JLabel labMyPort;
	  private JLabel labBootIp;
	  private JLabel labBootPort;
	  
	  private JButton cmdButton;
	  private tankgame _game;
	  
	  public CreateJoinPastryDialog(tankgame game)
	  {
		  this._game = game;
		  this.setSize(500, 150);
		  this.setTitle("Create/Join network Game");
		  //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  this.setLocationRelativeTo(null);
		  this.setResizable(false);
		  this.setLayout(new BorderLayout());
		  
		  labMyPort = new JLabel("Your Port");
		  labBootIp = new JLabel("Bootstrap IP");
		  labBootPort = new JLabel("Bootstrap Port");
		  
		  txtMyPort = new JTextField(20);
		  txtBootIp = new JTextField(20);
		  txtBootPort = new JTextField(20);
		  txtMyPort.setText("9000");
		  txtBootPort.setText("9000");
		  //txtBootIp.setText("10.192.0.119");
		  
		  cmdButton = new JButton("Join/Create");
		  
		  GridLayout gridLayout = new GridLayout(4,3);
		  gridLayout.setHgap(10);
		  
		  JPanel panel = new JPanel(gridLayout);
		  Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		  panel.setBorder(padding);
		  
		  panel.add(labMyPort);
		  panel.add(labBootIp);
		  panel.add(labBootPort);
		  
		  panel.add(txtMyPort);
		  panel.add(txtBootIp);
		  panel.add(txtBootPort);
		  
		  panel.add(new JLabel());
		  panel.add(new JLabel());
		  panel.add(new JLabel());
		  
		  panel.add(new JLabel());
		  panel.add(cmdButton);
		  cmdButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				// Validation here.
				try
				{
					int myPort = Integer.parseInt(txtMyPort.getText().trim());
					String bootIP = txtBootIp.getText().trim();
					int bootPort = Integer.parseInt(txtBootPort.getText().trim());
					
					_game.joinCreateGame(myPort, bootIP, bootPort);
					dispose();
				}
				catch(Exception ex)
				{
					
				}
			}
		});

		  this.add(panel);
		  this.setVisible(true);
	  }

  }
