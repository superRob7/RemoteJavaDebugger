package script;

public class WatchPoint {
	
	
	private String name;
	
	private boolean supsendThread;
	
	private boolean suspendVM;
	
	private int hitCount;
	
	public WatchPoint(String variableName, boolean suspendThread, boolean suspendVM)
	{
		this.name = variableName;
		this.supsendThread = suspendThread;
		this.suspendVM = suspendVM;
	}
	
	public WatchPoint(String variableName)
	{
		this.name = variableName;
	}
	
		
	public WatchPoint(String variableName, String suspendType)
	{
		this.name = variableName;
		
		if(suspendType.equals("suspendThread"))
		{
			supsendThread = true;
		}
		else if((suspendType.equals("suspendVM")))
		{
			suspendVM = true;
		}
		else
		{
			System.out.println("suspendType not valid");
		}

	}


	public String getVariableName() {
		return name;
	}

	public void setVariableName(String variableName) {
		this.name = variableName;
	}

	public boolean isSupsendThread() {
		return supsendThread;
	}

	public void setSupsendThread(boolean supsendThread) {
		this.supsendThread = supsendThread;
	}

	public boolean isSuspendVM() {
		return suspendVM;
	}

	public void setSuspendVM(boolean suspendVM) {
		this.suspendVM = suspendVM;
	}

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}
	
	public String toString()
	{
		return "Variable Name = " + this.getVariableName() + " " + "suspend Thread = " + this.isSupsendThread() + " " + "suspendVM = " + this.isSuspendVM();
	}
	

}
