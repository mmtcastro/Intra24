package br.com.tdec.intra.abs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.tdec.intra.utils.Utils;

public abstract class AbstractModel extends Abstract {

	/**
	 * Retorna todos os campos da classe e de suas superclasses
	 * 
	 * @return
	 */
	public List<Field> getAllModelFields() {
		List<Field> fields = new ArrayList<>();
		Class<?> currentClass = this.getClass();
		while (currentClass != null) {
			fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
			currentClass = currentClass.getSuperclass();
		}
		return fields;
	}

	public List<String> getAllModelFieldNames() {
		List<String> fields = new ArrayList<>();
		Class<?> currentClass = this.getClass();
		while (currentClass != null) {
			for (Field field : currentClass.getDeclaredFields()) {
				fields.add(field.getName());
			}
			currentClass = currentClass.getSuperclass();
		}
		return fields;
	}

	/**
	 * Retorna todos nomes dos campos da classe e de suas superclasses em properCase
	 * para uso do ActionPack que é case sensitive
	 * 
	 * @return
	 */
	public List<String> getAllModelFieldNamesProperCase() {
		List<String> fields = new ArrayList<>();
		Class<?> currentClass = this.getClass();
		while (currentClass != null) {
			for (Field field : currentClass.getDeclaredFields()) {
				fields.add((Utils.toProperCase(field.getName())));
			}
			currentClass = currentClass.getSuperclass();
		}
		return fields;
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?> parameterTypes)
			throws NoSuchMethodException {
		try {
			// Try to get the method from the current class
			return clazz.getMethod(methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			// If not found, try the superclass
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null) {
				return getMethod(superClass, methodName, parameterTypes);
			} else {
				// If no more superclasses, throw the exception
				throw e;
			}
		}
	}

	/**
	 * Busca todos os metodos da classe e das superclasses
	 */

	public static List<Method> findAllMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<>();
		for (Method method : clazz.getDeclaredMethods()) {
			methods.add(method);
		}
		if (clazz.getSuperclass() != null) {
			methods.addAll(findAllMethods(clazz.getSuperclass()));
		}
		return methods;
	}

	/**
	 * Busca o metodo pelo nome, buscando na classe e superclasses
	 * 
	 * @param methodName
	 * @return
	 */
	public Method findMethod(String methodName) {
		Method method = null;
		try {
			method = getMethod(this.getClass(), methodName, null);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return method;
	}
}
