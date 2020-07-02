package connection;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import functions.ConnectionBundle;

/**
 * This class is responsible for attempting to attach to a target VM
 * 
 * @author {Robert Dunsmore, 2718125}
 *
 */
public class ConnectionAttach extends Connection{

	/** Instance of the target VM */
	private VirtualMachine debuggeeMachine;

	/** Instance of the connection information */
	private ConnectionBundle conInfo;

	/**
	 * Constructor stores the connection information
	 * 
	 * @param conInfo : Connection information
	 */
	public ConnectionAttach(ConnectionBundle conInfo) {
		this.conInfo = conInfo;
	}

	/**
	 * Attempt to attach to the target VM using the connection information stored
	 * 
	 * @return
	 */
	public VirtualMachine attach() {

		
		// Getting launching connector
		AttachingConnector connector = (AttachingConnector) findConnector();

		// Get the connector arguments
		Map<String, Connector.Argument> argument = getArguments(connector);

		// Attempting the attaching connection
		try {
			debuggeeMachine = connector.attach(argument);
			
		} catch (IOException | IllegalConnectorArgumentsException e) {
			
			e.printStackTrace();
		}
		
		// Checking if the VM has been attached
		if (debuggeeMachine != null) {
			return debuggeeMachine;
		}
		
		//Dev output
		System.out.println("Target VM is null");
		
		//Defualt returning argument
		return null;
	}

	/**
	 * This method is responsible for setting connection arguments based on
	 * the saved connection information.
	 * @param connector
	 * @return Map : connector arguments
	 */
	private Map<String, Argument> getArguments(AttachingConnector connector) {

		// The map of arguments to be passed to the launching VM
		Map<String, Connector.Argument> argument = connector.defaultArguments();
	
				//Setting the host name to attch to
		argument.get("hostname").setValue(conInfo.getHost());
				
				//Setting the port number to attach to 
		argument.get("port").setValue(String.valueOf(conInfo.getPortNumber()));
	
		argument.get("timeout").setValue(String.valueOf(5000));
		
		return argument;
	}

	/**
	 * This method is responsible for locating an attach connector 
	 * @return AttachConnector
	 */
	protected Connector findConnector() {
		
		List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
		

			for (Connector connector : connectors) {
				if (connector.name().equals("com.sun.jdi.SocketAttach")) {
					return (AttachingConnector) connector;
				}
			}
			throw new Error("No launching connector");

	}

}
