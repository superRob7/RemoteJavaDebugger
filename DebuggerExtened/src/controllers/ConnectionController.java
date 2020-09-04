package controllers;

import static functions.ConnectionEnums.ATTACH;
import static functions.ConnectionEnums.LAUNCH;

import java.io.File;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connection.ConnectionManager;
import debuggee.Debuggee;
import functions.ConnectionBundle;
import functions.ConnectionEnums;
import functions.InformationBuilder;
import functions.UserNotification;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * This call is the controller class for the connection page that is shown to
 * the user. This class will gather the connection information needed to connect
 * to the required target VM. This class does not perform any of the connection,
 * rather just gathers the information needed to make the connection.
 * 
 * @author Robert
 */
public class ConnectionController {
	//////////////////////////////////////// FXML Page
	//////////////////////////////////////// Elements/////////////////////////////
	@FXML
	RadioButton launchRadio, attachRadio, suspendToggle, serverToggle;

	@FXML
	TextArea connectInfoArea;

	@FXML
	TextField portInput, hostInput;

	@FXML
	Pane leftPane, centerPane, rightPane;

	@FXML
	HBox hBoxPane;

	@FXML
	public void initialize() {
		launchRadio.setSelected(true);
		launchRadio.requestFocus();
		getSelectedConnectionType();
	}

	public void changeLogLocation() {
		logLocation = UserNotification.getLogLocation();

		if (logLocation != null) {
			logLocation = logLocation.replace('\\', '/');
			logLocation = logLocation.replace(" ", "");
		}
		System.out.println(logLocation);

	}

	public void changeLogLevel() {
		logLevel = UserNotification.getLogLevel().trim();

	}

	//////////////////////////////////////// Locale
	//////////////////////////////////////// variables/////////////////////////////

	private StringBuilder output = new StringBuilder();

	private InformationBuilder infoBuild = new InformationBuilder();

	/** Instance of connection information */
	private ConnectionEnums conType;

	/** File chooser to select target VM class file */
	private FileChooser fileChooser = new FileChooser();

	/** Holds the string value of the file path */
	private String filepath;

	/** Holds the String of the host to attach to */
	private String host;

	/** Holds the int value of the port to attach to */
	private int portNumber;

	/** Determines if the target is to be suspend */
	private boolean suspend;

	/** Determines if the debugger is to be the server */
	private boolean server;

	/** Instance of connection information */
	private ConnectionBundle conInfo = new ConnectionBundle();

	/** Hold the new log location */
	private String logLocation, logLevel = null;

	/** Hold the debuggee object, used to access the log object */
	private Debuggee debuggee = null;

	/**
	 * This method will pull the required information from the user to connect to a
	 * VM. This method will inform the user if they have missing or incorrect
	 * information that is still required. Once satisfied this class will open the
	 * debugger interface, once the connection with the VM is established
	 */
	public void connect() {
		if (validateUserInput() == 0) {
			output.append(connectInfoArea.getText());

			output.append("\n");
			output.append("Connecting on port " + portInput.getText());

			if (server) {
				output.append("\n");
				output.append("server on. waiting ....");
			} else {
				output.append("\n");
				output.append("server off");
			}

			if (suspend) {
				output.append("\n");
				output.append("process suspended. waiting .....");
			} else {
				output.append("\n");
				output.append("process running");
			}

			output.append("\n");
			output.append("Trying to connect.....");

			connectInfoArea.setText(output.toString());

			try {
				// attempt the connection to the target
				attemptConnection();
			} catch (Exception e) {
				connectInfoArea
						.setText("Connection to the target VM failed." + "\n" + " Consult the debugger log file");

				if (logLocation != null) {
					connectInfoArea.setText(connectInfoArea.getText() + "\n"
							+ "You may have entered the new log directory incorrectly ");
				}
			}

		}

	}

