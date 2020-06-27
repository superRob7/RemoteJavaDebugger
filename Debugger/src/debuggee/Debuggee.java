package debuggee;

import java.util.logging.Level;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequestManager;

import functions.Log;



public class Debuggee {

	private VirtualMachine vm;
	
	private EventRequestManager debuggeeEventMgr;
	
	
	private Log logger;
	
	private boolean isConnected = false;
	
	private StringBuilder classLineNumbers ;

	public VirtualMachine getVM() {
		return vm;
	}

	public void setVM(VirtualMachine debuggeeMachine) {
		this.vm = debuggeeMachine;
		setDebuggeeEventMgr(vm.eventRequestManager());
		
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public EventRequestManager getDebuggeeEventMgr() {
		return debuggeeEventMgr;
	}

	public void setDebuggeeEventMgr(EventRequestManager debuggeeEventMgr) {
		this.debuggeeEventMgr = debuggeeEventMgr;
	}


	public StringBuilder getClassLineNumbers() {
		return classLineNumbers;
	}

	public void setClassLineNumbers(StringBuilder classLineNumbers) {
		this.classLineNumbers = classLineNumbers;
	}


	public Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger = logger;
	}

	public void setCustomLogLocation(String location)
	{
		this.getLogger().setCustomLocation(location);
	}
	
	public void setCustomLogLevel(Level level)
	{
		this.getLogger().setCustomLevel(level);
	}

	
	
}
