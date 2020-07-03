package events;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;

import debuggee.Debuggee;
import script.BreakPoint;
import script.Script;

/**
 * This class is responsible for performing action on incoming events from the
 * debuggee.
 * 
 * @author {Robert Dunsmore, 2718125}
 */
public class EventHandler extends Thread {

	/** Internal copy debuggee class */
	private Debuggee debuggee;


	/** Internal copy of the uploaded script file */
	private Script script;

	/** Constructor, created when dispatching begins, as well as when gathering line numbers */
	public EventHandler(Debuggee debuggee) {
		this.debuggee = debuggee;
	}

	/** 
	 * Called when handling incoming event  
	 * @param event : The incoming event cause by the debugger
	 */
	public void checkVariables(Event event)
	{
		// A local copy of the jdi Event Request class
		EventRequest request = event.request();
		
		// The line number the request came from
		String line = (String) request.getProperty("location");
		
		// Getting the breakpoint from the script object with the same line location as the request 
		BreakPoint currentBreakPoint = script.getBreakpointByLineNumber(line);
		
		System.out.println("Current brakepoint, class = " + currentBreakPoint.getClassName() + " Line number = " + currentBreakPoint.getLineNumbers());
		
		// Search for fields and vars
		searchFields(request, currentBreakPoint);
		searchVars(event, currentBreakPoint);
		
	}
	

