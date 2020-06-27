package script;

public class BreakPoint {
	
	private String methodName, className, variableName;
	
	private int lineNumber;
	
	public BreakPoint(int lineNumber)
	{
		this.lineNumber = lineNumber;
	}
	
	/**
	 * Constuctor for breakpoint object
	 * @param name : The name of the class or method (String)
	 * @param identifier : To identified if the name suplided is the class or method name
	 */
	public BreakPoint(String name, int identifier)
	{
		if(identifier == 1)
		{
			methodName = name;
		}
		else if (identifier == 2)
		{
			className = name;
		}
		else if (identifier == 3)
		{
			variableName = name;
		}
		
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

	public String getVariableName()
	{
		return variableName;
	}

	public void setVariableName(String variableName)
	{
		this.variableName = variableName;
	}
	

}
