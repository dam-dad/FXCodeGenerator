package dad.codegen.model.java;


import jakarta.xml.bind.annotation.XmlAttribute;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Attribute extends Parameter {
	private ObjectProperty<Visibility> visibility;

	public Attribute() {
		super();
		visibility = new SimpleObjectProperty<>(this, "visibility", Visibility.PRIVATE);
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

	public String asJavaCode(String tabs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tabs);
		buffer.append(getVisibility() != null ? getVisibility().toString().toLowerCase() + " " : "");
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
		buffer.append(" ");
		buffer.append(getName() + ";");
		return buffer.toString();
	}
	
}
