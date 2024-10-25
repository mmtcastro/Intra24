package br.com.tdec.intra.abs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractModel extends Abstract {

	/**
	 * Retorna todos os campos da classe e de suas superclasses
	 *
	 * @return
	 */
	@JsonIgnore
	public List<Field> getAllModelFields() {
		List<Field> fields = new ArrayList<>();
		Class<?> currentClass = this.getClass();
		while (currentClass != null) {
			fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
			currentClass = currentClass.getSuperclass();
		}
		return fields;
	}

	/**
	 * Retorna os nomes de todos os campos da classe e de suas superclasses
	 */
	@JsonIgnore
	public List<String> getAllModelFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		Class<?> currentClass = this.getClass();
		while (currentClass != null) {
			for (Field field : currentClass.getDeclaredFields()) {
				fieldNames.add(field.getName());
			}
			currentClass = currentClass.getSuperclass();
		}
		return fieldNames;
	}

	/**
	 * Retorna os nomes dos campos da classe e de suas superclasses em properCase
	 * para uso do ActionPack que é case sensitive
	 *
	 * @return
	 */
	@JsonIgnore
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
	 * Busca todos os métodos da classe e das superclasses
	 */
	@JsonIgnore
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
	 * Busca um campo específico por nome, independentemente da visibilidade
	 */
	public Field getField(String fieldName) {
		try {
			for (Field field : this.getAllModelFields()) {
				if (field.getName().equalsIgnoreCase(fieldName)) {
					field.setAccessible(true); // Se necessário, tornar o campo acessível
					return field;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // Considere utilizar um logger ao invés de imprimir o stacktrace
		}
		return null;
	}
}