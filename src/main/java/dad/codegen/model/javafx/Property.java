package dad.codegen.model.javafx;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlType
public class Property {
	private StringProperty name;
	private BooleanProperty readOnly;
	private ObjectProperty<Type> type;
	private ObjectProperty<Bean> generic;

	public Property() {
		name = new SimpleStringProperty(this, "name");
		readOnly = new SimpleBooleanProperty(this, "readOnly");
		type = new SimpleObjectProperty<>(this, "type");
		generic = new SimpleObjectProperty<>(this, "generic");
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	@XmlAttribute
	public String getName() {
		return this.nameProperty().get();
	}

	public void setName(final String name) {
		this.nameProperty().set(name);
	}

	public BooleanProperty readOnlyProperty() {
		return this.readOnly;
	}

	@XmlAttribute
	public boolean isReadOnly() {
		return this.readOnlyProperty().get();
	}

	public void setReadOnly(final boolean readOnly) {
		this.readOnlyProperty().set(readOnly);
	}

	public ObjectProperty<Type> typeProperty() {
		return this.type;
	}

	@XmlAttribute
	public Type getType() {
		return this.typeProperty().get();
	}

	public void setType(final Type type) {
		this.typeProperty().set(type);
	}

	public ObjectProperty<Bean> genericProperty() {
		return this.generic;
	}

	@XmlIDREF
	@XmlAttribute
	public Bean getGeneric() {
		return this.genericProperty().get();
	}

	public void setGeneric(final Bean generic) {
		this.genericProperty().set(generic);
	}

}
