package functions;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class is used to set up the logger for both the debugger and debuggee.
 * 
 * @author Robert Dunsmore
 *
 */
public class Log {

	/** String holding the custom location to save the log files to */
	String customLocation = null;

	/** Holds the flag indicating the level of detailed to be logged */
	private Level customLevel;

	/** Logger object for the debuggee */
	public Logger debuggeeLogger = Logger.getLogger("DebuggerLog");

	/** Logger object for the debugger */
	public Logger debuggerLogger = Logger.getLogger("debuggerLog");

	/** FileHandler to allow log information to be written to file */
	FileHandler dubggeeHand, dubggerHand;

	/**
	 * @return Level : Get the level of detail to be written to log files
	 */
	public Level getCustomLevel() {
		return customLevel;
	}

	/**
	 * @param customLevel Level : Set the level of detail to be written to log files
	 */
	public void setCustomLevel(Level customLevel) {
		this.customLevel = customLevel;
	}

	public Log() {
	}

	/**
	 * Set info level messages to debugger logger
	 * 
	 * @param msg : String, the message to be logged
	 */
	public void logDebuggerInfo(String msg) {
		debuggerLogger.info(msg);
	}

	/**
	 * Set Warning level messages to debugger logger
	 * 
	 * @param msg : String, the message to be logged
	 */
	public void logDebuggerWarning(String msg) {
		debuggerLogger.warning(msg);
	}

	/**
	 * Set Error level messages to debugger logger
	 * 
	 * @param msg : String, the message to be logged
	 */
	public void logDebuggerError(String msg) {
		debuggerLogger.severe(msg);
	}

	/**
	 * Set info level messages to debuggee logger
	 * 
	 * @param msg : String, the message to be logged
	 */
	public void logDebuggeeInfo(String msg) {
		debuggeeLogger.info(msg);
	}

	/**
	 * Set info level messages to debuggee logger
	 * 
	 * @param msg : String, the message to be logged
	 */
	public void logDebuggeeError(String msg) {
		debuggeeLogger.warning(msg);
	}

	/**
	 * Set a custom location to out put log files to
	 * 
	 * @param location : String, path to the custom location
	 */
	public void setCustomLocation(String location) {
		customLocation = location;
	}

	/**
	 * This method sets up the loggers for both the debugger and debuggee
	 */
	public void setup() {

		if (customLevel != null) {
			debuggeeLogger.setLevel(customLevel);
			debuggerLogger.setLevel(customLevel);
		}

		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

		// This block configure the logger with handler and formatter
		try {
			//Check weather a custom location has been entered 
			if (customLocation == null) {
				dubggeeHand = new FileHandler("Dubggee.log");
				dubggerHand = new FileHandler("Dubgger.log");
			} else {
				dubggeeHand = new FileHandler(customLocation + "/Dubggee.log");
				dubggerHand = new FileHandler(customLocation + "/Dubgger.log");
			}

		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		debuggeeLogger.addHandler(dubggeeHand);
		debuggerLogger.addHandler(dubggerHand);

		SimpleFormatter formatter = new SimpleFormatter();
		dubggeeHand.setFormatter(formatter);
		dubggerHand.setFormatter(formatter);

		debuggeeLogger.setUseParentHandlers(false);
		debuggerLogger.setUseParentHandlers(false);

	}

}
