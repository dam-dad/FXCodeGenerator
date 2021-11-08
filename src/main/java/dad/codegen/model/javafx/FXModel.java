package dad.codegen.model.javafx;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import dad.codegen.model.java.Attribute;
import dad.codegen.model.java.Class;
import dad.codegen.model.java.Constructor;
import dad.codegen.model.java.Operation;
import dad.codegen.model.java.Parameter;
import dad.codegen.model.java.Primitives;
import dad.codegen.model.java.Repository;
import dad.codegen.model.java.ReturnType;
import dad.codegen.model.java.Visibility;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

@XmlRootElement
@XmlType
public class FXModel {
	private StringProperty packageName;
	private ListProperty<Bean> beans;

	public FXModel() {
		packageName = new SimpleStringProperty(this, "packageName", "");
		beans = new SimpleListProperty<>(this, "beans", FXCollections.observableArrayList((Bean b) -> new Observable[] { b.nameProperty() }));
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

	public ListProperty<Bean> beansProperty() {
		return this.beans;
	}

	@XmlElement(name = "bean")
	public ObservableList<Bean> getBeans() {
		return this.beansProperty().get();
	}

	public void setBeans(final ObservableList<Bean> beans) {
		this.beansProperty().set(beans);
	}
	
	// ---------------------------------------------------
	// Persistencia del modelo FX en XML
	
	public void save(File target) throws Exception {
		JAXBContext context = JAXBContext.newInstance(FXModel.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, target);
	}

	public static FXModel load(File source) throws Exception {
		JAXBContext context = JAXBContext.newInstance(FXModel.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (FXModel) unmarshaller.unmarshal(source);
	}
	
	// ---------------------------------------------------
	// Transformación del modelo FX en un modelo Java para generar el código
	
	private Repository transform() {
		Map<Pair<String,String>, Class> classesMap = modelToClassesMap();
		Repository repo = new Repository();
		for (Bean bean : getBeans()) {
			Class c = beanToClass(bean, classesMap);
			repo.getClasses().add(c);
		}
 		return repo;
	}
	
	private Map<Pair<String,String>, Class> modelToClassesMap() {
		Map<Pair<String,String>, Class> classesMap = new HashMap<>(); 
		for (Bean bean : getBeans()) {
			Class c = new Class(getPackageName(), bean.getName());
			classesMap.put(new Pair<String, String>(getPackageName(), bean.getName()), c);
		}
		return classesMap;
	}

	private Class beanToClass(Bean bean, Map<Pair<String, String>, Class> classesMap) {
		Class c = classesMap.get(new Pair<String, String>(getPackageName(), bean.getName()));
		
		// si tiene padre, buscamos la clase que corresponde al bean padre
		if (bean.getParent() != null) {
			c.setParent(classesMap.get(new Pair<String, String>(getPackageName(), bean.getParent().getName())));
		}
		
		Operation oConstructor = new Constructor();
		oConstructor.setName(bean.getName());
		if (bean.getParent() != null) {
			oConstructor.getImplementation().add("super()");
		}
		c.getOperations().add(oConstructor);
		
		// recorremos tods las propiedades
		for (Property p : bean.getProperties()) {
			
			// localiza la clase para establecer el gen�rico
			Class generic = null;
			if (p.getType().equals(Type.DATE)) {
				generic = Primitives.DATE;
			} else if (p.getGeneric() != null) {
				generic = classesMap.get(new Pair<String, String>(getPackageName(), p.getGeneric().getName()));
			}
			
			// ajusta el tipo en caso de que sea Date = Object
			String type = Type.DATE.equals(p.getType()) ? "Object" : p.getType().toString();
			
			// añade la instanciación de la propiedad en el método constructor
			String instanceProperty = p.getName();
			instanceProperty += p.isReadOnly() ? " = new ReadOnly" + type + "Wrapper" : " = new Simple" + type + "Property";
			instanceProperty += (generic != null ? "<>" : "") + "(this, \"" + p.getName() + "\"";
			if (Type.LIST.equals(p.getType())) {
				c.getImports().add(new Class("javafx.collections", "FXCollections"));
				instanceProperty += ", FXCollections.observableArrayList()";
			} else if (Type.SET.equals(p.getType())) {
				c.getImports().add(new Class("javafx.collections", "FXCollections"));
				c.getImports().add(new Class("java.util", "HashSet"));
				instanceProperty += ", FXCollections.observableSet(new HashSet<>())";
			} 
			instanceProperty += ")";
			oConstructor.getImplementation().add(instanceProperty);
			
			// crea y añade a la clase el atributo correspondiente a la propiedad
			Attribute a = new Attribute();
			a.setName(p.getName());
			a.setVisibility(Visibility.PRIVATE);
			if (!p.isReadOnly()) {
				a.setType(new Class("javafx.beans.property", type + "Property"));
				if (generic != null) a.getGenerics().add(generic);
				c.getImports().add(new Class("javafx.beans.property", "Simple" + type + "Property"));
			} else {
				a.setType(new Class("javafx.beans.property", "ReadOnly" + type + "Wrapper"));
				if (generic != null) a.getGenerics().add(generic);
			}
			c.getAttributes().add(a);
			
			// crea y añade el método getter de la propiedad
			Operation oGetter = new Operation();
			oGetter.setName("get" + StringUtils.capitalize(p.getName()));
			switch (p.getType()) {
			case INTEGER: 	oGetter.setReturnType(new ReturnType(Primitives.INTEGER)); break; 
			case LONG: 		oGetter.setReturnType(new ReturnType(Primitives.LONG)); break; 
			case FLOAT: 	oGetter.setReturnType(new ReturnType(Primitives.FLOAT)); break; 
			case DOUBLE: 	oGetter.setReturnType(new ReturnType(Primitives.DOUBLE)); break; 
			case BOOLEAN: 	oGetter.setReturnType(new ReturnType(Primitives.BOOLEAN)); break; 
			case STRING: 	oGetter.setReturnType(new ReturnType(Primitives.STRING)); break;
			case DATE: 		oGetter.setReturnType(new ReturnType(Primitives.DATE)); break;
			case LIST: 		oGetter.setReturnType(new ReturnType(new Class("javafx.collections", "ObservableList"), generic)); break;
			case SET: 		oGetter.setReturnType(new ReturnType(new Class("javafx.collections", "ObservableSet"), generic)); break;
			case OBJECT: 	oGetter.setReturnType(new ReturnType(generic)); break;
			default: ;
			}
			oGetter.getImplementation().add("return this." + p.getName() + "Property().get()");
			c.getOperations().add(oGetter);
			
			// si la propiedad NO es de s�lo lectura, crea y añade el método "setter" de la propiedad 
			if (!p.isReadOnly()) {
				Operation oSetter = new Operation();
				oSetter.setName("set" + StringUtils.capitalize(p.getName()));
				oSetter.getParameters().add(new Parameter());
				oSetter.getParameters().get(0).setName(p.getName());
				oSetter.getParameters().get(0).setFinalValue(true);
				switch (p.getType()) {
				case INTEGER: 	oSetter.getParameters().get(0).setType(Primitives.INTEGER); break; 
				case LONG: 		oSetter.getParameters().get(0).setType(Primitives.LONG); break; 
				case FLOAT: 	oSetter.getParameters().get(0).setType(Primitives.FLOAT); break; 
				case DOUBLE: 	oSetter.getParameters().get(0).setType(Primitives.DOUBLE); break; 
				case BOOLEAN: 	oSetter.getParameters().get(0).setType(Primitives.BOOLEAN); break; 
				case STRING: 	oSetter.getParameters().get(0).setType(Primitives.STRING); break;
				case DATE: 		oSetter.getParameters().get(0).setType(Primitives.DATE); break;
				case LIST: 		{
					oSetter.getParameters().get(0).setType(new Class("javafx.collections", "ObservableList"));
					oSetter.getParameters().get(0).getGenerics().add(generic);
				} break;
				case SET: 		{
					oSetter.getParameters().get(0).setType(new Class("javafx.collections", "ObservableSet"));
					oSetter.getParameters().get(0).getGenerics().add(generic);						
				} break;
				case OBJECT: 	oSetter.getParameters().get(0).setType(generic); break;
				default: ;
				}
				oSetter.getImplementation().add("this." + p.getName() + "Property().set(" + p.getName() + ")");
				c.getOperations().add(oSetter);
			}
			
			// crea y añade el método "property"
			Operation oProperty = new Operation();
			oProperty.setName(p.getName() + "Property");
			Class returnClass = new Class("javafx.beans.property", (p.isReadOnly() ? "ReadOnly" : "") + type + "Property");
			if (generic != null)
				oProperty.setReturnType(new ReturnType(returnClass, generic));
			else
				oProperty.setReturnType(new ReturnType(returnClass));
			oProperty.getImplementation().add("return this." + p.getName() + (p.isReadOnly() ? ".getReadOnlyProperty()" : ""));
			c.getOperations().add(oProperty);
			
		}
		
		return c;
	}
	
	public void generateCode(File path) throws IOException {
		if (path == null) throw new IllegalArgumentException("La ruta especificada no puede ser nula.");
		transform().generateCode(path);
	}
	
	public String generateBeanCode(Bean bean) {
		if (bean == null) throw new IllegalArgumentException("El bean especificado no puede ser nulo.");
		if (!getBeans().contains(bean)) throw new IllegalArgumentException("El bean '" + bean.getName() + "' no se encuentra en el modelo FX.");
		return beanToClass(bean, modelToClassesMap()).asJavaCode();
	}
	
}
