package dad.codegen.model.javafx;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Type {
	INTEGER	("Integer"),
	LONG	("Long"),
	FLOAT	("Float"),
	DOUBLE	("Double"),
	BOOLEAN	("Boolean"),
	STRING	("String"),
	DATE	("Date"),
	LIST	("List"),	
	SET		("Set"),	
	OBJECT	("Object");
	
    private final String name;       

    private Type(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
