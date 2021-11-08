package dad.codegen.model.java;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

@XmlType
public class Class extends NamedElement {
	private StringProperty packageName;
	private BooleanProperty abstractClass;
	private ObjectProperty<Class> parent;
	private ListProperty<Attribute> attributes;
	private ListProperty<Operation> operations;
	private SetProperty<Class> imports;

	public Class() {
		super();
		packageName = new SimpleStringProperty(this, "packageName");
		abstractClass = new SimpleBooleanProperty(this, "abstractClass", false);
		parent = new SimpleObjectProperty<>(this, "parent");
		attributes = new SimpleListProperty<>(this, "attributes", FXCollections.observableArrayList());
		operations = new SimpleListProperty<>(this, "operations", FXCollections.observableArrayList());
		imports = new SimpleSetProperty<>(this, "imports", FXCollections.observableSet(new HashSet<>()));
	}
	
	public Class(String packageName, String name) {
		this();
		setPackageName(packageName);
		setName(name);
	}
	
	@XmlID
	@XmlAttribute
	public String getFullName() {
		return toString();
	}

	public StringProperty packageNameProperty() {
		return this.packageName;
	}

	@XmlAttribute
	public String getPackageName() {
		return this.packageNameProperty().get();
	}

	public void setPackageName(final String packageName) {
		this.packageNameProperty().set(packageName);
	}

	public BooleanProperty abstractClassProperty() {
		return this.abstractClass;
	}

	@XmlAttribute
	public boolean isAbstractClass() {
		return this.abstractClassProperty().get();
	}

	public void setAbstractClass(final boolean abstractClass) {
		this.abstractClassProperty().set(abstractClass);
	}

	public ObjectProperty<Class> parentProperty() {
		return this.parent;
	}

	@XmlIDREF
	@XmlElement
	public Class getParent() {
		return this.parentProperty().get();
	}

	public void setParent(final Class parent) {
		this.parentProperty().set(parent);
	}

	public ListProperty<Attribute> attributesProperty() {
		return this.attributes;
	}

	@XmlElement(name = "attribute")
	@XmlElementWrapper
	public ObservableList<Attribute> getAttributes() {
		return this.attributesProperty().get();
	}

	public void setAttributes(final ObservableList<Attribute> attributes) {
		this.attributesProperty().set(attributes);
	}

	public ListProperty<Operation> operationsProperty() {
		return this.operations;
	}

	@XmlElement(name = "operation")
	@XmlElementWrapper
	public ObservableList<Operation> getOperations() {
		return this.operationsProperty().get();
	}

	public void setOperations(final ObservableList<Operation> operations) {
		this.operationsProperty().set(operations);
	}
	
	public SetProperty<Class> importsProperty() {
		return this.imports;
	}

	@XmlIDREF
	@XmlElement(name = "import")
	@XmlElementWrapper(name = "imports")
	public ObservableSet<Class> getImports() {
		return this.importsProperty().get();
	}

	public void setImports(final ObservableSet<Class> imports) {
		this.importsProperty().set(imports);
	}
	
	@Override
	public String toString() {
		return getPackageName() + "." + getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Class) {
			Class c = (Class) obj;
			return getName().equals(c.getName()) && getPackageName().equals(c.getPackageName());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return getFullName().hashCode();
	}
	
	public void searchImports() {
		for (Attribute a : getAttributes()) {
			getImports().add(a.getType());
		}
		for (Operation o : getOperations()) {
			if (o.getReturnType() != null) {
				getImports().add(o.getReturnType().getType());
			}
			for (Parameter p : o.getParameters()) {
				getImports().add(p.getType());
			}
		}
	}
	
	public String asJavaCode() {
		searchImports();
		StringBuffer buffer = new StringBuffer();
		if (getPackageName() != null && !getPackageName().isEmpty()) { 
			buffer.append("package " + getPackageName() + ";\n");
			buffer.append("\n");
		}
		List<Class> sortedImports = imports.stream().filter(p -> !p.getPackageName().equals("java.lang")).sorted((c1, c2) -> c1.getFullName().compareTo(c2.getFullName())).collect(Collectors.toList());
		for (Class i : sortedImports) {
			buffer.append("import " + i + ";\n");
		}
		buffer.append("\n");
		buffer.append("public ");
		buffer.append(isAbstractClass() ? "abstract " : "");
		buffer.append("class ");
		buffer.append(getName());
		buffer.append(getParent() != null ? " extends " + getParent().getName() : "");
		buffer.append(" {\n");
		for (Attribute a : getAttributes()) {
			buffer.append(a.asJavaCode("\t") + "\n");
		}
		buffer.append("\n");
		for (Operation o : getOperations()) {
			if (o instanceof Constructor) o.setName(getName());
			buffer.append(o.asJavaCode("\t"));		
			buffer.append("\n");
		}
		buffer.append("}\n");
		return buffer.toString();
	}

}
