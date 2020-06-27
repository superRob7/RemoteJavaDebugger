package connection;



	
	import java.io.IOException;

	import controllers.MainController;
	import debuggee.Debuggee;
	import functions.ConnectionBundle;
	import functions.Log;
	import javafx.fxml.FXMLLoader;
	import javafx.scene.Parent;
	import javafx.scene.Scene;
	import javafx.stage.Stage;

	/**
	 * This class controls the attempts to establish a connection between
	 * the debugger and debuggee. 
	 * @author {Rober Dunsmore, 2718125}
	 *
	 */
	public class ConnectionManager {

		/** Reference to connection information */
		private ConnectionBundle conInfo;

		/** Reference to the target VM */
		private Debuggee debuggee;
		
		/**Flag to till if its the first connection or a reconnection attempt*/
		private boolean alreadyLoadedFlag = false;
		
		private MainController mainScene;

		/**
		 * Constructor to all for the connection bundle information to be passed to be
		 * utilised within this class
		 * 
		 * @param conInfo
		 */
		public ConnectionManager(ConnectionBundle conInfo) {
			this.conInfo = conInfo;
			debuggee = new Debuggee();
			debuggee.setLogger(new Log());
			
			if (conInfo.getLogLevel() != null)
			{
				debuggee.getLogger().setCustomLevel(conInfo.getLogLevel());
			}
			
			if (conInfo.getLogLocation() != null)
			{
				debuggee.getLogger().setCustomLocation(conInfo.getLogLocation());
				debuggee.getLogger().setup();
			}
			else
			{
				debuggee.getLogger().setup();
			}
			
			
			
			

		}

		/**
		 * Starts a connection attempt, using information supplied by the user.
		 * 
		 */
		public void run() {

			try {
				// Determine the type of connection to make
				switch (conInfo.getConType()) {
				case ATTACH:
					if (conInfo.isServer())
					{
						listen();
					}
					else
					{
						attach();
					}
					
					break;
				case LAUNCH:
					launch();
					break;
				}
			}catch (Exception e) {
				debuggee.getLogger().logDebuggerError("Failed to connect to target : " + e);
			}
			
			if (debuggee.isConnected()) {
				debuggee.getVM().suspend();
				
				if (alreadyLoadedFlag)
				{
					//Passing this instance of the connection object to be called latter to reconnect to the target VM
					conInfo.setCurrentConnection(this);
					
					//passing the debuggee & connection info before the state is show to user 
					mainScene.setDebuggee(debuggee, conInfo);
					
					return;
				}
				else
				{
					loadNextStage();
				}
			}
			
			

		}

		/**
		 * Attempt to attach to an already running VM
		 */
		private void attach() {
			// A new attach connection object
			ConnectionAttach conAttach = new ConnectionAttach(conInfo);

			try{
				// Setting the debuggee object to store a mirror of the target VM
				debuggee.setVM(conAttach.attach());
			}catch (Exception e) {
				debuggee.getLogger().logDebuggerWarning("Attach unsuccessful");
			}
			
			
				if (debuggee.getVM() != null) {
					debuggee.setConnected(true);
					// Logging the success
					debuggee.getLogger().logDebuggerInfo("Attach successful");
				} 


		}
		
		private void listen()
		{
			// A new attach connection object
			ConnectionListen conListen = new ConnectionListen(conInfo);

			
				try{
					// Setting the debuggee object to store a mirror of the target VM
					debuggee.setVM(conListen.listen());
					
					
					if (debuggee.getVM() != null) {
						debuggee.setConnected(true);
						// Logging the success
						debuggee.getLogger().logDebuggerInfo("Attach successful");
					}
					
					
					
				}catch (Exception e) {
					debuggee.getLogger().logDebuggerWarning("Attach unsuccessful");
					return;
				}
				
				
		}

		/**
		 * Attempt to launch a local VM
		 */
		private void launch()  {

			// A new launch connection object
			ConnectionLaunch conLaunch = new ConnectionLaunch(conInfo);

		
			try{
				// Setting the debuggee object to store a mirror of the target VM
				debuggee.setVM(conLaunch.safeStart());
			}catch (Exception e) {
				debuggee.getLogger().logDebuggerWarning("Launch unsuccessful");
			}
			
			if (!(debuggee.getVM() == null)) {
				debuggee.setConnected(true);
				debuggee.getLogger().logDebuggerInfo("Launch successful.");
			} 

		}

		/**
		 * This method loads the next stage of the debugger, once the connection to the
		 * target vm has been established.
		 */
		private void loadNextStage() {

			try {
				
				

				String fxmlResource = "/xmlLayouts/mainLayout.fxml";

				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlResource));
				Parent root1 = (Parent) fxmlLoader.load();

				 mainScene = fxmlLoader.getController();
				
				//Passing this instance of the connection object to be called latter to reconnect to the target VM
				conInfo.setCurrentConnection(this);
				
				//passing the debuggee & connection info before the state is show to user 
				mainScene.setDebuggee(debuggee, conInfo);

				Stage stage = new Stage();
				stage.setScene(new Scene(root1));
				stage.setTitle("Debugger");
				
				alreadyLoadedFlag = true;
				
				stage.show();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Debuggee getDebuggee() {
			return debuggee;
		}

	
}
