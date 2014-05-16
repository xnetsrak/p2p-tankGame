package org.tank.Model;

import org.tank.Msg.CoordinatorUpdateMsg;
import org.tank.Msg.JoinResponseMsg;
import org.tank.Msg.JoinScribeMsg;
import org.tank.Msg.LeaveScribeMsg;
import org.tank.Msg.MyMsg;
import org.tank.Msg.MyScribeMsg;
import org.tank.Msg.TankPositionUpdateMsg;

import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.CancellableTask;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.Node;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;
import rice.p2p.scribe.Scribe;
import rice.p2p.scribe.ScribeClient;
import rice.p2p.scribe.ScribeContent;
import rice.p2p.scribe.ScribeImpl;
import rice.p2p.scribe.Topic;
import rice.pastry.commonapi.PastryIdFactory;

@SuppressWarnings("deprecation")
public class PastryApp implements Application, ScribeClient
{
	  protected Endpoint endpoint;
	  private Model _model;
	  
	  CancellableTask publishTask;
	  Scribe myScribe;
	  Topic myTopic;
	  
	  int seqNum = 0;

	  public PastryApp(Node node, Model model) 
	  {
		  this._model = model;
	    this.endpoint = node.buildEndpoint(this, "myinstance");
	    
	    // construct Scribe
	    myScribe = new ScribeImpl(node,"myScribeInstance");

	    // construct the topic
	    myTopic = new Topic(new PastryIdFactory(node.getEnvironment()), "Zone1Topic");
	    System.out.println("myTopic = "+myTopic);
	    
	    this.subscribe();
	    this.endpoint.register();  // now we can receive messages
	  }

	  /* Called to route a message to the id */
	  public void routeMyMsg(Id id,  MyMsg msg1) {
	    //System.out.println(this+" sending to "+id);    
	    endpoint.route(id, msg1, null);
	  }
	  
	  /* Called to directly send a message to the nh */
	  public void routeMyMsgDirect(NodeHandle nh, MyMsg msg1) {
	    //System.out.println(this+" sending direct to "+nh);    
	    endpoint.route(null, msg1, nh);
	  }
	    
	  /* Called when we receive a message. */
	  public void deliver(Id id, Message message) {
	    //System.out.println(this+" received "+message);
	    MyMsg recivedMsg = (MyMsg)message;
	    String msgType = recivedMsg.getType();
	    
	    if(msgType.equals("Join")) {
	    	//_model.tankJoin((JoinMsg)recivedMsg);
	    }
	    else if(msgType.equals("TankUpdate"))
	    {
	    	if(_model.getCoordinator() != null)
	    		_model.getCoordinator().tankUpdateRequest((TankPositionUpdateMsg)recivedMsg);
	    }
	    else if(msgType.equals("ShotMsg"))
	    {
	    	//_model.tankShotMsg((ShotMsg)recivedMsg);
	    }
	    else if(recivedMsg instanceof JoinResponseMsg) {
	    	_model.tankJoinResponse((JoinResponseMsg)recivedMsg);
	    }
	    
	  }

	  /* Called when you hear about a new neighbor. */
	  public void update(NodeHandle handle, boolean joined) { }
	  
	  /*Called a message travels along your path.*/
	  public boolean forward(RouteMessage message) { return true; }
	  
	  public String toString() {
	    return "MyApp "+endpoint.getId();
	  }

	  /**
	   * SCRIBE PART
	   */
	  
	  /* Subscribes to myTopic. */
	  public void subscribe() {
	  		myScribe.subscribe(myTopic, this);
	  }
	  
	  /* Sends the multicast message.*/
	  public void sendMulticast(MyScribeMsg msg, int frameNumber) {
			System.out.println("Node "+endpoint.getLocalNodeHandle()+" broadcasting frame: "+ frameNumber);
			myScribe.publish(myTopic, msg);
	  }
	  
	public boolean anycast(Topic arg0, ScribeContent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void childAdded(Topic arg0, NodeHandle arg1) {
		// TODO Auto-generated method stub
		
	}

	public void childRemoved(Topic arg0, NodeHandle arg1) {
		// TODO Auto-generated method stub
		
	}

	public void deliver(Topic arg0, ScribeContent message)
	{
		MyScribeMsg recivedMsg = (MyScribeMsg)message;
	    
	    if(recivedMsg instanceof JoinScribeMsg) {
	    	if(_model.getCoordinator() != null)
	    		_model.getCoordinator().tankJoin((JoinScribeMsg)recivedMsg);
	    }
	    else if(recivedMsg instanceof CoordinatorUpdateMsg) {
	    	//_model.coordinatorUpdateMsg((CoordinatorUpdateMsg)message);
	    	_model.coordinatorMsgRecived((CoordinatorUpdateMsg)message);
	    }
	    else if(recivedMsg instanceof LeaveScribeMsg) {
	    	_model.recivedLeaveMsg((LeaveScribeMsg)message);
	    }
		
	}
	
	public boolean isRoot() {
		return myScribe.isRoot(myTopic);
	}

	public void subscribeFailed(Topic arg0) {
		// TODO Auto-generated method stub
		
	}

}
