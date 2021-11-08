package dad.codegen.model.java;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlType
public class Parameter extends NamedElement {
	private ObjectProperty<Class> type;
	private BooleanProperty finalValue;
	private ListProperty<Class> generics;

	public Parameter() {
		super();
		type = new SimpleObjectProperty<>(this, "type");
		finalValue = new SimpleBooleanProperty(this, "finalValue", false);
		generics = new SimpleListProperty<>(this, "generics", FXCollections.observableArrayList());		
	}
	
	public Parameter(Class type, Class ... generics) {
		this();
		setType(type);
		getGenerics().addAll(generics);
	}
	
	public Parameter(Class type, String name) {
		this();
		setType(type);
		setName(name);
	}

	public ObjectProperty<Class> typeProperty() {
		return this.type;
	}

	@XmlIDREF
	@XmlAttribute
	public Class getType() {
		return this.typeProperty().get();
	}

	public void setType(final Class type) {
		this.typeProperty().set(type);
	}

	public BooleanProperty finalValueProperty() {
		return this.finalValue;
	}

	@XmlAttribute
	public boolean isFinalValue() {
		return this.finalValueProperty().get();
	}

	public void setFinalValue(final boolean finalValue) {
		this.finalValueProperty().set(finalValue);
	}
	
	public ListProperty<Class> genericsProperty() {
		return this.generics;
	}

	public ObservableList<Class> getGenerics() {
		return this.genericsProperty().get();
	}

	public void setGenerics(final ObservableList<Class> generics) {
		this.genericsProperty().set(generics);
	}
	
	@Override
	public String toString() {
		return getName() + ":" + getType().getName();
	}
	
	public String asJavaCode() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(isFinalValue()? "final " : "");
		buffer.append(getType().getName());
		if (!getGenerics().isEmpty()) {
			buffer.append("<");	
			for (int i = 0; i < getGenerics().size(); i++) {
				Class gc = getGenerics().get(i);
				buffer.append(gc.getName());
				if (i + 1 < getGenerics().size()) buffer.append(", ");
			}
			buffer.append(">");			
		}
		buffer.append(" " + getName());
		return buffer.toString();
	}

}
