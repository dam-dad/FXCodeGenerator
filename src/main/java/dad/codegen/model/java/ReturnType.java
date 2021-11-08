package dad.codegen.model.java;

import jakarta.xml.bind.annotation.XmlType;

@XmlType
public class ReturnType extends Attribute {
	
	public ReturnType() {
		super();
	}
	
	public ReturnType(Class type, Class ... generics) {
		this();
		setType(type);
		getGenerics().addAll(generics);
	}

	public String asJavaCode() {
		StringBuffer buffer = new StringBuffer();
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
		return buffer.toString();
	}
	
}
