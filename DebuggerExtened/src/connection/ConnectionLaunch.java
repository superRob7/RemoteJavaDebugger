package connection;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;

import functions.ConnectionBundle;

/**
 * This class is resposable for attempting launching a target VM
 * 
 * @author {Robert Dunsmore, 2718125}
 *
 */
public class ConnectionLaunch extends Connection{

	/** Instance of the target VM */
	private VirtualMachine debuggeeMachine;

	/** Instance of the connection information */
	private ConnectionBundle conInfo;

	/**
	 * Constructor stores the connection information
	 * 
	 * @param conInfo : Connection information
	 */
	public ConnectionLaunch(ConnectionBundle conInfo) {
		this.conInfo = conInfo;
	}

	/**
	 * Attempt to launch the target VM using the connection information stored
	 * 
	 * @return VirtualMachine : The target VM
	 */
	public VirtualMachine safeStart() {

		// Getting launching connector
		LaunchingConnector connector = findConnector();

		// Get the connector arguments
		Map<String, Connector.Argument> arguments = getArguments(connector);

		try {
			debuggeeMachine = connector.launch(arguments);
	
		} catch (IOException | IllegalConnectorArgumentsException | VMStartException e) {
		
			e.printStackTrace();
		}
		if (debuggeeMachine != null) {
			return debuggeeMachine;
		}
		System.out.println("Target VM is null");
		return null;

	}

	/**
	 * This method is responsible for setting connection arguments based on the
	 * saved connection information.
	 * 
	 * @param connector
	 * @return
	 */
	private Map<String, Connector.Argument> getArguments(LaunchingConnector connector) {

		// The map of arguments to be passed to the launching VM
		Map<String, Connector.Argument> arguments = connector.defaultArguments();

		// The main argument to be past (The class name)
		Connector.Argument mainArg = (Connector.Argument) arguments.get("main");

		// setting the class name
		mainArg.setValue(conInfo.getClassNameNoEx());

		// Setting the class path to the directory of the class file
		Connector.Argument optionArg = (Connector.Argument) arguments.get("options");
		optionArg.setValue(" -cp " + conInfo.getClassLocation());

		// Determine weather to suspend the target VM
		if (conInfo.isSuspend()) {
			Connector.Argument supendArg = (Connector.Argument) arguments.get("suspend");
			supendArg.setValue("true");
		} else {
			Connector.Argument supendArg = (Connector.Argument) arguments.get("suspend");
			supendArg.setValue("false");
		}

		return arguments;
	}

	/**
	 * This method is responsible for locating a launch connector
	 * 
	 * @return LaunchConnector
	 */
	public LaunchingConnector findConnector() {
		List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
		for (Connector connector : connectors) {
			if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
				return (LaunchingConnector) connector;
			}
		}
		throw new Error("No launching connector");
	}

}
