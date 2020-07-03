package script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLScriptReader {

	File scriptFile;

	Script script = new Script();

	BreakPoint breakPoint;
	
	HashMap<Integer,String> xmlBreakPoints = new HashMap<Integer,String>();
	
	ArrayList<String> varNames = new ArrayList<String>();

	Document doc = null;

	public XMLScriptReader(File xmlScript) {
		scriptFile = xmlScript;
	}

	public Script readScript() {
		try {
				if (buildDocument() == -1)
				{
					return null;
				}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.out.println("Failed to read script \n");
			e.printStackTrace();
		}
		
		
		return script;
	}

	private int buildDocument() throws ParserConfigurationException, SAXException, IOException {
		// Prepare to build document model
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(scriptFile);

		doc.getDocumentElement().normalize();

		NodeList breakPointList = doc.getElementsByTagName("breakPoint");
		
		

		if (!(breakPointList.getLength() == 0)) {
			
			
			if(isValidBreakPoint(breakPointList))
			{
				setBreakpoints(breakPointList);
			}
			else
			{
				System.out.println("\n No line number in any breakpoint");
				return -1;
			}
			
			
		} else {
			System.out.println("\n No breakpoints inside xml");
			return -1;
		}

		
		return 1;
		
	}

	private boolean isValidBreakPoint(NodeList breakPointList) {
		
		for (int i = 0; i < breakPointList.getLength(); i++) 
		{
				Node currentNode = breakPointList.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentNode;

					NodeList breakPointInfo = element.getChildNodes();
		
					//Loop through the element nodes
					for (int j = 0; j < breakPointInfo.getLength(); j++) {
						
										
						Node currentChildNode = breakPointInfo.item(j);
						if (currentChildNode.getNodeType() == Node.ELEMENT_NODE) {
							Element childElemt = (Element) currentChildNode;
							
							if(childElemt.getNodeName().equals("lineNumber"))
							{
								return true;
							}
							

					}
				}
			}
		
		}
		
		return false;
	}

	private void setBreakpoints(NodeList breakPointList) {
		
		Map<String, BreakPoint> breakpoints = new HashMap<String,BreakPoint>();
		ArrayList<String> varNames =  new ArrayList<String>();
		ArrayList<Integer> lineNumber =  new ArrayList<Integer>();
		String className;
		
		
		for (int i = 0; i < breakPointList.getLength(); i++) 
		{
			breakpoints.clear();
			varNames.clear();
			className=null;
			
				Node currentNode = breakPointList.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentNode;
					
					 className = element.getAttribute("className");

					NodeList breakPointInfo = element.getChildNodes();
		
					//Loop through the element nodes
					for (int j = 0; j < breakPointInfo.getLength(); j++) {	
						Node currentChildNode = breakPointInfo.item(j);
						if (currentChildNode.getNodeType() == Node.ELEMENT_NODE) {
							Element childElemt = (Element) currentChildNode;
							
							switch(childElemt.getNodeName())
							{
								case "lineNumber" : 
									script.setBreakPoints(new BreakPoint(Integer.valueOf(childElemt.getTextContent()), className));						
									break;
								case "variableName":
									varNames.add(childElemt.getTextContent());
									break;
								default :
									System.out.println("Invalid tag");
									break;
							}
					}
						
				}
					
					for (BreakPoint bp : script.getBreakPoints())
					{
						if (bp.getClassName().equals(className))
						{
							bp.setVariableName(varNames);
						}
					}
					
			}
		
		}
		

		for (BreakPoint bp : script.getBreakPoints())
		{
			System.out.println("\n");
			
			System.out.println("BreakPoint line = " + bp.getLineNumbers()  + " | "  + " ClassName = " + bp.getClassName()  + " | " + " Variables = " + bp.getVariableName());
		}
		
		
	}
}
		
/**		
		for (int temp = 0; temp < breakPointList.getLength(); temp++) {
			
			Node nNode = breakPointList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				
				try {
					lineNumbers.add((Integer.valueOf(eElement.getElementsByTagName("lineNumber").item(0).getTextContent())));
				}catch (Exception e) {
					System.out.println("No line number added to the breakpoint");
				}
	
			}
		}
		
		
		
		if (lineNumbers.size() > 0)
		{
			for (Integer lineNumber : lineNumbers)
			{
				script.setBreakPoints(new BreakPoint(lineNumber));
			}
		}else {
			System.out.println("No breakpoints added");
		}
		

		
		

			for (int i = 0; i < breakPointList.getLength(); i++) 
			{
				Node currentNode = breakPointList.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentNode;
	
					NodeList breakPointInfo = element.getChildNodes();
					//Loop through the element nodes
					for (int j = 0; j < breakPointInfo.getLength(); j++) {
						int lineNumber = 0;
						String varName = null;
										
						Node currentChildNode = breakPointInfo.item(j);
						if (currentChildNode.getNodeType() == Node.ELEMENT_NODE) {
							Element childElemt = (Element) currentChildNode;
							
							if (childElemt.getTagName().equals("lineNumber")) {
								lineNumber = (Integer.parseInt(childElemt.getTextContent()));
								if (lineNumber <= 0)
								{
									UserNotification.warning("Script Error", "Line number missing", "When suppling a breakpoint script tag you must supply a line number for said breakpoint. ");
								}
								

							}
			
					}
				}
			}
			
		}*/

	
	/** 
	private void setWatchPoints(NodeList watchPointList) {
		for (int i = 0; i < watchPointList.getLength(); i++) {
			Node currentNode = watchPointList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) currentNode;

				NodeList watchPointInfo = element.getChildNodes();
				for (int j = 0; j < watchPointInfo.getLength(); j++) {
					Node currentChildNode = watchPointInfo.item(j);
					if (currentChildNode.getNodeType() == Node.ELEMENT_NODE) {
						Element childElemt = (Element) currentChildNode;
						if (childElemt.getTagName().equals("variableName")) {
							watchPointName.add(childElemt.getTextContent());
						} else if (childElemt.getTagName().equals("methodName")) {
							methodNames.add(childElemt.getTextContent());

						} else if (childElemt.getTagName().equals("className")) {
							classNames.add(childElemt.getTextContent());
						}
					}
				}
			}
		}
		
		if (watchPointName != null) {
			
		for (String varName : watchPointName)
		{
			if (suspendThread == null && suspendVM.equals("true")) {
				watchPoint = new WatchPoint(varName, "suspendVM");
				script.setWatchPoints(watchPoint);
			} else if (suspendVM == null && suspendThread.equals("true")) {
				watchPoint = new WatchPoint(varName, "suspendThread");
				script.setWatchPoints(watchPoint);
			} else if (suspendThread.equals("true") && suspendVM.equals("true")) {
				watchPoint = new WatchPoint(varName, true, true);
				script.setWatchPoints(watchPoint);
				System.out.println("Watchpoint made and added to the list");
			}
			else
			{
				watchPoint = new WatchPoint(varName);
			}
		}

			watchPointName = null;
			suspendThread = null;
			suspendVM = null;
		} else {
			UserNotification.warning("Warning", "Specify a variable name ",
					"Please provide the variable name you wish to add as a watchpoint ");
		}
		
		
	}
	*/
	


