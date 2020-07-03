package script;

import java.util.ArrayList;

public class Script {

	private ArrayList<BreakPoint> breakPoints = new ArrayList<BreakPoint>();
	
	private ArrayList<String> watchVariables = new ArrayList<String>();
	
	private ArrayList<WatchPoint> watchPoints = new ArrayList<WatchPoint>();
	
	
	public ArrayList<BreakPoint> getBreakPoints() {
		return breakPoints;
	}

	public void setBreakPoints(BreakPoint breakPoint) {
		this.breakPoints.add(breakPoint);
	}

	public ArrayList<WatchPoint> getWatchPoints() {
		return watchPoints;
	}

	public void setWatchPoints(WatchPoint watchPoint) {
		watchPoints.add(watchPoint);
	}

	public ArrayList<String> getWatchVariables() {
		return watchVariables;
	}

	public void setWatchVariables(ArrayList<String> watchVariables) {
		this.watchVariables = watchVariables;
	}

	public BreakPoint getBreakpointByLineNumber(String line)
	{
		int lineNumber = Integer.valueOf(line);
		
		for (BreakPoint bp : breakPoints)
		{
			if (bp.getLineNumbers() == lineNumber)
			{
				return bp;
			}
		}
		
		return null;
		
	}
	
	public BreakPoint getBreakpointByLineNumber(int line)
	{
		for (BreakPoint bp : breakPoints)
		{
			if (bp.getLineNumbers() == line)
			{
				return bp;
			}
		}
		return null;
		
	}
	
}
