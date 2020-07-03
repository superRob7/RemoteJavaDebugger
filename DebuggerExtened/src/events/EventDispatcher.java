package events;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;

import debuggee.Debuggee;
import functions.ConnectionBundle;
import script.Script;

public class EventDispatcher extends Thread {

	
	/** Instance of Debuggee object */
	private Debuggee debuggee;

	private ConnectionBundle conInfo;
	
	/** Instance of the Event Handler object to handle target VM events */
	private EventHandler eventHandler;

	private Script script;

	/**
	 * Flag to determin if line numbers have been located and not breakpoints set.
	 * -1 (No located, no script) 1 (Located, no script) 2 (located, script)
	 */	
	public boolean classLineNumFlag = false;
	
	public boolean isClassLineNumFlag() {
		return classLineNumFlag;
	}

	public void setClassLineNumFlag(boolean classLineNumFlag) {
		this.classLineNumFlag = classLineNumFlag;
	}

	private boolean scriptFlag = false;
	
	/**
	 * 0: startDisppatching
	 * 1: classNumbers */
	private int commandFlag = 0;
	
	public EventDispatcher(Debuggee debuggee, ConnectionBundle conInfo, Script script,int command) {
		this.debuggee = debuggee;
		this.conInfo = conInfo;
		this.script = script;
		commandFlag = command;
	}
	
	public EventDispatcher(Debuggee debuggee, ConnectionBundle conInfo, int command) {
		this.debuggee = debuggee;
		this.conInfo = conInfo;
		commandFlag = command;
	}
	

	public void run() {
		
		switch (commandFlag)
		{
		case 0:
			startDispatching();
			break;
		case 1:
			getClassLineNumber();
			break;
			
		}
		
	}
	
	
	/**
	 * This method is responsible for starting the process of handleing incoming
	 * events.
	 * 
	 * @param debuggee : Target VM
	 * @param conInfo  : Connection information used to connection to target VM
	 * @param script   : Script to set debugging parameters
	 */
	private void startDispatching() {
		
		eventHandler = new EventHandler(debuggee);
		if (this.script != null) {
			eventHandler.setScript(script);
			scriptFlag = true;
		}
		trackEvents();
	}

	private void getClassLineNumber() {
		
		eventHandler = new EventHandler(debuggee);
		trackEvents();
	}


	/**
	 * This method is responsible for retiring incoming events while the debugger -
	 * debuggee connection is live
	 */
	private void trackEvents() {
		
		while (debuggee.isConnected()) {
			
			if (classLineNumFlag == false & scriptFlag == false)
			{
				handleIncomingEvents();
			}
			else if (classLineNumFlag == true & scriptFlag == false)
			{
				return; 
			}
			else if (classLineNumFlag == true & scriptFlag == true) {
				handleIncomingEvents();
			} else if (scriptFlag){
				handleIncomingEvents();
			}

		}
		
	}

	private void handleIncomingEvents() {
		try {
			EventQueue eventQueue = debuggee.getVM().eventQueue();
			EventSet eventSet = eventQueue.remove(2500);
			EventIterator  iterate = null;
			 // Just loop again if we have no events
			while(eventSet == null)
			{
				eventSet = eventQueue.remove(2500);	
			}
			
				 iterate = eventSet.eventIterator();
			
			
			while (iterate.hasNext()) {
				
				if (classLineNumFlag == false & scriptFlag == false) {
					dispatchEvent(iterate.nextEvent(), eventSet);
				}else if (classLineNumFlag == true & scriptFlag == false) {
					eventSet.resume();
					return;
				}
				else if (classLineNumFlag == true & scriptFlag == true) {
					dispatchEvent(iterate.nextEvent(), eventSet);
				} else {
					dispatchEvent(iterate.nextEvent(), eventSet);
				}

			}
			
		} catch (VMDisconnectedException | InterruptedException exc) {
			debuggee.setConnected(false);
			System.out.println("Error: Target Machine has disconnected" + exc);
			return;
		}
	
		
	
		
	}

	/**
	 * This method is responsible for determining the correct action to perform
	 * based on the type of event.
	 * 
	 * @param event    : The current target VM event
	 * @param eventSet : The eventSet the event is apart off
	 */
	private void dispatchEvent(Event event, EventSet eventSet) {
		System.out.println("(dispathcEvent) Event = " + event.toString());
		
		boolean resume = false;
		
		if(event != null)
		{
			resume = true;
		}

		if (event instanceof VMStartEvent) {
			debuggee.setConnected(true);
			eventHandler.createClassRequests();
			debuggee.getLogger().logDebuggerInfo("Virtual Machine has started, class request created");
		}
		if (event instanceof ClassPrepareEvent) {
			debuggee.getLogger().logDebuggerInfo("Class loaded, ClassPrepareEvent triggered, setting Breakpoints");

			ReferenceType classRef = ((ClassPrepareEvent) event).referenceType();
			
			System.out.println("This is the name of the classprepareEvent = " + classRef );
			
			if (script == null) {
				eventHandler.getClassLineNumber(event);
				classLineNumFlag = true;
				return;
			} else {
				eventHandler.setBreakpoints(event);
				
			}
			

		}
		if (event instanceof BreakpointEvent) {
			debuggee.getLogger().logDebuggerInfo("Breakpoint reached, checking variables");
			eventHandler.checkVariables(event);
			
		}
		// The debugger & debuggee are no longer connected
		if (event instanceof VMDisconnectEvent) {
			debuggee.getLogger().logDebuggerInfo("Target Virtual Machine has disconnected");
			debuggee.setConnected(false);
			
		}
		// The debuggee VM dieded before disconnecting
		if (event instanceof VMDeathEvent) {
			debuggee.getLogger().logDebuggerInfo("VM DeathEvent : Targer VM has turmanated");
			debuggee.setConnected(false);
			
		}
		
		if (event instanceof ExceptionEvent)
		{
			System.out.println("exeption event cought");
		}
		
		if(resume)
		{
			eventSet.resume();
		}
	}

}
