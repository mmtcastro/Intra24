package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class AbstractModelLista<E extends AbstractModelDoc> extends AbstractModel
		implements Iterable<AbstractModelDoc> {

	protected List<E> lista;

	public AbstractModelLista() { // por que será que isto nao funciona? Sera que só roda o constructor do pai
		createLista(); // cria lista e hashMap filtros
	}

	@SuppressWarnings("unchecked")
	public void createLista() {
		try {
			Class<?> cl = Class.forName("java.util.ArrayList");
			lista = (List<E>) cl.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			handleException("Erro - AbstractModelLista - inicializando. Nao achou a classe", e);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			handleException("Erro - AbstractModelLista - erro instanciando a lista", e);
		}
	}

	// protected abstract String getListaImplementationClassName(); eh assim que se
	// faz!

	private void handleException(String message, Exception e) {
		print(message);
		e.printStackTrace();
	}
}
