package br.com.tdec.intra;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ModelFactory<T> {

	public T createModel(Class<T> modelClass) {
		/**
		 * Cria um AbstractModelDoc sem argumentos
		 * 
		 * @param <T>
		 * @param modelClass
		 * @return
		 */
		T model = null;
		try {
			// Accessible constructor might be required
			Constructor<T> constructor = modelClass.getDeclaredConstructor();
			constructor.setAccessible(true); // Make private constructor accessible if needed
			model = constructor.newInstance();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
}