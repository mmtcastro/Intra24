package br.com.tdec.intra.abs;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Lista genérica para modelos multivalue.
 * 
 * @param <E> tipo do item, que deve ser um AbstractModelDocMultivalue e
 *            Comparable (para sort()).
 */
public class AbstractModelListaMultivalue<E extends AbstractModelDocMultivalue> extends AbstractModel
		implements Iterable<E> {

	protected List<E> lista = new ArrayList<>();

	public AbstractModelListaMultivalue() {
		// nada além de iniciar a lista
	}

	// ============ Básicos ============

	public List<E> getLista() {
		return lista;
	}

	public int size() {
		return lista.size();
	}

	public int getSize() {
		return lista.size();
	}

	public boolean isEmpty() {
		return lista.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return lista.iterator();
	}

	// ============ Operações de adição/remoção/busca ============

	/**
	 * Adiciona no final e ordena (se E for Comparable).
	 */
	public void addBottom(E model) {
		if (model == null) {
			print("Warning - AbstractModelListaMultivalue - addBottom - model == null");
			return;
		}
		if (model.getIdMulti() == null) {
			print("Warning - AbstractModelListaMultivalue - addBottom - model.getIdMulti() == null: "
					+ model.getClass());
			return;
		}
		try {
			lista.add(model);
			this.sort();
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - addBottom - problema ao adicionar: " + model.getIdMulti());
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona no topo (índice 0) e ordena (se E for Comparable). Se já existir
	 * item com mesmo ID, substitui conteúdo (opcional implementar).
	 */
	public void addTop(E model) {
		if (model == null) {
			print("Warning - AbstractModelListaMultivalue - addTop - model == null");
			return;
		}
		if (model.getIdMulti() == null) {
			print("Warning - AbstractModelListaMultivalue - addTop - model.getIdMulti() == null: " + model.getClass());
			return;
		}
		try {
			lista.add(0, model);
			this.sort();
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - addTop - problema ao adicionar: " + model.getIdMulti());
			e.printStackTrace();
		}
	}

	/**
	 * Verifica se contém pelo idMulti.
	 */
	public boolean contem(E model) {
		if (model == null || model.getIdMulti() == null)
			return false;
		try {
			for (E doc : lista) {
				if (model.getIdMulti().equals(doc.getIdMulti())) {
					return true;
				}
			}
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - contem");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retorna o item pelo idMulti ou null se não achar.
	 */
	public E get(String id) {
		if (id == null)
			return null;
		try {
			for (E doc : lista) {
				if (id.equals(doc.getIdMulti())) {
					return doc;
				}
			}
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - get");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Remove pelo objeto (compara por idMulti).
	 */
	public void remove(E doc) {
		if (doc == null || doc.getIdMulti() == null)
			return;
		for (int i = 0; i < lista.size(); i++) {
			E it = lista.get(i);
			if (doc.getIdMulti().equals(it.getIdMulti())) {
				lista.remove(i);
				this.sort();
				return;
			}
		}
	}

	/**
	 * Remove pelo idMulti.
	 */
	public void removeByIdMulti(String idMulti) {
		if (idMulti == null)
			return;
		for (int i = 0; i < lista.size(); i++) {
			E it = lista.get(i);
			if (idMulti.equals(it.getIdMulti())) {
				lista.remove(i);
				this.sort();
				return;
			}
		}
	}

	/**
	 * Adiciona todos os itens de outra lista ao final desta.
	 */
	public void addListaToBottom(AbstractModelListaMultivalue<? extends E> outraLista) {
		if (outraLista == null || outraLista.getLista() == null)
			return;
		lista.addAll(outraLista.getLista());
		this.sort();
	}

	/**
	 * Ordena a lista com base no Comparable de E.
	 */
	public void sort() {
		Collections.sort(lista);
	}

	// ============ Fábrica / Instanciação ============

	/**
	 * Cria e adiciona uma nova instância de E com base na convenção do seu Utils.
	 * Mantive sua lógica, mas tipada. Requer: -
	 * Utils.getModelPackageFromPackage(String canonicalName) ->
	 * "pacote.ClasseConcreta" - Construtor sem args na classe concreta
	 */
	@SuppressWarnings("unchecked")
	public void addNewModel() {
		try {
			String classe = this.getClass().getCanonicalName();
			String modelFqcn = Utils.getModelPackageFromPackage(classe);

			if (Utils.classeExiste(modelFqcn)) {
				Class<?> modelClass = Class.forName(modelFqcn);
				Object instance = modelClass.getDeclaredConstructor().newInstance();

				if (!(instance instanceof AbstractModelDocMultivalue)) {
					print("Erro - addNewModel - classe não é AbstractModelDocMultivalue: " + modelFqcn);
					return;
				}
				// Tenta fazer cast para E (unchecked, mas ok por convenção do projeto)
				E model = (E) instance;
				lista.add(model);
				this.sort();
			} else {
				print("Erro - addNewModel - classe do modelo não encontrada: " + modelFqcn);
			}
		} catch (Exception e) {
			print("Erro - AbstractModelListaMultivalue - addNewModel");
			e.printStackTrace();
		}
	}

	// ============ Conversão utilitária para Map (para persistência) ============

	/**
	 * Converte a lista em Map<String, List<?>> com os valores dos campos, agregando
	 * por nome do campo. Depende de: - E.getAllModelFields() -
	 * E.getGetMethod(String fieldName)
	 */
	@SuppressWarnings("unchecked")
	public Map<String, List<?>> convertToMap() {
		Map<String, List<?>> ret = new HashMap<>();
		try {
			for (E model : lista) {
				List<Field> campos = model.getAllModelFields();
				for (Field campo : campos) {
					campo.setAccessible(true);
					Method getter = model.getGetMethod(campo.getName());
					if (getter != null) {
						List<Object> bucket = (List<Object>) ret.get(campo.getName());
						if (bucket == null) {
							bucket = new ArrayList<>();
							ret.put(campo.getName(), bucket);
						}
						bucket.add(getter.invoke(model));
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			print("Erro - AbstractModelListaMultivalue - convertToMap");
			e.printStackTrace();
		}
		return ret;
	}
}
