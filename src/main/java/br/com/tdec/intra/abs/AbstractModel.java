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

	/**
	 * Retorna o getCodigo() dentro de um DOC empresa, por exemplo.
	 */
	public Method getGetMethod(String campo) {
		return getMethod(campo, "get");
	}

	public Method getSetMethod(String campo) {
		return getMethod(campo, "set");

	}

	/**
	 * procura por um método com algum prefixo (get ou set, por exemplo) dentro de
	 * todos os metodos da classe
	 * 
	 * @param campo
	 * @param prefix
	 * @return
	 */
	public Method getMethod(String campo, String prefix) {
		Method method = null;

		Method[] methods = this.getClass().getMethods();

		String methodName = prefix + Character.toUpperCase(campo.charAt(0)) + campo.substring(1, campo.length());

		for (int i = 0; i < methods.length; i++) {

			if (methodName.equals(methods[i].getName())) {
				method = methods[i];
				break;
			}
		}
		return method;
	}

	public void setField(String fieldName, Object newValue) {
		try {
			Class<?> classe = this.getClass();
			Field declaredField = null;

			// Busca o campo na hierarquia de classes
			while (classe != null) {
				try {
					declaredField = classe.getDeclaredField(fieldName);
					break;
				} catch (NoSuchFieldException e) {
					classe = classe.getSuperclass();
				}
			}

			if (declaredField == null) {
				throw new NoSuchFieldException("Campo não encontrado: " + fieldName);
			}

			// Define a acessibilidade e ajusta o valor
			boolean accessible = declaredField.canAccess(this);
			declaredField.setAccessible(true);
			declaredField.set(this, newValue);
			declaredField.setAccessible(accessible);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Campo não encontrado: " + fieldName, e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Acesso ilegal ao campo: " + fieldName, e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Valor incompatível para o campo: " + fieldName + " - Valor: " + newValue, e);
		} catch (SecurityException e) {
			throw new RuntimeException("Erro de segurança ao acessar o campo: " + fieldName, e);
		}
	}

	protected void setFieldValue(Class<?> clazz, String fieldName, Object value) {
		try {
			Field field = getFieldByName(clazz, fieldName);
			if (field != null) {
				field.setAccessible(true);
				field.set(this, value);
			}
		} catch (Exception e) {
			System.err.println("Erro ao definir valor para o campo " + fieldName + ": " + e.getMessage());
			e.printStackTrace();
		}
	}

}