package dad.codegen.model.javafx;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlType
public class Bean {
	private StringProperty name;
	private ObjectProperty<Bean> parent;
	private ListProperty<Property> properties;

	public Bean() {
		name = new SimpleStringProperty(this, "name");
		parent = new SimpleObjectProperty<>(this, "parent");
		properties = new SimpleListProperty<>(this, "properties", FXCollections.observableArrayList());
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	@XmlID
	@XmlAttribute
	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(final String name) {
		this.nameProperty().set(name);
	}

	public ObjectProperty<Bean> parentProperty() {
		return this.parent;
	}

	@XmlIDREF
	@XmlAttribute
	public Bean getParent() {
		return this.parentProperty().get();
	}

	public void setParent(final Bean parent) {
		this.parentProperty().set(parent);
	}

	public ListProperty<Property> propertiesProperty() {
		return this.properties;
	}

	@XmlElement(name = "property")
	public ObservableList<Property> getProperties() {
		return this.propertiesProperty().get();
	}

	public void setProperties(final ObservableList<Property> properties) {
		this.propertiesProperty().set(properties);
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
