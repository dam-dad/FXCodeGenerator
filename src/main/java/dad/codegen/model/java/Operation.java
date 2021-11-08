package dad.codegen.model.java;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSeeAlso;
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
@XmlSeeAlso(value = { Constructor.class })
public class Operation extends NamedElement {
	private ObjectProperty<Visibility> visibility;
	private BooleanProperty abstractOperation;
	private ObjectProperty<ReturnType> returnType;
	private ListProperty<Parameter> parameters;
	private ListProperty<String> implementation;

	public Operation() {
		super();
		visibility = new SimpleObjectProperty<>(this, "visibility", Visibility.PUBLIC);
		abstractOperation = new SimpleBooleanProperty(this, "abstractOperation", false);
		returnType = new SimpleObjectProperty<>(this, "returnType");
		parameters = new SimpleListProperty<>(this, "parameters", FXCollections.observableArrayList());
		implementation = new SimpleListProperty<>(this, "implementation", FXCollections.observableArrayList());
	}

	public ObjectProperty<Visibility> visibilityProperty() {
		return this.visibility;
	}

	@XmlAttribute
	public Visibility getVisibility() {
		return this.visibilityProperty().get();
	}

	public void setVisibility(final Visibility visibility) {
		this.visibilityProperty().set(visibility);
	}

	public ObjectProperty<ReturnType> returnTypeProperty() {
		return this.returnType;
	}

	@XmlAttribute
	@XmlIDREF
	public ReturnType getReturnType() {
		return this.returnTypeProperty().get();
	}

	public void setReturnType(final ReturnType returnType) {
		this.returnTypeProperty().set(returnType);
	}

	public ListProperty<Parameter> parametersProperty() {
		return this.parameters;
	}

	@XmlElement
	public ObservableList<Parameter> getParameters() {
		return this.parametersProperty().get();
	}

	public void setParameters(final ObservableList<Parameter> parameters) {
		this.parametersProperty().set(parameters);
	}
	
	public ListProperty<String> implementationProperty() {
		return this.implementation;
	}

	@XmlElement
	public ObservableList<String> getImplementation() {
		return this.implementationProperty().get();
	}

	public void setImplementation(final ObservableList<String> implementation) {
		this.implementationProperty().set(implementation);
	}
	

	public BooleanProperty abstractOperationProperty() {
		return this.abstractOperation;
	}
	
	@XmlAttribute
	public boolean isAbstractOperation() {
		return this.abstractOperationProperty().get();
	}
	

	public void setAbstractOperation(final boolean abstractOperation) {
		this.abstractOperationProperty().set(abstractOperation);
	}
	
	public String toString() {
		return getHeader();
	}
	
	private String getHeader() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getVisibility() != null? getVisibility().toString().toLowerCase() + " " : "");
		buffer.append(isAbstractOperation() ? "abstract " : "");
		buffer.append(getReturnType() != null? getReturnType().asJavaCode() + " " : "void ");
		buffer.append(getName());
		buffer.append("(");
		for (int i = 0; i < getParameters().size(); i++) {
			buffer.append(getParameters().get(i).asJavaCode());
			if (i + 1 < getParameters().size()) buffer.append(", ");
		}
		buffer.append(")");
		return buffer.toString();
	}
	
	public String asJavaCode(String tabs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tabs); 
		buffer.append(getHeader());
		if (!isAbstractOperation()) { 
			buffer.append(" {\n");
			for (String impl : getImplementation()) {
				buffer.append(tabs + "\t" + impl + ";\n");
			}
			buffer.append(tabs + "}\n");
		} else {
			buffer.append(";\n");
		}
		return buffer.toString();
	}

}
