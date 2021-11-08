package dad.codegen.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.FXModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class RootController implements Initializable {

	private FileChooser fileChooser;
	private DirectoryChooser directoryChooser;

	// model

	private ObjectProperty<FXModel> fxModel = new SimpleObjectProperty<>();

	// controllers

	private BeanController beanController;

	// view

	@FXML
	private BorderPane view;

	@FXML
	private ListView<Bean> beansList;

	@FXML
	private BorderPane beanPane;

	@FXML
	private Button nuevoButton, abrirButton, guardarButton, generarButton;

	@FXML
	private Button nuevoBeanButton, eliminarBeanButton;

	@FXML
	private VBox infoPane;

	@FXML
	private TextField paqueteText;

	public RootController() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		beanController = new BeanController();

		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Modelo FX (*.fx)", "*.fx"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Todos los ficheros (*.*)", "*.*"));
		fileChooser.setInitialDirectory(new File("."));

		directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File("."));

		this.beansList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				this.beanPane.setCenter(infoPane);
			} else {
				this.beanController.setBeans(getFxModel().beansProperty());
				this.beanController.setBean(newValue);
				this.beanPane.setCenter(this.beanController.getView());
			}
		});

		eliminarBeanButton.disableProperty().bind(beansList.getSelectionModel().selectedItemProperty().isNull());
		
		fxModel.addListener((o, ov, nv) -> onFXModelChanged(o, ov, nv));
		
		fxModel.set(new FXModel());
	}

	private void onFXModelChanged(ObservableValue<? extends FXModel> o, FXModel ov, FXModel nv) {
		
		if (ov != null) {
			this.beansList.itemsProperty().unbind();
			this.paqueteText.textProperty().unbindBidirectional(ov.packageNameProperty());
		}
		
		if (nv != null) {
			this.beansList.itemsProperty().bind(nv.beansProperty());
			this.paqueteText.textProperty().bindBidirectional(nv.packageNameProperty());			
		}
		
	}

	public BorderPane getView() {
		return view;
	}

	@FXML
	private void onNuevoButtonAction(ActionEvent e) {
		if (CodeGenApp.confirm("Nuevo modelo FX",
				"Se va a crear un nuevo modelo FX.\n\nLos cambios que haya realizado en el modelo actual se perderán.",
				"¿Desea continuar?")) {
			setFxModel(new FXModel());
		}
	}

	@FXML
	private void onAbrirButtonAction(ActionEvent e) {
		if (CodeGenApp.confirm("Abrir modelo FX",
				"Va a abrir un modelo FX desde fichero.\n\nLos cambios que haya realizado en el modelo actual se perderán.",
				"¿Desea continuar?")) {
			File fichero = fileChooser.showOpenDialog(CodeGenApp.getPrimaryStage());
			if (fichero != null) {
				try {
					FXModel fxModel = FXModel.load(fichero);
					setFxModel(fxModel);
				} catch (Exception e1) {
					CodeGenApp.error("Error al abrir el modelo FX desde el fichero '" + fichero.getName() + "'.", e1.getMessage());
				}
			}
		}
	}

	@FXML
	private void onGuardarButtonAction(ActionEvent e) {
		File fichero = fileChooser.showSaveDialog(CodeGenApp.getPrimaryStage());
		if (fichero != null) {
			try {
				getFxModel().save(fichero);
			} catch (Exception e1) {
				CodeGenApp.error("Error al guardar el modelo FX en el fichero '" + fichero.getName() + "'.",
						e1.getMessage());
			}
		}
	}

	@FXML
	private void onGenerarButtonAction(ActionEvent e) {
		File directorio = directoryChooser.showDialog(CodeGenApp.getPrimaryStage());
		if (directorio != null) {
			try {
				getFxModel().generateCode(directorio);
				CodeGenApp.info(
						"Se ha generado el código correctamente en el directorio '" + directorio.getName() + "'.", "");
			} catch (Exception e1) {
				e1.printStackTrace();
				CodeGenApp.error(
						"Error al generar el código del modelo FX en el directorio '" + directorio.getName() + "'.",
						e1.getMessage());
			}
		}
	}

	@FXML
	private void onNuevoBeanButtonAction(ActionEvent e) {
		Bean bean = new Bean();
		bean.setName("NuevoBean");
		this.getFxModel().getBeans().add(bean);
		this.beansList.getSelectionModel().select(bean);
	}

	@FXML
	private void onEliminarBeanButtonAction(ActionEvent e) {
		Bean seleccionado = beansList.getSelectionModel().getSelectedItem();
		if (CodeGenApp.confirm("Eliminar bean", "Se va a eliminar el bean '" + seleccionado.getName() + "'.",
				"¿Desea continuar?")) {
			getFxModel().getBeans().remove(seleccionado);
		}
	}

	public final ObjectProperty<FXModel> fxModelProperty() {
		return this.fxModel;
	}

	public final FXModel getFxModel() {
		return this.fxModelProperty().get();
	}

	public final void setFxModel(final FXModel fxModel) {
		this.fxModelProperty().set(fxModel);
	}

}
