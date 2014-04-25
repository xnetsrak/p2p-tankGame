package org.tank.Model;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

public class PastryApp implements Application
{
	  protected Endpoint endpoint;
	  private Model _model;

	  public PastryApp(Node node, Model model) 
	  {
		  this._model = model;
	    this.endpoint = node.buildEndpoint(this, "myinstance");
	    
	    // the rest of the initialization code could go here
	    
	    this.endpoint.register();  // now we can receive messages
	  }

	  /* Called to route a message to the id */
	  public void routeMyMsg(Id id) {
	    System.out.println(this+" sending to "+id);    
	    Message msg = new MyMsg(endpoint.getId(), id, "");
	    endpoint.route(id, msg, null);
	  }
	  
	  /* Called to directly send a message to the nh */
	  public void routeMyMsgDirect(NodeHandle nh, MyMsg msg1) {
	    System.out.println(this+" sending direct to "+nh);    
	    //Message msg = new MyMsg(endpoint.getId(), nh.getId());
	    endpoint.route(null, msg1, nh);
	  }
	    
	  /* Called when we receive a message. */
	  public void deliver(Id id, Message message) {
	    System.out.println(this+" received "+message);
	    MyMsg recivedMsg = (MyMsg)message;
	    String msgType = recivedMsg.getType();
	    
	    if(msgType.equals("join"))
	    	_model.tankJoin();
	    
	  }

	  /* Called when you hear about a new neighbor. */
	  public void update(NodeHandle handle, boolean joined) {
	  }
	  
	  /*Called a message travels along your path.*/
	  public boolean forward(RouteMessage message) {
	    return true;
	  }
	  
	  public String toString() {
	    return "MyApp "+endpoint.getId();
	  }

}
