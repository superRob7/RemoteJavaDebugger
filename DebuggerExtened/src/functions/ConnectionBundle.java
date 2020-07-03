package functions;

import java.util.logging.Level;

import connection.ConnectionManager;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

/**
 * This class is an entity class that will contain the information about the required debugging connection.
 * The information held in this class will only be relevant to the current request.  
 * @author robert
 *
 */
public class ConnectionBundle {
	
	private ConnectionManager currentConnection;
	
	private String filePath, host, logLocation;
	
	private int portNumber;
	
	private HBox hBoxPane;
	
	private TextArea connectInfoArea;
	
	private boolean server , suspend;
	
	private ConnectionEnums conType;
	
	private Level logLevel;
	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getClassNameFromPath()
	{
		String fileName;
		fileName = filePath.substring(filePath.lastIndexOf('\\') +1, filePath.length());
		return fileName;
	}
	
	public String getClassNameNoEx() {
		String fileName;
		fileName = filePath.substring(filePath.lastIndexOf('\\') +1, filePath.indexOf('.'));
		return fileName;
	}
	
	public String getClassLocation()
	{
		String fileName;
		fileName = filePath.substring(0, filePath.lastIndexOf('\\'));
		return fileName;
	}
	
	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public HBox gethBoxPane() {
		return hBoxPane;
	}

	
	public void sethBoxPane(HBox hBoxPane) {
		 this.hBoxPane = hBoxPane;
		
	}

	public TextArea getConnectInfoArea() {
		return connectInfoArea;
	}

	public void setConnectInfoArea(TextArea connectInfoArea) {
		this.connectInfoArea = connectInfoArea;
	}

	public boolean isSuspend() {
		return suspend;
	}

	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}

	public boolean isServer() {
		return server;
	}

	public void setServer(boolean sever) {
		this.server = sever;
	}

	public ConnectionEnums getConType() {
		return conType;
	}

	public void setConType(ConnectionEnums conType) {
		this.conType = conType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setLogLocation(String logLocation) {
		this.logLocation = logLocation;
		
	}

	public String getLogLocation()
	{
		return logLocation;
	}

	public ConnectionManager getCurrentConnection() {
		return currentConnection;
	}

	public void setCurrentConnection(ConnectionManager currentConnection) {
		this.currentConnection = currentConnection;
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		switch (logLevel)
		{
		case "SEVERE": setLogLevel(Level.SEVERE);
			break;
		case"WARNING" :setLogLevel(Level.WARNING);
			break;
		case"INFO":
			break;
		case"CONFIG":setLogLevel(Level.CONFIG);
			break;
		case"FINE":setLogLevel(Level.FINE);
			break;
		case"FINER":setLogLevel(Level.FINER);
			break;
		case"FINEST":setLogLevel(Level.FINEST);
			break;
		}
	}
	
	private void setLogLevel(Level level)
	{
		logLevel = level;
	}


}
