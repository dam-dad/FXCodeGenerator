package dad.codegen.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.Property;
import dad.codegen.model.javafx.Type;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PropertyController implements Initializable {
	
	// model
	
	private Property property;
	
	private Stage stage;
	
	// view
	
	@FXML
	private BorderPane view;
	
	@FXML 
	private TextField nombreText;
	
	@FXML 
	private CheckBox soloLecturaCheck;

	@FXML
	private ComboBox<Type> tipoCombo;

	@FXML
	private ComboBox<Bean> genericoCombo;
	
	@FXML
	private Button volverButton;
	
	public PropertyController() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PropertyView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.tipoCombo.setItems(FXCollections.observableArrayList(Type.values()));
	}
	
	public void bind(Property property, ListProperty<Bean> beans) {
		unbind(this.property);
		this.property = property;
		this.nombreText.textProperty().bindBidirectional(property.nameProperty());
		this.soloLecturaCheck.selectedProperty().bindBidirectional(property.readOnlyProperty());
		this.tipoCombo.valueProperty().bindBidirectional(property.typeProperty());
		this.genericoCombo.itemsProperty().bind(beans);
		this.genericoCombo.valueProperty().bindBidirectional(property.genericProperty());
	}
	
	public void unbind(Property property) {
		if (property == null) return;
		this.property = null;
		this.nombreText.textProperty().unbindBidirectional(property.nameProperty());
		this.soloLecturaCheck.selectedProperty().unbindBidirectional(property.readOnlyProperty());
		this.tipoCombo.valueProperty().unbindBidirectional(property.typeProperty());
		this.genericoCombo.itemsProperty().unbind();
		this.genericoCombo.valueProperty().unbindBidirectional(property.genericProperty());
	}
	
	public BorderPane getView() {
		return view;
	}
	
	@FXML
	private void onVolverButtonAction(ActionEvent e) {
		unbind(property);
		stage.close();
	}
	
	public void showOnStage(Window owner) {
		stage = new Stage();
		stage.getIcons().addAll(((Stage)owner).getIcons());
		stage.setTitle("Editar propiedad");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(owner);
		stage.setScene(new Scene(view, 400, 200));
		stage.showAndWait();
	}

}
