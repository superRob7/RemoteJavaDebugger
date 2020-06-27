package functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

public class UserNotification {

	
	public static void warning (String title, String header, String context)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}
	
	public static void information(String title, String header, String context)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(context);
		alert.showAndWait();
	}
	
	public static String getTargetVMClassName()
	{
		TextInputDialog dialog = new TextInputDialog("Class Name");
		dialog.setTitle("Class Name");
		dialog.setHeaderText("The class name is required");
		dialog.setContentText("Please enter the class name:");
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent())
		{
			return result.get();
		}
		return null;
	}
	
	public static String getLogLocation()
	{
		TextInputDialog dialog = new TextInputDialog("Log Location");
		dialog.setTitle("Log Location");
		dialog.setHeaderText("The specified location to store the log files ");
		dialog.setContentText("Please enter the directory:");
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent())
		{
			return result.get();
		}
		return null;
	}
	
	public static String  getLogLevel()
	{
		List<String> levels = new ArrayList<>();
		levels.add("SEVERE ");
		levels.add("WARNING");
		levels.add("INFO");
		levels.add("CONFIG");
		levels.add("FINE");
		levels.add("FINER");
		levels.add("FINEST");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("INFO", levels);
		dialog.setTitle("Log Level");
		dialog.setHeaderText("Selecte the detail level of the log file");
		dialog.setContentText("Select the log level:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			return result.get();
		}
		return null;
	}
	
}
