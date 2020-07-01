package connection;

import java.util.Map;

import com.sun.jdi.connect.Connector;

import debuggee.Debuggee;
import functions.ConnectionBundle;

/**
 * This class controls the attempts to establish a connection between
 * the debugger and debuggee. 
 * @author {Rober Dunsmore, 2718125}
 *
 */
public class Connection {

	/** Reference to connection information */
	ConnectionBundle conInfo;

	/** Reference to the target VM */
	Debuggee debuggee;
	
	ConnectionManager con;
	
	
	protected Map<String, Connector.Argument> getArguments(Connector connector)
	{
		return null;
	}
	
	protected Connector findConnector() {
		return null;	
	}
	
	protected Debuggee getDebuggee()
	{
		return debuggee;
	}
}
