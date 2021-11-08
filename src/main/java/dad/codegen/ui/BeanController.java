package dad.codegen.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.codegen.model.javafx.Bean;
import dad.codegen.model.javafx.Property;
import dad.codegen.model.javafx.Type;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;

public class BeanController implements Initializable {

	// mode

	private ObjectProperty<Bean> bean = new SimpleObjectProperty<>();
	private ListProperty<Bean> beans = new SimpleListProperty<>(FXCollections.observableArrayList());

	// view

	@FXML
	private AnchorPane view;

	@FXML
	private TextField nombreText;

	@FXML
	private ComboBox<Bean> padreCombo;

	@FXML
	private TableView<Property> propiedadesTable;

	@FXML
	private TableColumn<Property, String> nombreColumn;

	@FXML
	private TableColumn<Property, Boolean> soloLecturaColumn;

	@FXML
	private TableColumn<Property, Type> tipoColumn;

	@FXML
	private TableColumn<Property, Bean> genericoColumn;

	@FXML
	private Button nuevaPropiedadButton, editarPropiedadButton, eliminarPropiedadButton, quitarPadreButton;

	public BeanController() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BeanView.fxml"));
			loader.setController(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nombreColumn.setCellValueFactory(value -> value.getValue().nameProperty());
		soloLecturaColumn.setCellValueFactory(value -> value.getValue().readOnlyProperty());
		soloLecturaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(soloLecturaColumn));
		tipoColumn.setCellValueFactory(value -> value.getValue().typeProperty());
		genericoColumn.setCellValueFactory(value -> value.getValue().genericProperty());

		editarPropiedadButton.disableProperty()
				.bind(Bindings.size(propiedadesTable.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
		eliminarPropiedadButton.disableProperty()
				.bind(Bindings.isEmpty(propiedadesTable.getSelectionModel().getSelectedItems()));
		quitarPadreButton.disableProperty().bind(padreCombo.valueProperty().isNull());

		bean.addListener((o, ov, nv) -> onBeanChanged(o, ov, nv));
		beans.addListener((o, ov, nv) -> onBeansChanged(o, ov, nv));

	}

	private void onBeansChanged(ObservableValue<? extends ObservableList<Bean>> o, ObservableList<Bean> ov, ObservableList<Bean> nv) {

		if (ov != null) {
			this.padreCombo.setItems(null);
		}

		if (nv != null) {
			this.padreCombo.setItems(nv);
		}

	}

	private void onBeanChanged(ObservableValue<? extends Bean> o, Bean ov, Bean nv) {

		if (ov != null) {
			this.nombreText.textProperty().unbindBidirectional(ov.nameProperty());
			this.padreCombo.valueProperty().unbindBidirectional(ov.parentProperty());
			this.propiedadesTable.itemsProperty().unbind();
		}

		if (nv != null) {
			this.nombreText.textProperty().bindBidirectional(nv.nameProperty());
			this.padreCombo.valueProperty().bindBidirectional(nv.parentProperty());
			this.propiedadesTable.itemsProperty().bind(nv.propertiesProperty());
		}

	}

	public AnchorPane getView() {
		return view;
	}

	@FXML
	private void onNuevaPropiedadButtonAction(ActionEvent e) {
		Property nueva = new Property();
		nueva.setName("nuevaPropiedad");
		nueva.setType(Type.STRING);
		getBean().getProperties().add(nueva);
		propiedadesTable.getSelectionModel().select(nueva);
		onEditarPropiedadButtonAction(e);
	}

	@FXML
	private void onEditarPropiedadButtonAction(ActionEvent e) {
		Property seleccionada = propiedadesTable.getSelectionModel().getSelectedItem();
		PropertyController controller = new PropertyController();
		controller.bind(seleccionada, beans);
		controller.showOnStage(CodeGenApp.getPrimaryStage());
	}

	@FXML
	private void onEliminarPropiedadButtonAction(ActionEvent e) {
		Property seleccionada = propiedadesTable.getSelectionModel().getSelectedItem();
		if (CodeGenApp.confirm("Eliminar propiedad", "Se va a eliminar la propiedad '" + seleccionada.getName() + "'.", "¿Está seguro?")) {
			getBean().getProperties().remove(seleccionada);
		}
	}

	@FXML
	private void onQuitarPadreButtonAction(ActionEvent e) {
		this.getBean().setParent(null);
	}

	public final ObjectProperty<Bean> beanProperty() {
		return this.bean;
	}

	public final Bean getBean() {
		return this.beanProperty().get();
	}

	public final void setBean(final Bean bean) {
		this.beanProperty().set(bean);
	}

	public final ListProperty<Bean> beansProperty() {
		return this.beans;
	}

	public final ObservableList<Bean> getBeans() {
		return this.beansProperty().get();
	}

	public final void setBeans(final ObservableList<Bean> beans) {
		this.beansProperty().set(beans);
	}

}
