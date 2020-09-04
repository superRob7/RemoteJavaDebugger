package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import debuggee.Debuggee;
import events.EventDispatcher;
import functions.ConnectionBundle;
import functions.ConnectionEnums;
import functions.Log;
import functions.UserNotification;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import script.Script;
import script.XMLScriptReader;

public class MainController {

	//////////////////////////////////////// FXML Page
	//////////////////////////////////////// Elements/////////////////////////////

	@FXML
	AnchorPane scriptLeftPane, mainPane;

	@FXML
	TextArea infoArea, actionArea;

	@FXML
	Button intoBtnm, uploadBtn, clearBtn, runBtn;

	//////////////////////////////////////// Locale
	//////////////////////////////////////// variables/////////////////////////////

	/** Instance of connection information */
	private ConnectionBundle conInfo;

	/** Instance of debuggee object */
	private Debuggee debuggee;

	/** Instance of Event Dispatcher to dispatch target VM events */
	private EventDispatcher eventDispatcher;

	/** File chooser to select debugging script file */
	private FileChooser fileChooser = new FileChooser();

	/** Instance of the XML script reader that phrase the script file */
	private XMLScriptReader scriptReader;

	private Log logger;

	/** Instance of a script object to represent the phrased script */
	private Script script;

	/**
	 * This method is called by javafx internal when the new scene is loaded
	 * 
	 * @param location
	 * @param resources
	 */
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void reconnect() {
		conInfo.getCurrentConnection().run();
		// infoArea.setText("The connection to the target was restarted");
		debuggee.getLogger().logDebuggerInfo("Connection to target VM restarted");

	}

	public void closeConnection() {
		debuggee.getVM().exit(0);
		// infoArea.setText("The connection to the target VM is closed");
		debuggee.getLogger().logDebuggerInfo("Manual disconection for target VM");
	}


	//////////////////////////////////////// Getters
	//////////////////////////////////////// Setters/////////////////////////////

	/**
	 * returns this objects ConnectionBundle
	 * 
	 * @return ConnectionBundle : connection information
	 */
	public ConnectionBundle getConInfo() {
		return conInfo;
	}

	/**
	 * Set an instance of ConnectinBundle to this objects internal reference
	 * 
	 * @param conInfo : ConnectionBundle
	 */
	public void setConInfo(ConnectionBundle conInfo) {
		this.conInfo = conInfo;
	}

	/**
	 * Returns this object Debuggee object
	 * 
	 * @return Debuggee : Target VM
	 */
	public Debuggee getDebuggee() {
		return debuggee;
	}

	/**
	 * Set an instance of Debuggee to this objects internal reference
	 * 
	 * @param debuggee
	 */
	public void setDebuggee(Debuggee debuggee) {
		this.debuggee = debuggee;
	}

	/**
	 * This method is called by the ConnectionConroller when it is ready to load the
	 * main scene. This method is used to pass the connection information from the
	 * connection controller to the main controller.
	 * 
	 * @param conInfo : The connection information
	 */
	public void setDebuggee(Debuggee debuggee, ConnectionBundle conInfo) {

		this.debuggee = debuggee;
		this.conInfo = conInfo;

		if (this.conInfo.getLogLocation() != null) {
			logger = new Log();
			logger.setCustomLocation(this.conInfo.getLogLocation());
		}

	}

	public Script getScript() {
		return script;
	}

	

	//////////////////////// Script
	//////////////////////// Pane////////////////////////////////////////////////

	public void clearScript() {
		if (script != null) {
			script = null;
			infoArea.setText("Script cleared, please upload a new script.");
			debuggee.getLogger().logDebuggerInfo("Script cleared successfully.");
		}

	

	}

	/**
	 * This method is responsible for locating the script file, passing the file to
	 * be pharsed and instantiating this object script file. (Result of uploadBtn
	 * being activated)
	 */
	public void uploadScript() {
		Stage stage = (Stage) scriptLeftPane.getScene().getWindow();
		File file = null;
		try {
			file = fileChooser.showOpenDialog(stage);
			
			String filePath = file.getAbsolutePath();

			String fileEx = filePath.substring(filePath.lastIndexOf('.'));

			if (!fileEx.equals(".xml")) {
			
				infoArea.setText("Invalid file type selected.\n Please select a '.xml' file");
				UserNotification.information("File selection ", "Select a file",
						"Please select a valid XML script file that contains at lest one breakpoint, the file extension must end in '.xml'");
				return;
			}


		} catch (Exception e) {
			UserNotification.information("Script", "Invalid script", "Please select an XML script");
			debuggee.getLogger().logDebuggerInfo("Failed to read script : " + e);
		}

		try {
			scriptReader = new XMLScriptReader(file);
			script = scriptReader.readScript();
		} catch (Exception e) {
			System.out.println("Failed to read script file.");
			debuggee.getLogger().logDebuggerInfo("Failed to read script : " + e);
		}

		if (script != null) {
			

			debuggee.getLogger().logDebuggerInfo("Script uploaded successfully.");

		} else {
			infoArea.setText("Script uploaded failed \n Please provide attlest one breakpoint.");
			debuggee.getLogger().logDebuggerInfo(
					"Script uploaded failed. Scripts must contain at lest a breakpoint and linenumber tag");
		}

	}

	/**
	 * This method attempts to execute command to the target VM using the script
	 * object attached to this instance of the class
	 */
	public void runScript() {

		infoArea.clear();
		
		if (!debuggee.isConnected())
		{
			UserNotification.information("Connection", "Connection Lost",
					"Please reconnect to the debuggee to execute a script.");
		}

		if (script != null) {
			infoArea.setText(infoArea.getText() + "\n" + "Running script");
			debuggee.getVM().resume();

			eventDispatcher = new EventDispatcher(debuggee, script, 0);

			eventDispatcher.start();

			try {
				eventDispatcher.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			displayDebuggeeInfo();

			if (conInfo.getConType().equals(ConnectionEnums.LAUNCH)) {
				try {
					logDebuggeeStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			

		} else {
			infoArea.setText("Please upload a valid script file.");
		}


	}

	public void displayDebuggeeInfo()
	{
		StringBuilder line = new StringBuilder();
		File debuggeeLogFile = null;
		
		if (conInfo.getLogLocation() == null)
			debuggeeLogFile = new File("./Dubggee.log");
		else
			debuggeeLogFile = new File(conInfo.getLogLocation()+"/Dubggee.log");
		
		try{
			
			FileInputStream fstream  = new FileInputStream(debuggeeLogFile);
			   BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			   String strLine;
			   /* read log line by line */
			   while ((strLine = br.readLine()) != null)   {
				   String currentLine = strLine.substring(strLine.indexOf("V"), strLine.length());
				   line.append(currentLine + "\n");
			   }
			  
			 System.out.println(line);
			   fstream.close();
			} catch (Exception e) {
			     System.err.println("Error: " + e.getMessage());
			}
		
		infoArea.setText(infoArea.getText() + line.toString());
		
	}

	private void logDebuggeeStream() throws IOException {

		final InputStream inputStream = debuggee.getVM().process().getInputStream();

		final StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name()))) {
			int c;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		}

		infoArea.setText(infoArea.getText() + "\n" + "Debuggee Console Output \n" + textBuilder.toString());
	}

}
