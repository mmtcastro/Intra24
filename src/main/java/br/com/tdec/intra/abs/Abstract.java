package br.com.tdec.intra.abs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Abstract {

	protected static final Logger logger = LoggerFactory.getLogger(Abstract.class);

	public static void print(Object object) {
		System.out.println(object);
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * Retorna o logger associado à classe concreta.
	 */
	protected Logger getLogger() {
		return LoggerFactory.getLogger(this.getClass());
	}

	protected static Object newInstance(Class<?> type) {
		try {
			var c = type.getDeclaredConstructor();
			c.setAccessible(true);
			return c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static Field getFieldByName(Class<?> c, String name) {
		for (Class<?> k = c; k != null; k = k.getSuperclass()) {
			for (Field f : k.getDeclaredFields()) {
				if (f.getName().equals(name))
					return f;
			}
		}
		return null;
	}

	/**
	 * Utilizado nas conversoes de Multivalue para Listas em Service
	 * 
	 * @param model
	 * @param field
	 * @param items
	 * @param pluralHint
	 */
	protected static void assignListToField(Object model, Field field, List<?> items, String pluralHint) {
		try {
			field.setAccessible(true);
			Class<?> t = field.getType();

			// Caso 1: campo é uma Collection/List -> set direto
			if (java.util.Collection.class.isAssignableFrom(t)) {
				field.set(model, items);
				return;
			}

			// [NOVO] Tenta usar o wrapper JÁ EXISTENTE no campo
			Object existing = field.get(model);
			if (existing != null) {
				if (tryFillWrapper(existing, items, pluralHint)) {
					return; // sucesso preenchendo o existente
				}
			}

			// Caso 2: construtor Wrapper(List)
			for (var c : t.getDeclaredConstructors()) {
				var ps = c.getParameterTypes();
				if (ps.length == 1 && java.util.List.class.isAssignableFrom(ps[0])) {
					c.setAccessible(true);
					try {
						field.set(model, c.newInstance(items));
						return;
					} catch (Exception ignore) {
					}
				}
			}

			// Caso 3: instancia NOVO wrapper e tenta setters/getters/campo interno
			Object wrapper = t.getDeclaredConstructor().newInstance();
			if (tryFillWrapper(wrapper, items, pluralHint)) {
				field.set(model, wrapper);
				return;
			}

			throw new IllegalStateException("Não foi possível injetar lista no campo " + field.getName());

		} catch (Exception e) {
			throw new RuntimeException("Falha ao setar campo " + field.getName(), e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static boolean tryFillWrapper(Object wrapper, List<?> items, String pluralHint) {
		Class<?> t = wrapper.getClass();
		String cap = Character.toUpperCase(pluralHint.charAt(0)) + pluralHint.substring(1);

		// 1) setters: substitui direto
		for (String m : new String[] { "setLista", "setItems", "set" + cap }) {
			try {
				t.getMethod(m, java.util.List.class).invoke(wrapper, items);
				return true;
			} catch (NoSuchMethodException ignore) {
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 2) getters: **clear + addAll** (idempotente)
		for (String g : new String[] { "getLista", "getItems", "get" + cap }) {
			try {
				Object col = t.getMethod(g).invoke(wrapper);
				if (col instanceof java.util.Collection c) {
					c.clear(); // <-- limpa antes
					c.addAll(items); // <-- substitui
					return true;
				}
			} catch (NoSuchMethodException ignore) {
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 3) campo interno "lista" ou "<plural>" (set direto)
		for (String fn : new String[] { "lista", pluralHint }) {
			try {
				Field f = getFieldByNameDeep(t, fn);
				if (f != null && java.util.List.class.isAssignableFrom(f.getType())) {
					f.setAccessible(true);
					f.set(wrapper, items);
					return true;
				}
			} catch (Exception e) {
				throw new RuntimeException("Erro setando campo '" + fn + "'", e);
			}
		}
		return false;
	}

	protected static Field getFieldByNameDeep(Class<?> clazz, String fieldName) {
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			try {
				return c.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
			}
		}
		return null;
	}

	// Descobre o tipo da linha T de um campo AbstractModelListaMultivalue<T>
	@SuppressWarnings("unused")
	protected Class<?> resolveRowTypeFromWrapperField(Field wrapperField) {
		Type g = wrapperField.getGenericType();
		if (g instanceof ParameterizedType pt) {
			Type arg = pt.getActualTypeArguments()[0];
			if (arg instanceof Class<?> c)
				return c;
			if (arg instanceof ParameterizedType pt2 && pt2.getRawType() instanceof Class<?> c2)
				return c2;
		}
		throw new IllegalStateException("Não foi possível descobrir o tipo de linha de " + wrapperField);
	}

	// Resolve o prefixo: @MultivaluePrefix vence; senão, nome da classe da linha em
	// lower case
	@SuppressWarnings("unused")
	protected String resolvePrefix(Class<?> rowType) {
		return Utils.addPlurais(rowType.getSimpleName().toLowerCase());
	}

}