	/**
	 * This methods is used to display the new debugger scene, while hiding the
	 * connection scene.
	 */
	private void attemptConnection() {

		ConnectionManager con = null;

		if (logLocation != null) {
			conInfo.setLogLocation(logLocation);
		}

		if (logLevel != null) {
			conInfo.setLogLevel(logLevel);
		}

		try {

			// If the connection type is shared then we dont need more info.
			if (conType.equals(ConnectionEnums.LAUNCH)) {
				if (getDebugFile() != true) {
					connectInfoArea.setText("Please select the compiled class file that contains the main method. "
							+ "\n" + " The file extension must end in '.class'");
					return;
				}
				
				

				conInfo.setFilePath(filepath);
				
				conInfo.setConType(conType);
				conInfo.setServer(server);
				conInfo.setSuspend(suspend);
				
				con = new ConnectionManager(conInfo);
				
				debuggee = con.getDebuggee();
				con.run();
				

			} else {
				// Storing all the connection info to be used when connecting to the
				// VM
				conInfo.setPortNumber(portNumber);
				conInfo.setHost(host);
				conInfo.setFilePath(UserNotification.getTargetVMClassName());
				conInfo.setServer(server);
				conInfo.setSuspend(suspend);
				conInfo.sethBoxPane(hBoxPane);
				conInfo.setConnectInfoArea(connectInfoArea);
				conInfo.setConType(conType);
				con = new ConnectionManager(conInfo);
				debuggee = con.getDebuggee();
				con.run();
			}

		} catch (Exception e) {
			e.printStackTrace();
			connectInfoArea.setText("Connection to the target VM failed." + "\n" + " Consult the debugger log file");
			debuggee.getLogger().logDebuggeeError("Connection to the target VM failed." + e.toString());
		}

		if (con.getDebuggee().getVM() == null) {
			UserNotification.warning("Warrning", "Connection Faild", "Could not connect");
			debuggee.getLogger().logDebuggeeError("Connection to the target VM failed.");
			connectInfoArea.setText("Connection failed" + "\n" + "Please try again.");
		} else {

			// Hiding this scene from the user
			hBoxPane.getScene().getWindow().hide();
			if (logLevel != null)
				;
			{
				debuggee.setCustomLogLevel(Level.parse(logLevel));
			}

		}

	}

	/**
	 * This method is to get the type of radio button to determine the type of
	 * connection the user would like to perform
	 */
	public void getSelectedConnectionType() {
		// Sets the page up, and back to normal if the connection type changes
		pageSetUp();

		if (launchRadio.isSelected()) {
			connectInfoArea.setText(infoBuild.connectionInfo(launchRadio.getText()));
			conType = LAUNCH;
			updateUI();
		}

		if (attachRadio.isSelected()) {
			connectInfoArea.setText(infoBuild.connectionInfo(attachRadio.getText()));
			conType = ATTACH;
			updateUI();
		}

	}

	public void getSelectedToggles() {

		
			if (serverToggle.isSelected() && conType.equals(ATTACH))
			{
				server = true;
				hostInput.setEditable(false);
				hostInput.setDisable(true);
			}
			
			if (!serverToggle.isSelected() && conType.equals(ATTACH))
			{
				server = false;
				hostInput.setEditable(true);
				hostInput.setDisable(false);
			}

	}

	/**
	 * This method is responsible for getting the file location of the local target
	 * 
	 * @return boolean : True = success, False = fail;
	 */
	private boolean getDebugFile() {
		try {
			Stage stage = (Stage) centerPane.getScene().getWindow();

			File file = fileChooser.showOpenDialog(stage);

			filepath = file.getAbsolutePath();

			String fileEx = filepath.substring(filepath.lastIndexOf('.'));

			if (fileEx.equals(".class")) {
				return true;
			} else {
				connectInfoArea.setText("Invalid file type selected.\n Please select a '.class' file");
				UserNotification.information("File selection ", "Select a file",
						"Please select the compiled class file that contains the main method, the file extension must end in '.class'");
				return false;
			}

		} catch (NullPointerException e) {
			UserNotification.information("File selection ", "Select a file",
					"Please select the compiled class file that contains the main method, the file extension must end in '.class'");
			return false;
		}

	}

