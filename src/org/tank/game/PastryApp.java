package org.tank.game;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

public class PastryApp implements Application
{
	/**
	   * The Endpoint represents the underlieing node.  By making calls on the 
	   * Endpoint, it assures that the message will be delivered to a MyApp on whichever
	   * node the message is intended for.
	   */
	  protected Endpoint endpoint;
	  private tankgame tGame;

	  public PastryApp(Node node, tankgame tGame) 
	  {
		  this.tGame = tGame;
	    // We are only going to use one instance of this application on each PastryNode
	    this.endpoint = node.buildEndpoint(this, "myinstance");
	    
	    // the rest of the initialization code could go here
	    
	    // now we can receive messages
	    this.endpoint.register();
	  }

	  /**
	   * Called to route a message to the id
	   */
	  public void routeMyMsg(Id id) {
	    System.out.println(this+" sending to "+id);    
	    Message msg = new MyMsg(endpoint.getId(), id, "");
	    endpoint.route(id, msg, null);
	  }
	  
	  /**
	   * Called to directly send a message to the nh
	   */
	  public void routeMyMsgDirect(NodeHandle nh, MyMsg msg1) {
	    System.out.println(this+" sending direct to "+nh);    
	    //Message msg = new MyMsg(endpoint.getId(), nh.getId());
	    endpoint.route(null, msg1, nh);
	  }
	    
	  /**
	   * Called when we receive a message.
	   */
	  public void deliver(Id id, Message message) {
	    System.out.println(this+" received "+message);
	    MyMsg recivedMsg = (MyMsg)message;
	    String msgType = recivedMsg.getType();
	    
	    if(msgType.equals("join"))
	    {
	    	tGame.mp.addEnemyTank();
	    }
	    
	  }

	  /**
	   * Called when you hear about a new neighbor.
	   * Don't worry about this method for now.
	   */
	  public void update(NodeHandle handle, boolean joined) {
	  }
	  
	  /**
	   * Called a message travels along your path.
	   * Don't worry about this method for now.
	   */
	  public boolean forward(RouteMessage message) {
	    return true;
	  }
	  
	  public String toString() {
	    return "MyApp "+endpoint.getId();
	  }

}