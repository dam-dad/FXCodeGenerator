package dad.codegen.model.java;

import jakarta.xml.bind.annotation.XmlAttribute;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NamedElement {
	private StringProperty name;

	public NamedElement() {
		name = new SimpleStringProperty(this, "name");
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

}
