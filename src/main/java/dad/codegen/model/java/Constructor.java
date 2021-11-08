package dad.codegen.model.java;

import jakarta.xml.bind.annotation.XmlType;

@XmlType
public class Constructor extends Operation {
	
	public Constructor() {
		super();
		setVisibility(Visibility.PUBLIC);
	}

	public String asJavaCode(String tabs) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tabs); 
		buffer.append(getVisibility() != null? getVisibility().toString().toLowerCase() + " " : " ");
		buffer.append(getName() + "(");
		for (int i = 0; i < getParameters().size(); i++) {
			buffer.append(getParameters().get(i).asJavaCode());
			if (i + 1 < getParameters().size()) buffer.append(", ");
		}
		buffer.append(") {\n");
		for (String impl : getImplementation()) {
			buffer.append(tabs + "\t" + impl + ";\n");
		}
		buffer.append(tabs + "}\n");
		return buffer.toString();
	}
	
}
