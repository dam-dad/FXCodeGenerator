package dad.codegen.model.java;

public class Primitives {
	
	public static final Class STRING = new Class("java.lang", "String"); 
	public static final Class INTEGER = new Class("java.lang", "Integer"); 
	public static final Class LONG = new Class("java.lang", "Long"); 
	public static final Class FLOAT = new Class("java.lang", "Float"); 
	public static final Class DOUBLE = new Class("java.lang", "Double"); 
	public static final Class BOOLEAN = new Class("java.lang", "Boolean"); 
	public static final Class DATE = new Class("java.time", "LocalDate"); 
	public static final Class LIST = new Class("java.util", "List"); 
	public static final Class SET = new Class("java.util", "Set"); 
	
	public static final Class [] ALL = { STRING, INTEGER, LONG, FLOAT, DOUBLE, DATE, BOOLEAN, LIST, SET };

}
