package functions;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;



public class Log {
	
	
	String customLocation = null;
	
	private Level customLevel;
	
	public Logger debuggeeLogger = Logger.getLogger("DebuggerLog");  
	public Logger debuggerLogger = Logger.getLogger("debuggerLog");
	
	FileHandler dubggeeHand, dubggerHand; 
	
	public Level getCustomLevel() {
		return customLevel;
	}


	public void setCustomLevel(Level customLevel) {
		this.customLevel = customLevel;
	}

	
	
	
    public Logger getDebuggeeLogger() {
		return debuggeeLogger;
	}


	public void setDebuggeeLogger(Logger debuggeeLogger) {
		this.debuggeeLogger = debuggeeLogger;
	}


	public Logger getDebuggerLogger() {
		return debuggerLogger;
	}


	public void setDebuggerLogger(Logger debuggerLogger) {
		this.debuggerLogger = debuggerLogger;
	}

	
    
    public Log()
    {
    	
    }
    
    
    public void logDebuggerInfo(String msg)
    {
    	debuggerLogger.info(msg);
    }
    
    public void logDebuggerWarning(String msg)
    {
    	debuggerLogger.warning(msg);
    }
    
    public void logDebuggerError(String msg)
    {
    	debuggerLogger.severe(msg);
    }
    
    public void logDebuggeeInfo(String msg)
    {
    	debuggeeLogger.info(msg);
    }
    
    public void logDebuggeeError(String msg)
    {
    	debuggeeLogger.warning(msg);
    }
    
    public void setCustomLocation(String location)
    {
    	customLocation = location;
    }
    		
    
    public void setup()
    {
    	System.out.println(customLevel + " log level");
    	if (customLevel != null)
    	{
    		debuggeeLogger.setLevel(customLevel);
    		debuggerLogger.setLevel(customLevel);
    	}
    		
    	if (customLocation == null)
    	{

	    	  System.setProperty("java.util.logging.SimpleFormatter.format",
	                  "[%1$tF %1$tT] [%4$-7s] %5$s %n");
	    	
	    	 // This block configure the logger with handler and formatter  
	    	try {
				dubggeeHand = new FileHandler("Dubggee.log");
				dubggerHand = new FileHandler("Dubgger.log");  
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
    	
    	else {

	    	System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
	    	
	    	 // This block configure the logger with handler and formatter  
	    	try {
				dubggeeHand = new FileHandler(customLocation + "/Dubggee.log");
				dubggerHand = new FileHandler(customLocation + "/Dubgger.log");  
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
    
    public void clearLogs()
    {
    	
    }

}
