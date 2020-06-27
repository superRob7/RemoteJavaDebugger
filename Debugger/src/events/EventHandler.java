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
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.event.BreakpointEvent;

import debuggee.Debuggee;
import functions.ConnectionBundle;
import functions.DebuggingStageEnums;
import script.BreakPoint;
import script.Script;
import static functions.DebuggingStageEnums.ALL;
import static functions.DebuggingStageEnums.VARIABLES;
import static functions.DebuggingStageEnums.BOTH;

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

	private ArrayList<Field> fieldsToLog = new ArrayList<Field>();
	
	private DebuggingStageEnums stageFlag = BOTH;

	private boolean  anyFieldsToLog = false;

	
	

	public EventHandler(Debuggee debuggee, ConnectionBundle conInfo) {
		this.debuggee = debuggee;
		this.conInfo = conInfo;
	}

	public void checkVariables(Event event) {
		
		
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");
		BreakPoint firstBreakpoint = script.getBreakPoints().get(0);
		BreakPoint lastBreakpoint = script.getBreakPoints().get(script.getBreakPoints().size()-1);
		
		if (line.equals(Integer.toString(firstBreakpoint.getLineNumbers())))
		{
			System.out.println("first breakpoint");
			if (script.getWatchVariables().size() <= 0)
			{	
				stageFlag = ALL;
			}
			else
			{
				 searchForFields(event);	
				
			}
		}
		
			switch (stageFlag)
			{
			case ALL:
					logAllVaribales(event);
					break;
			case VARIABLES:
				logIdentifiedVariabls(event);
				break;
			case BOTH:
				logIdentifiedFields(event);
				logIdentifiedVariabls(event);
				break;
			}
		
			if (line.equals(Integer.toString(lastBreakpoint.getLineNumbers())))
			{
				//Clean up
				fieldsToLog.clear();
			}
		
	}

	private boolean searchForFields(Event event) {
		System.out.println("searchForFields");
		
		EventRequest request = event.request();
		List<Field> fieldList = ((BreakpointRequest) request).location().declaringType().allFields();

		for (Field currentField : fieldList) {
			if (fieldsToLog.contains(currentField)) {
				continue;
			}

			String fieldName = currentField.name();

			for (String varName : script.getWatchVariables()) {
				if (fieldName.equals(varName)) {
					fieldsToLog.add(currentField);
					anyFieldsToLog = true;
				}
			}
		}
		
		if (anyFieldsToLog)
		{
			stageFlag = BOTH;
		}
		else
		{
			stageFlag = VARIABLES;
		}
		 
		return true;
	}

	private void logIdentifiedFields(Event event) {
		
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");

		ReferenceType refType;
		Value currentFieldVal;

		List<Field> fieldList = ((BreakpointRequest) request).location().declaringType().allFields();

		for (Field currentField : fieldList) {
			if (currentField.isStatic() & fieldsToLog.contains(currentField)) {
				refType = currentField.declaringType();
				currentFieldVal = refType.getValue(currentField);
				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentField.name() + " " + "Value = "
						+ currentFieldVal + " Line: " + line);
				
			}
		}
	}

	private void logIdentifiedVariabls(Event event) {
		
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");
		
		ArrayList<LocalVariable> variablesToLog = searchStackVariables(event);
		ThreadReference thread = ((BreakpointEvent) event).thread();
		StackFrame stackFrame;
		try {
			
			stackFrame = thread.frame(0);
			
				Map<LocalVariable, Value> stackFrameVariables = stackFrame.getValues(variablesToLog);
				for (LocalVariable currentVal : stackFrameVariables.keySet()) {
					if (stackFrameVariables.get(currentVal) instanceof ObjectReference) {
						ObjectReference ref = (ObjectReference) stackFrameVariables.get(currentVal);
						Method toString = ref.referenceType().methodsByName("toString", "()Ljava/lang/String;").get(0);
						try {
							debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentVal.name() + " "
									+ "Value = " + ref.invokeMethod(thread, toString, Collections.emptyList(), 0) +  " Line: " + line);

						} catch (Exception e) {
							debuggee.getLogger().logDebuggerError(
									"Variable = " + currentVal.name() + " " + "Did not have a toString method");
							System.out.println("couldnt get toString");
						}
						continue;
					}
					debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentVal.name() + " " + "Value = "
							+ stackFrameVariables.get(currentVal) + " Line: " + line);

				}
			
			} catch (IncompatibleThreadStateException e) {

					e.printStackTrace();
				}


	}
	
	private void logAllVaribales(Event event)
	{
		System.out.println("logAllVaribales");
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");
		
		List<Field> fieldList = ((BreakpointRequest) request).location().declaringType().allFields();
		
		for (Field currentField : fieldList)
		{
			if (currentField.isStatic())
			{
				ReferenceType refType = currentField.declaringType();
				Value currentFieldVal = refType.getValue(currentField);
				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentField.name() + " " + "Value = " + currentFieldVal + " Line " + line);   
				System.out.println(currentField);
			}
			else
			{
				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + currentField.name() + " " + "is not a static field. Value could not be recored." + " Line " + line);   
			}
		}
		
		
		ThreadReference thread = ((BreakpointEvent) event).thread();
		StackFrame stackFrame;
		try {
			stackFrame = thread.frame(0);
		
			Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
	
			for (Map.Entry<LocalVariable, Value> entry : visibleVariables.entrySet()) {
	
				
				debuggee.getLogger().logDebuggeeInfo("Variable Name = " + entry.getKey().name() + " " + "Value = "
						+ entry.getValue().toString() + " Line " + line);
				System.out.println(entry.getKey().name() + " = " + entry.getValue());
			}
		} catch (IncompatibleThreadStateException | AbsentInformationException e) {

			e.printStackTrace();
		}
	}

	private ArrayList<LocalVariable> searchStackVariables(Event event) {
		EventRequest request = event.request();
		String line = (String) request.getProperty("location");
		StackFrame stackFrame = null;
		String className = null;
		ArrayList<LocalVariable> variablesToLog = new ArrayList<LocalVariable>();
		try {
			ThreadReference thread = ((BreakpointEvent) event).thread();
			
			int frameCount = thread.frameCount();
			
			for (int i =0; i < frameCount; i++)
			{
				System.out.println("StackFrame " + i);
				stackFrame = thread.frame(i);
				Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());
				for (LocalVariable currentVal : visibleVariables.keySet()) {
					System.out.println(currentVal + "Value = " + visibleVariables.get(currentVal) + " Line " + line);
				}
			}
			
			stackFrame = thread.frame(0);

			switch (conInfo.getConType()) {
			case ATTACH:
				className = conInfo.getFilePath();
				break;
			case LAUNCH:
				className = conInfo.getClassNameNoEx();
				break;
			default:
				break;
			}

			stackFrame.location().toString().contains(className);

			Map<LocalVariable, Value> visibleVariables = stackFrame.getValues(stackFrame.visibleVariables());

			for (LocalVariable currentVal : visibleVariables.keySet()) {

				String currentValName = currentVal.name();

				for (String varName : script.getWatchVariables()) {
					if (currentValName.equals(varName)) {
						variablesToLog.add(currentVal);
					}
				}
			}

			return variablesToLog;

		} catch (IncompatibleThreadStateException | AbsentInformationException e) {
			debuggee.getLogger().logDebuggerError("An expetion was thrown when looking for static variables");
		}

		return null;
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

			if (bp.getLineNumbers() > 0) {
				addBreakPointLineNumber(bp, classLocation);
			}

		}

	}

	private void addBreakPointLineNumber(BreakPoint bp, ClassType classLocation) {
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

		ClassPrepareRequest classprep = debuggee.getDebuggeeEventMgr().createClassPrepareRequest();

		switch (conInfo.getConType()) {
		case ATTACH:
			classprep.addClassFilter(conInfo.getFilePath());
			classprep.putProperty("classname", conInfo.getFilePath());
			classprep.enable();
			break;
		case LAUNCH:
			classprep.addClassFilter(conInfo.getClassNameNoEx());
			classprep.putProperty("classname", conInfo.getClassNameNoEx());
			classprep.enable();
			break;
		default:
			break;

		}

	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

}
