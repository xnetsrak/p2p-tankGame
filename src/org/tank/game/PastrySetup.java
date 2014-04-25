package org.tank.game;
import java.io.IOException;
import java.net.InetSocketAddress;

import rice.environment.Environment;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;


public class PastrySetup 
{
	 
	public PastrySetup(int bindport, InetSocketAddress bootaddress, Environment env) throws Exception
	{
		// Generate the NodeIds Randomly
	    NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
	    
	    // construct the PastryNodeFactory, this is how we use rice.pastry.socket
	    PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
	
	    // construct a node
	    PastryNode node = factory.newNode();
	      
	    // construct a new MyApp
	    PastryApp app = new PastryApp(node);    
	    
	    node.boot(bootaddress);
	    
	    // the node may require sending several messages to fully boot into the ring
	    synchronized(node) {
	      while(!node.isReady() && !node.joinFailed()) {
	        // delay so we don't busy-wait
	        node.wait(500);
	        
	        // abort if can't join
	        if (node.joinFailed()) {
	          throw new IOException("Could not join the FreePastry ring.  Reason:"+node.joinFailedReason()); 
	        }
	      }       
	    }
	    
	    System.out.println("Finished creating new node "+node);
	}
}
