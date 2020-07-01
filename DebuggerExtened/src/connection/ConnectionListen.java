package connection;

import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.ListeningConnector;

import functions.ConnectionBundle;

public class ConnectionListen extends Connection{

	/** Instance of the target VM */
	private VirtualMachine debuggeeMachine;

	/** Instance of the connection information */
	private ConnectionBundle conInfo;
	

	/**
	 * Constructor stores the connection information
	 * 
	 * @param conInfo : Connection information
	 * @param connectionManager 
	 */
	public ConnectionListen(ConnectionBundle conInfo) {
		this.conInfo = conInfo;
	}

	/**
	 * Attempt to attach to the target VM using the connection information stored
	 * 
	 * @return
	 */
	public VirtualMachine listen() {

		
		// Getting launching connector
		ListeningConnector connector = (ListeningConnector) findConnector();

		// Get the connector arguments
		Map<String, Connector.Argument> argument = getArguments(connector);

		try {
			debuggeeMachine = connector.accept(argument);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("Target VM is null");
			con.getDebuggee().getLogger().logDebuggerError("Connection failed : "+ e.toString());
		}
		
		if (debuggeeMachine != null) {
			return debuggeeMachine;
		}
		
		return null;
	}

	/**
	 * This method is responsible for setting connection arguments based on
	 * the saved connection information.
	 * @param connector
	 * @return Map : connector arguments
	 */
	public Map<String, Argument> getArguments(Connector connector) {

		// The map of arguments to be passed to the launching VM
		Map<String, Connector.Argument> argument = connector.defaultArguments();
		System.out.println(connector.name());
		switch(connector.name())
		{
			case "com.sun.jdi.SocketListen":
				//Setting the port number to attach to 
				argument.get("port").setValue(String.valueOf(conInfo.getPortNumber()));
				
				//Setting the host name to attch to
				argument.get("localAddress").setValue(conInfo.getHost());
				break;
				
			case "com.sun.jdi.SocketAttach":
				//Setting the host name to attch to
				argument.get("hostname").setValue(conInfo.getHost());
				
				//Setting the port number to attach to 
				argument.get("port").setValue(String.valueOf(conInfo.getPortNumber()));
				break;
		}
		argument.get("timeout").setValue(String.valueOf(10000));
		
		return argument;
	}

	/**
	 * This method is responsible for locating an attach connector 
	 * @return AttachConnector
	 */
	public Connector findConnector() {
		List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();

		for (Connector connector : connectors) {
			if (connector.name().equals("com.sun.jdi.SocketListen")) {
				return (ListeningConnector) connector;
			}
		}
		throw new Error("No launching connector");
	}

	
}







