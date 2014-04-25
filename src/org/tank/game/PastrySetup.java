package org.tank.game;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import rice.environment.Environment;
import rice.pastry.Id;
import rice.pastry.NodeHandle;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.PastryNodeFactory;
import rice.pastry.leafset.LeafSet;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;


public class PastrySetup 
{
	public PastryNode _node = null;
	 
	public PastrySetup(int bindport, InetSocketAddress bootaddress, Environment env, tankgame tGame) throws Exception
	{
		// Generate the NodeIds Randomly
	    NodeIdFactory nidFactory = new RandomNodeIdFactory(env);
	    
	    // construct the PastryNodeFactory, this is how we use rice.pastry.socket
	    PastryNodeFactory factory = new SocketPastryNodeFactory(nidFactory, bindport, env);
	
	    // construct a node
	    PastryNode node = factory.newNode();
	      
	    // construct a new MyApp
	    PastryApp app = new PastryApp(node,tGame);    
	    
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
	    _node = node;
	    
	    LeafSet leafSet = node.getLeafSet();

	    ArrayList<rice.p2p.commonapi.Id> sentTo = new ArrayList<rice.p2p.commonapi.Id>();
	    for (int i=-leafSet.ccwSize(); i<=leafSet.cwSize(); i++) {
	      if (i != 0) { // don't send to self
	        // select the item
	        NodeHandle nh = leafSet.get(i);
	        if(!sentTo.contains(nh.getId()))
	        	app.routeMyMsgDirect(nh, new MyMsg(app.endpoint.getId(), nh.getId(), "join"));   
	        sentTo.add(nh.getId());
	      }
	    }
	}
}
