package br.com.tdec.intra.abs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractModelListaMultivalue<E extends AbstractModelDocMultivalue> extends AbstractModel
		implements Iterable<AbstractModelDocMultivalue> {
	protected List<AbstractModelDocMultivalue> lista;

	public AbstractModelListaMultivalue() {

		createLista();
	}

	@SuppressWarnings("unchecked")
	public void createLista() {
		try {
			Class<?> cl = Class.forName("java.util.ArrayList");
			lista = (ArrayList<AbstractModelDocMultivalue>) cl.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * adiciona o item no final da lista (para o caso do sort nao funcionar
	 * 
	 */
	public void addBottom(AbstractModelDocMultivalue model) {
		if (model == null) {
			print("Warning - AbstracModelLista - add - (model==null) tentando adicionar um modelo que eh null");
			return;
		}
		if (model.getIdMulti() == null) {
			print("Warning - AbstracModelLista - add - (model.getId()==null) - Tenho que verificar por que quando uso o deleteModel tenho null aqui: "
					+ model.getClass());
			return;
		}
		try {
			lista.add(model);
			this.sort();
		} catch (Exception e) {
			print("Erro - AbstractModelLista - add - problema ao adicionar o AbstractModelDoc - " + model.getIdMulti());
			e.printStackTrace();
		}
	}

	/**
	 * adiciona o item no final da lista caso nao exista. Caso o ID já exista, ele
	 * troca o conteúdo pelo novo conteudo do Model
	 * 
	 */
	public void addTop(AbstractModelDocMultivalue model) {
		if (model == null) {
			print("Warning - AbstracModelListaMultivalue - add - (model==null) tentando adicionar um modelo que eh null");
			return;
		}
		if (model.getIdMulti() == null) {
			print("Warning - AbstracModelListaMultivalue - add - (model.getId()==null) - Tenho que verificar por que quando uso o deleteModel tenho null aqui: "
					+ model.getClass());
			return;
		}
		try {
			lista.add(0, model);
			this.sort();
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - add - problema ao adicionar o AbstractModelDoc - "
					+ model.getIdMulti());
			e.printStackTrace();
		}
	}

	public boolean contem(AbstractModelDocMultivalue model) {
		boolean ret = false;
		try {
			if (model == null) {
				return ret;
			}
			if (model.getIdMulti() == null) {
				return ret;
			}
			for (AbstractModelDocMultivalue doc : lista) {
				if (doc.getIdMulti().equals(model.getIdMulti())) {
					ret = true;
					break;
				}
			}
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - contem");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * retorna um AbstractModelDocMultivalue de um determinado Id
	 * 
	 * @param id
	 * @return
	 */
	public AbstractModelDocMultivalue get(String id) {
		AbstractModelDocMultivalue ret = null;
		try {
			if (id == null) {
				return ret;
			}
			for (AbstractModelDocMultivalue doc : lista) {
				if (doc.getIdMulti().equals(id)) {
					ret = doc;
					break;
				}
			}
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - contem");
			e.printStackTrace();
		}
		return ret;
	}

	public void remove(AbstractModelDocMultivalue doc) {
		int i = 0;
		for (AbstractModelDocMultivalue it : lista) {
			if (it.getIdMulti().equals(doc.getIdMulti())) {
				lista.remove(i);
				this.sort();
				return;
			}
			i++;
		}
	}

	public void removeByIdMulti(String idMulti) {
		int i = 0;
		for (AbstractModelDocMultivalue it : lista) {
			if (it.getIdMulti().equals(idMulti)) {
				lista.remove(i);
				this.sort();
				return;
			}
			i++;
		}
	}

	public void addNewModel() {
		try {
			String classe = this.getClass().getCanonicalName();
			String modelPackage = br.com.tdec.utils.Utils.getModelPackageFromPackage(classe);
			AbstractModelDocMultivalue model = createApp().createNewModelMultivalue(modelPackage);
			lista.add(model);
		} catch (Exception e) {
			printErro(e);
		}
	}

	/**
	 * Converte um uma lista de maps os campos para facilitar a gravação no disco.
	 * Criado originalmente para uso em innerClasses que não funcionam com o
	 * SaveModel por que os MultiValueFields não são públicos na lista de classes.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<?>> convertToMap() {
		Map<String, List<?>> ret = new HashMap<String, List<?>>();
		try {
			Method method;
			List<Field> campos;
			ArrayList<Object> check;
			for (AbstractModelDocMultivalue model : lista) {
				campos = model.getAllModelFields();
				for (Field campo : campos) {
					campo.setAccessible(true);
					method = model.getGetMethod(campo.getName());
					if (method != null) {
						check = (ArrayList<Object>) ret.get(campo.getName());
						if (check == null) {
							ret.put(campo.getName(), new ArrayList<Object>(Arrays.asList(method.invoke(model))));
						} else {
							check.add(method.invoke(model));
						}
					}
				}
			}
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
		return ret;
	}

	@Override
	public Iterator<AbstractModelDocMultivalue> iterator() {
		return new InnerIterator();
	}

	// Inner class
	class InnerIterator implements Iterator<AbstractModelDocMultivalue> {
		int currentIndex = 0;

		// @Override
		@Override
		public boolean hasNext() {
			if (currentIndex >= lista.size()) {
				return false;
			} else {
				return true;
			}
		}

		// @Override
		@Override
		public AbstractModelDocMultivalue next() {
			return lista.get(currentIndex++);
		}

		// @Override
		@Override
		public void remove() {
			lista.remove(--currentIndex);
		}
	}

	public void addListaToBottom(AbstractModelListaMultivalue<?> outraLista) {
		lista.addAll(outraLista.getLista());
		// for (AbstractModelDocMultivalue doc : outraLista.getLista()) {
		// if (outraLista.getClass().equals(this.getClass())) {
		// this.addBottom(doc);
		// }
		// }
	}

	public int size() {
		return lista.size();
	}

	public int getSize() {
		return lista.size();
	}

	public void sort() {
		Collections.sort(lista);
	}

	public void sort(String campo, int ordem) {
		GenericComparator comparator = new GenericComparator(campo, ordem);
		Collections.sort(this.getLista(), comparator);
	}

	@Override
	public int compareTo(AbstractModelDocMultivalue o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