	/**
	 * This method is called when the user selects to use a shared memory location
	 * to connect the debuggee to. The information needed to connect through sockets
	 * is grayed out, to inform the user that they do not need to supply this
	 * information.
	 */
	public void updateUI() {

		switch (conType) {
		case ATTACH:			
			portInput.setEditable(true);
			portInput.setDisable(false);
			hostInput.setEditable(true);
			hostInput.setDisable(false);
			serverToggle.setSelected(false);
			serverToggle.setDisable(false);
			suspendToggle.setSelected(false);
			suspendToggle.setDisable(true);
			break;
		case LAUNCH:
			portInput.setEditable(false);
			portInput.setDisable(true);
			hostInput.setEditable(false);
			hostInput.setDisable(true);
			serverToggle.setSelected(false);
			serverToggle.setDisable(true);
			suspendToggle.setSelected(true);
			suspendToggle.setDisable(false);
			break;

		}

	}

	/**
	 * This method is used to allow the port and sever type text fields be used.
	 * This is recalled when one of the first two radio buttons are clicked, as the
	 * socket and server type are needed to connect to the VM
	 */
	private void pageSetUp() {
		connectInfoArea.setEditable(false);
		portInput.setEditable(true);
		portInput.setDisable(false);
		portInput.setText(null);

		hostInput.setEditable(true);
		hostInput.setDisable(false);
		hostInput.setText(null);

		serverToggle.setDisable(false);
		suspendToggle.setDisable(false);

	}

	/**
	 * This method is used to validate the users input, this method will return an
	 * int to determine what output should be displayed to the user. The state of
	 * the toggle buttons are also saved within this method
	 * 
	 * @return -4 : Port number is empty -3 : Port number input not valid -2 : Host
	 *         Number is empty -1 : Host number is not valid 0 : All is ok
	 */
	private int validateUserInput() {
		String portNum, hostNum;

		portNum = portInput.getText();
		hostNum = hostInput.getText();

		String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

		switch (conType) {
		case ATTACH:

			if (serverToggle.isSelected()) {
				if (portInput == null | portInput.getLength() < 0) {
					UserNotification.warning("Warning!", "Port number is missing",
							"Please provid an integer value of the port number you would like to attch to.");
					return -4;
				}

				try {
					portNumber = Integer.parseInt(portNum);

				} catch (Exception e) {
					UserNotification.warning("Warning!", "Port number invalid",
							"Please provid an integer value of the port number you would like to attch to.");
					return -3;
				}

				host = "localHost";
				return 0;
			}

			if (portInput == null | portInput.getLength() < 0) {
				UserNotification.warning("Warning!", "Port number is missing",
						"Please provid an integer value of the port number you would like to attch to.");
				return -4;
			}

			try {
				portNumber = Integer.parseInt(portNum);

			} catch (Exception e) {
				UserNotification.warning("Warning!", "Port number invalid",
						"Please provid an integer value of the port number you would like to attch to.");
				return -3;
			}

			if (hostInput == null | hostInput.getLength() < 0) {
				UserNotification.warning("Warning!", "Host IP is missing",
						"Please provid an Host/IP of the machine you would like to attch to.");
				return -2;
			} else {
				try {
					if (hostNum.equals("localHost")) {
						host = hostNum;
						return 0;
					}
					Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
					Matcher matcher = pattern.matcher(hostNum);
					if (!matcher.matches()) {
						UserNotification.warning("Warning!", "Host IP is invalid", "Please provid a valid IP address.");
						return -1;
					}
				} catch (Exception e) {
					UserNotification.warning("Warning!", "Host IP is invalid", "Please provid a valid IP address.");
					return -1;
				}

				host = hostNum;
			}

			break;
		case LAUNCH:
			server = serverToggle.isSelected();
			suspend = suspendToggle.isSelected();
			output.append("\n" + suspend);
			return 0;
		}

		return 0;

	}

}
