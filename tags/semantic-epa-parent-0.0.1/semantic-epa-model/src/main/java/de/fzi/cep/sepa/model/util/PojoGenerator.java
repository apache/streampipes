package de.fzi.cep.sepa.model.util;

import java.util.List;

import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class PojoGenerator {

	public static Class generate(String className, List<EventProperty> eventProperties) throws NotFoundException,
			CannotCompileException {

		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.makeClass(className);

		for (EventProperty p : eventProperties) {
			String propertyName = p.getRuntimeName();
			if (p instanceof EventPropertyPrimitive)
			{
				EventPropertyPrimitive primitive = (EventPropertyPrimitive) p;
				cc.addField(new CtField(resolveCtClass(primitive.getRuntimeType()), propertyName, cc));
	
				// add getter
				cc.addMethod(generateGetter(cc, propertyName, ModelUtils.getPrimitiveClass(primitive.getRuntimeType())));
	
				// add setter
				cc.addMethod(generateSetter(cc, propertyName, ModelUtils.getPrimitiveClass(primitive.getRuntimeType())));
			}
			/*
			else if (p instanceof EventPropertyNested)
			{
				EventPropertyNested nested = (EventPropertyNested) p;
				CtClass subClass = pool.makeClass(StringUtils.capitalize(propertyName));
				cc.addField(new CtField(subClass, propertyName, cc));
	
				// add getter
				cc.addMethod(generateGetter(cc, propertyName, subClass.));
	
				// add setter
				cc.addMethod(generateSetter(cc, propertyName, ModelUtils.getPrimitiveClass(primitive.getPropertyType())));
			}
			*/
		}

		return cc.toClass();
	}

	private static CtMethod generateGetter(CtClass declaringClass, String fieldName, Class fieldClass)
			throws CannotCompileException {

		String getterName = "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);

		StringBuffer sb = new StringBuffer();
		sb.append("public ").append(fieldClass.getName()).append(" ")
				.append(getterName).append("(){").append("return this.")
				.append(fieldName).append(";").append("}");
		return CtMethod.make(sb.toString(), declaringClass);
	}

	private static CtMethod generateSetter(CtClass declaringClass, String fieldName, Class fieldClass)
			throws CannotCompileException {

		String setterName = "set" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);

		StringBuffer sb = new StringBuffer();
		sb.append("public void ").append(setterName).append("(")
				.append(fieldClass.getName()).append(" ").append(fieldName)
				.append(")").append("{").append("this.").append(fieldName)
				.append("=").append(fieldName).append(";").append("}");
		return CtMethod.make(sb.toString(), declaringClass);
	}

	private static CtClass resolveCtClass(String propertyType) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		return pool.get(ModelUtils.getPrimitiveClass(propertyType).getName());
	}
}