	/**
	 * Searches the visible fields at the location of the request. 
	 * @param EventRequest : The EventRequest object, cause by the debugger
	 * @param currentBreakPoint : The breakpoint object that has the same line location
	 */
	private void searchFields(EventRequest request, BreakPoint currentBreakPoint) {
		
		System.out.println("inside searchfields");
		
		String line = (String) request.getProperty("location");
		
		ReferenceType refType;
		Value currentFieldVal;

		// Getting all of the visible fields to that breakpoint request (The request is of type breakpoint)
		List<Field> fieldList = ((BreakpointRequest) request).location().declaringType().allFields();

		for (Field currentField : fieldList) {
			
			// Checking if the field is statice and it is listed within the breakpoint script object
			if (currentField.isStatic() & currentBreakPoint.getVariableName().contains(currentField.name())) {
				// Getting the type of the field
				refType = currentField.declaringType();
				
				//Getting the value of the field and storing it in a Value object
				currentFieldVal = refType.getValue(currentField);
				
				// Log info to debuggee log file thought local copy of the debuggee object
				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentField.name() + " " + "Value = "
						+ currentFieldVal + " Class:  " + currentBreakPoint.getClassName() + " Line: " + line);
				
			}
			else
			{
				System.out.println(currentField.name() + " Is not static variable. (Instance variable)");
			}
		}
	}
	
	/**
	 * Searches the visible vars at the location of the request. 
	 * @param EventRequest : The EventRequest object, cause by the debugger
	 * @param currentBreakPoint : The breakpoint object that has the same line location
	 */
	private void searchVars(Event event, BreakPoint currentBreakPoint) {
		
		System.out.println("inside searchVars");
		
		// Getting the thread that triggered the request
		ThreadReference thread = ((BreakpointEvent) event).thread();
		
		// Local copy of the jdi stack frame object
		StackFrame stackFrame;
		
		try {
				// Getting the first frame on the stack of the thread
				stackFrame = thread.frame(0);
			
				// Storing '.this' of the current frame object
				ObjectReference objRef = stackFrame.thisObject();
				
				if (objRef != null)
				{
					// Getting the type of class relating to the '.this' object
					ReferenceType refType = objRef.referenceType();
					// Getting all of the visible fields
					List<Field> objFields = refType.allFields();
					for (int j=0; j<objFields.size(); j++)
					{
					  Field nextField = objFields.get(j);
					  for (String varName : currentBreakPoint.getVariableName())
					  {
						  if (nextField.name().equals(varName))
						  {
						    Value targetVal = objRef.getValue(nextField);
						    debuggee.getLogger().logDebuggeeInfo("Variable Name = " + nextField.name() + " " + "Value = " + targetVal + " Class:  " + currentBreakPoint.getClassName() +" Line: " + currentBreakPoint.getLineNumbers());
						  }
					  }
					}
				}
				else
				{
					System.out.println("ObjRef is null");
					Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
					for (LocalVariable currentStackVar : visibleVariables.keySet())
						{
							for (String scriptVar : currentBreakPoint.getVariableName())
							{
								if (currentStackVar.name().equals(scriptVar))
								{
									System.out.println(currentStackVar.name());
									if (currentStackVar instanceof ObjectReference) {
										ObjectReference ref = (ObjectReference) currentStackVar;
										Method toString = ref.referenceType().methodsByName("toString", "()Ljava/lang/String;").get(0);
										try {
											debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentStackVar.name() + " " + "Value = " + ref.invokeMethod(thread, toString, Collections.emptyList(), 0) +  " Line: " + currentBreakPoint.getLineNumbers());
		
										} catch (Exception e) {
											debuggee.getLogger().logDebuggerError("Variable = " + currentStackVar.name() + " " + "Did not have a toString method"+ " Class:  " + currentBreakPoint.getClassName() + " Line: " + currentBreakPoint.getLineNumbers());
											System.out.println("couldnt get toString");
										}
									}
									else {
										debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentStackVar.name() + " " + "Value = " + stackFrame.getValue(currentStackVar) +  " Class:  " + currentBreakPoint.getClassName() + " Line: " + currentBreakPoint.getLineNumbers());
									}
								}
							}
						}
				}
			
			} catch (IncompatibleThreadStateException | AbsentInformationException e) {
				e.printStackTrace();
			}
	}


	public void getClassLineNumber(Event event) {

		ClassType classLocation = (ClassType) ((ClassPrepareEvent) event).referenceType();

		try {
			FileWriter classLineWriter = new FileWriter("classLineNumers.txt");

			for (Location l : classLocation.allLineLocations()) {
				int lineNumber = l.lineNumber();

				try {

					classLineWriter.write("Line Number =" + Integer.toString(lineNumber) + ", ");
					classLineWriter.write("Method = " + l.method() + ", ");
					classLineWriter.write("Code Index = " + l.codeIndex() + ", ");
					classLineWriter.write(System.getProperty("line.separator"));

				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
			classLineWriter.close();

		} catch (AbsentInformationException | IOException e) {
			System.out.println("failed when looking for line numbers");
			e.printStackTrace();
		}

	}

	public void setBreakpoints(Event event) {

		ClassType classLocation = (ClassType) ((ClassPrepareEvent) event).referenceType();

		for (BreakPoint bp : script.getBreakPoints()) {

			if (bp.getClassName().equals(classLocation.name()))
			{
				if (bp.getLineNumbers() > 0) {
					addBreakPointLineNumber(bp, classLocation);
				}
			}
		}

	}

	private void addBreakPointLineNumber(BreakPoint bp, ClassType classLocation) {
		
		
		System.out.println("Breakpoint to be set at line = " + bp.getLineNumbers() + " | On class =" + bp.getClassName());
		
		try {
			List<Location> loc = classLocation.locationsOfLine(bp.getLineNumbers());
			
			try {
			Location lineloc = loc.get(0);
			EventRequest breakpoint = debuggee.getDebuggeeEventMgr().createBreakpointRequest(lineloc);
			breakpoint.putProperty("location", String.valueOf(bp.getLineNumbers()));
			breakpoint.enable();
			}catch (Exception e) {
				debuggee.getLogger().logDebuggerError("Could not find Location " + bp.getLineNumbers() + " to add breakpoint to: " + e);
			}
			
		} catch (AbsentInformationException exc) {
			debuggee.getLogger().logDebuggerError(exc.toString());

		}

	}

	public void createClassRequests() {
		
		ArrayList<String> classNames = new ArrayList<String>(script.getBreakPoints().size());
		
		
		for (int i=0; i < script.getBreakPoints().size(); i++)
		{
			BreakPoint bp = script.getBreakPoints().get(i);
			if (bp.getClass() != null && !bp.getClassName().isEmpty())
			{
				if (!classNames.contains(bp.getClassName()))
				{
					classNames.add(bp.getClassName());
				}
			}
				
		}
		
		if (classNames != null && !classNames.isEmpty())
		{
			ClassPrepareRequest classprep;
			for (String s : classNames)
			{
				classprep = debuggee.getDebuggeeEventMgr().createClassPrepareRequest();
				classprep.addClassFilter(s);
				classprep.putProperty("classname", s);
				classprep.enable();
			}
	
		}
				
}


	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

}
