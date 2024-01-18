package br.com.tdec.intra.abs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.tdec.intra.utils.Utils;
import lombok.Data;

@Data
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
	public Map<String, Class<?>> getAllModelFieldNamesProperCase() {
		Map<String, Class<?>> fields = new HashMap<>();
		Class<?> currentClass = this.getClass();
		while (currentClass != null) {
			for (Field field : currentClass.getDeclaredFields()) {
				fields.put(Utils.toProperCase(field.getName()), field.getType());
			}
			currentClass = currentClass.getSuperclass();
		}
		return fields;
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
		methods.stream().forEach(m -> System.out.println(m.getName()));
		return methods;
	}

	public Field getField(String fieldName) {
		Field ret = null;
		try {
			List<Field> fields = this.getAllModelFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getName().equalsIgnoreCase(fieldName)) {
					return field;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
