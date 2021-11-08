package dad.codegen.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CodeGenApp extends Application {
	
	private RootController rootController;
	
	private static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		CodeGenApp.primaryStage = primaryStage;

		rootController = new RootController();
		
		primaryStage.setTitle("FXCodeGenerator");
		primaryStage.setScene(new Scene(rootController.getView(), 640, 480));
		primaryStage.getIcons().add(new Image("images/fx-64x64.png"));	
		primaryStage.show();
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void error(String header, String content) {
		Alert error = new Alert(AlertType.ERROR);
		error.initOwner(getPrimaryStage());
		error.setTitle("Error");
		error.setHeaderText(header);
		error.setContentText(content);
		error.showAndWait();
	}
	
	public static boolean confirm(String title, String header, String content) {
		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.initOwner(getPrimaryStage());
		confirm.setTitle(title);
		confirm.setHeaderText(header);
		confirm.setContentText(content);
		return confirm.showAndWait().get().equals(ButtonType.OK);
	}
	
	public static void info(String header, String content) {
		Alert info = new Alert(AlertType.INFORMATION);
		info.initOwner(getPrimaryStage());
		info.setTitle(getPrimaryStage().getTitle());
		info.setHeaderText(header);
		info.setContentText(content);
		info.showAndWait();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
