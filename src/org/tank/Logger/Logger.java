package org.tank.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;

import org.tank.Model.PastryApp;
import org.tank.Msg.CoordinatorUpdateMsg;
import org.tank.Msg.JoinScribeMsg;
import org.tank.Msg.LeaveScribeMsg;
import org.tank.Msg.MyScribeMsg;

import rice.environment.Environment;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.commonapi.PastryIdFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;

@SuppressWarnings("deprecation")
public class Logger implements Application, ScribeClient
{
	protected Endpoint endpoint;
	private PastryNode pastryNode;
	Scribe scribeNode;
	Topic scribeTopic;
	private ArrayList<NodeHandle> _coordinatorIds = new ArrayList<NodeHandle>();
	private int seqNum = 0;		// A sequence number used in the Scribe part of the code
	
	private String logFileName = "";
	private File logFile;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private Date now;
	private long previousFrameStart = 0;
	private long frameLength;
	
	public Logger(String[] args)
	{
		int localPort = Integer.parseInt(args[0]);
		String bootIPAddress = args[1];
		int remotePort = Integer.parseInt(args[2]);
		System.out.println("========== Local Port..... " + localPort);
		System.out.println("========== Boot IP adr.... " + bootIPAddress);
		System.out.println("========== Boot Port...... " + remotePort);
		if (args[3] != null) {
			logFileName = "c:\\" + args[3];
			System.out.println("========== Log File....... " + logFileName);

			logFile = new File(logFileName);
			now = new Date();
			try {
				fileWriter = new FileWriter(logFile, false); 	// false => overwriting existing file
				bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write("============================================================================================");
				bufferedWriter.newLine();
				bufferedWriter.write(now.toString());
				bufferedWriter.write("          Logger app started");
				bufferedWriter.newLine();
				bufferedWriter.write("============================================================================================");
				bufferedWriter.newLine();
				// bufferedWriter.close();
				// fileWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
		pastryNode = null;
		scribeNode = null;
		
		try {
			scribeNode = setupScribeNode(localPort, bootIPAddress, remotePort, "Zone1Topic");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (scribeNode != null) {
			System.out.println("========== Node has joined the Scribe group!");
		}
		else System.out.println("========== Node did NOT join the Scribe group!");		
	}

	public Scribe setupScribeNode(int bindPort, String bootAddress, int bootPort, String scribeTopicName) throws Exception
	{
		// Create node environment
	    Environment env = new Environment();
	    
	    Scribe scribeNode;

	    // Disable the UPnP setting (in case you are testing this on a NATted LAN)
	    env.getParameters().setString("nat_search_policy","never");
	    
	    try 
	    {
	    	// Set the IP port number to use locally
			int localPort = bindPort;
			  
			// Set IP address + port of remote bootstrap node - and construct socket address
			InetAddress bootaddr = InetAddress.getByName(bootAddress);  // bootAddress can be either host name or textual repr. of IP adr
			int bootport = bootPort;
			InetSocketAddress bootaddress = new InetSocketAddress(bootaddr,bootport);
			
			// Construct factory that generates NodeIds Randomly
		    NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
		    
		    // Construct PastryNodeFactory, which uses sockets (rice.pastry.socket)
		    PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, localPort, env);
		
		    // Construct a node
		    pastryNode = factory.newNode();
		    
		    // Construct a new Pastry application for this node
		    // LoggerPastryApp logApp = new LoggerPastryApp(pastryNode);    
		    
		    // Boot the node (attempting to contact bootstrap node)
		    pastryNode.boot(bootaddress);
		    
		    // The node may require sending several messages to fully boot into the ring
		    synchronized(pastryNode) {
		    	while(!pastryNode.isReady() && !pastryNode.joinFailed()) {
		    		// delay so we don't busy-wait
		    		pastryNode.wait(500);
		    		// abort if can't join
		    		if (pastryNode.joinFailed()) {
		    			throw new IOException("Could not join the FreePastry ring.  Reason:"+pastryNode.joinFailedReason()); 
		    		}
		    	}       
		    }
	    
	    } catch (Exception e) {
	    	// remind user how to use
		    System.out.println("Usage:"); 
		    System.out.println("java [-cp FreePastry-<version>.jar] rice.tutorial.lesson3.DistTutorial localbindport bootIP bootPort");
		    System.out.println("example java rice.tutorial.DistTutorial 9001 pokey.cs.almamater.edu 9001");
		    throw e; 
	    }
	    
	    if (pastryNode != null) {
	    	System.out.println("Pastry node created... " + pastryNode);
	    } else {
	    	System.out.println("<<<< Pastry node = null! >>>>");
	    }
	    
	    // Construct a FreePastry endpoint
	    this.endpoint = pastryNode.buildEndpoint(this, "myinstance");
	    // Construct a Scribe node
	    scribeNode = new ScribeImpl(pastryNode,"myScribeInstance");
    	System.out.println("Scribe node created... " + scribeNode);

	    // Construct the topic for the Scribe group
	    scribeTopic = new Topic(new PastryIdFactory(pastryNode.getEnvironment()), scribeTopicName);
	    System.out.println("scribeTopic = "+scribeTopicName);
	    scribeNode.subscribe(scribeTopic, this);
	    this.endpoint.register();  // Now we can receive messages

	    System.out.println("Scribe node '" + scribeNode + "' configured for topic '" + scribeTopicName + "'");
	    logEntry(-1, System.currentTimeMillis());
	    return scribeNode;
	}

	private void logEntry(int frameNo, long startTime) throws IOException {
		frameLength = startTime - previousFrameStart;
		previousFrameStart = startTime;
		System.out.format("Frame %9d" + " | Start %16d" + " | Frame Length %10d" + "%n", frameNo, startTime, frameLength);
		bufferedWriter.write("Frame " + frameNo + "   |   Start " + startTime + "   |   Frame Length " + frameLength);
		bufferedWriter.newLine();
		bufferedWriter.flush();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Below are methods for the "ScribeClient" interface + a couple of additional ones.
	// ----------------------------------------------------------------------------------------------------------------

	/* Sends the multicast message.*/
	public void sendMulticast(MyScribeMsg msg) {
		System.out.println("Node "+endpoint.getLocalNodeHandle()+" broadcasting "+seqNum);
		scribeNode.publish(scribeTopic, msg);
	}
	  
	public boolean anycast(Topic arg0, ScribeContent arg1) {
	    System.out.println("The Scribe 'anycast' method was called!");
		return false;
	}

	public void childAdded(Topic arg0, NodeHandle arg1) {
	    System.out.println("The Scribe 'childAdded' method was called!");
	}

	public void childRemoved(Topic arg0, NodeHandle arg1) {
	    System.out.println("The Scribe 'childRemoved' method was called!");		
	}

	public void deliver(Topic arg0, ScribeContent message)
	{
		MyScribeMsg msg = (MyScribeMsg)message;
		CoordinatorUpdateMsg updMsg;
		int frameNumber, lastLoggedFrameNumber = -1;
		long startTime;
	    
		if(msg instanceof CoordinatorUpdateMsg) {
			updMsg = (CoordinatorUpdateMsg)msg;
			frameNumber = updMsg.newFrameNumber;
			if (frameNumber > lastLoggedFrameNumber) {
				lastLoggedFrameNumber = frameNumber;
				startTime = System.currentTimeMillis();
				try {
					logEntry(frameNumber, startTime);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }
	}
	
	public boolean isRoot() {
		return scribeNode.isRoot(scribeTopic);
	}

	public void subscribeFailed(Topic arg0) {
	    System.out.println("The 'subscribeFailed' method was called!");
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Below are methods for the "Application" interface, which are mainly not implemented, as Logger only uses the 
	// ScribeClient part.
	// ----------------------------------------------------------------------------------------------------------------

	public void deliver(Id arg0, Message arg1) {
	    System.out.println("The Pastry 'deliver' method was called!");
	}

	public boolean forward(RouteMessage arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	public void update(NodeHandle arg0, boolean arg1) {
	    System.out.println("The Pastry 'update' method was called!");
	}	

	public String toString() {
	    return "MyApp "+this.endpoint.getId();
	}

}
