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
import functions.ConnectionBundle;
import script.BreakPoint;
import script.Script;

/**
 * This class is responsible for performing action on incoming events from the
 * debuggee.
 * 
 * @author {Robert Dunsmore, 2718125}
 */
public class EventHandler extends Thread {

	private Debuggee debuggee;

	private ConnectionBundle conInfo;

	private Script script;

	public EventHandler(Debuggee debuggee, ConnectionBundle conInfo) {
		this.debuggee = debuggee;
		this.conInfo = conInfo;
	}

	
	public void checkVariables(Event event)
	{
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");
		
		BreakPoint currentBreakPoint = script.getBreakpointByLineNumber(line);
		
		System.out.println("Current brakepoint, class = " + currentBreakPoint.getClassName() + " Line number = " + currentBreakPoint.getLineNumbers());
		
		searchFields(event, currentBreakPoint);
		searchVars(event, currentBreakPoint);
		
	}
	

	private void searchFields(Event event, BreakPoint currentBreakPoint) {
		
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");

		ReferenceType refType;
		Value currentFieldVal;

		List<Field> fieldList = ((BreakpointRequest) request).location().declaringType().allFields();

		for (Field currentField : fieldList) {
			if (currentField.isStatic() & currentBreakPoint.getVariableName().contains(currentField.name())) {
				refType = currentField.declaringType();
				currentFieldVal = refType.getValue(currentField);
				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentField.name() + " " + "Value = "
						+ currentFieldVal + " Class:  " + currentBreakPoint.getClassName() + " Line: " + line);
				
			}
			else
			{
				System.out.println(currentField.name() + " Is not static variable. (Instance variable)");
			}
		}
//		
//		EventRequest request = event.request();
//
//		ReferenceType refType;
//		Value currentFieldVal;
//
//		List<Field> fieldList = ((BreakpointRequest) request).location().declaringType().allFields();
//
//		for (Field currentField : fieldList) {
//			if (!currentField.isStatic())
//			{
//				
//				ObjectReference objRef = stackFrame.thisObject();
//				ReferenceType refType = objRef.referenceType();
//				List<Field> objFields = refType.allFields();
//				for (int i=0; i<objFields.size(); i++)
//				{
//				  Field nextField = objFields.get(i);
//				  if (nextField.name().equals(varName))
//				  {
//				    Value targetVal = objRef.getValue(nextField);
//				  }
//				}
//				
//				
//			}
//			else
//			{
//				refType = currentField.declaringType();
//				currentFieldVal = refType.getValue(currentField);
//				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentField.name() + " " + "Value = "
//						+ currentFieldVal + " Line: " + currentBreakPoint.getLineNumbers());
//			}
//			
//		}
//		
	}
	
	private void searchVars(Event event, BreakPoint currentBreakPoint) {
			ThreadReference thread = ((BreakpointEvent) event).thread();
			StackFrame stackFrame;
		
		try {
		
				stackFrame = thread.frame(0);
			
				ObjectReference objRef = stackFrame.thisObject();
				
				if (objRef != null)
				{
					ReferenceType refType = objRef.referenceType();
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
			
//			
//			for (int i =0; i < frameCount; i++)
//			{
//				
//				stackFrame = thread.frame(i);
//				System.out.println("On Stack Frame " + i);
//				
//			
//				Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
//				
//				for (LocalVariable currentVal : visibleVariables.keySet()) {
//					System.out.println(currentVal + "Value = " + visibleVariables.get(currentVal) + " Line " + currentBreakPoint.getLineNumbers());
//				}
//				
//				
//				
//				for (LocalVariable currentStackVar : visableVars)
//				{
//					for (String scriptVar : currentBreakPoint.getVariableName())
//					{
//						if (currentStackVar.name().equals(scriptVar))
//						{
//							if (currentStackVar instanceof ObjectReference) {
//								ObjectReference ref = (ObjectReference) currentStackVar;
//								Method toString = ref.referenceType().methodsByName("toString", "()Ljava/lang/String;").get(0);
//								try {
//									debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentStackVar.name() + " " + "Value = " + ref.invokeMethod(thread, toString, Collections.emptyList(), 0) +  " Line: " + currentBreakPoint.getLineNumbers());
//
//								} catch (Exception e) {
//									debuggee.getLogger().logDebuggerError("Variable = " + currentStackVar.name() + " " + "Did not have a toString method"+ " Line: " + currentBreakPoint.getLineNumbers());
//									System.out.println("couldnt get toString");
//								}
//							}
//							else {
//								debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentStackVar.name() + " " + "Value = " + stackFrame.getValue(currentStackVar) + " Line: " + currentBreakPoint.getLineNumbers());
//							}
//						}
//					}
//				}
//			}
//		
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
