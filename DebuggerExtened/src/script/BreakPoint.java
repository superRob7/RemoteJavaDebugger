package script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BreakPoint {
	
	private String methodName, className;
	private List<String> variables;
	
	private int lineNumber;
	
	public BreakPoint(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}

	
	public BreakPoint()
	{
		
	}

	public BreakPoint(int lineNumber , String className) {
		this.lineNumber = lineNumber;
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumbers() {
		return lineNumber;
	}

	public void setLineNumbers(int lineNumbers) {
		this.lineNumber = lineNumbers;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String toString()
	{
		return "Line number = " + this.getLineNumbers() + " " + "Method name = " + this.getMethodName() + " " + "Class Name = " + this.getClassName() + " " + "Variable name = " + this.getVariableName();
	}

	public List<String> getVariableName()
	{
		return variables;
	}

	public void setVariableName(Collection<String> collection)
	{
		 this.variables = new ArrayList<String>(collection);
	}
	

}
