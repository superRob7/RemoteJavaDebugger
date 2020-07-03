package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main root of execution. Create an FX scene, load related XML layout
 * launch application.
 * @author {Robert Dunsmore, 2718125}
 *
 */
public class Main extends Application {
	
	String fxmlResource = "/xmlLayouts/connectionLayout.fxml";

	@Override
	public void start(Stage primaryStage) throws IOException
	{
		Parent root = FXMLLoader.load(getClass().getResource(fxmlResource));
		Scene scene = new Scene(root);

		primaryStage.setTitle("Debugger");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}
	public static void main(String[] args) {
		launch(args);
	}
}
