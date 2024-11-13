package br.com.tdec.intra.abs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import br.com.tdec.intra.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractModelDocMultivalue extends AbstractModel implements Comparable<AbstractModelDocMultivalue> {
	/**
	 * Modelos que existem distribuidos em campos multiValue dentro de um
	 * determinado AbstractModelDoc (Document). Como utilizamos muito dentro de
	 * nossos documentos Notes, achei importante criar os objetos para podemormos
	 * manipula-los em Java para depois coloca-los de volta no modelo onde ele
	 * "vive" Obrigatorio! Criar os campos: private String idNomeDoModelo
	 * (Ex.:idAvisoChamado); private String autorNomeDoModelo (Ex.:
	 * autorAvisoChamado); private Date criacaoNomeDoModelo (Ex.:
	 * criacaoAvisoChamado); Caso contrario o AbstractDaoNsf nao funcionará
	 */

	protected String idMulti; // importante para adicionar e remover multivalue dentro do bean

	public AbstractModelDocMultivalue() {
		idMulti = Utils.randomString(5);
	}

	/**
	 * deixei o CompareTo pronto para ser implementado a nivel de objeto, pois não
	 * há como implementar genericamente, já que não existe um campo padrao de
	 * comparacao. O básico vai se como ele vem do Multivaluefield dentro do Doc.
	 * 
	 */
	@Override
	public int compareTo(AbstractModelDocMultivalue arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * retorna os campos que devem ser procurados dentro do Documento/Modelo de
	 * origem Temos que retirar o SerialID e o origem pois só podemos procurar por
	 * campos multiValue que são ArrayLists. Procurar por Longs ou por
	 * AbstractModelDoc daria errado isSynthetic fields é importante para evitar
	 * métodos getThis$0() criados pelo compilador Java
	 * 
	 * @return
	 */
	public ArrayList<Field> getValidFields() {
		ArrayList<Field> fields = new ArrayList<Field>();
		try {
			// ArrayList<Integer> remover = new ArrayList<Integer>();
			ArrayList<String> removeFields = new ArrayList<String>();
			removeFields.add("origem");
			removeFields.add("autor");
			removeFields.add("criacao");
			removeFields.add("id");
			removeFields.add("serialVersionUID");

			Field[] declaredFields = this.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
				if (!field.isSynthetic() && !removeFields.contains(field.getName())) { //
					fields.add(field);
				}
			}

			// int i = 0;
			// Field[] f = this.getClass().getDeclaredFields();
			// Collections.addAll(fields, f);
			// for (Field field : fields) {
			// if (removeFields.contains(field.getName())) {
			// remover.add(i);
			// }
			// i++;
			// }
			// for (Integer r : remover) {
			// fields.remove(r.intValue());
			// }
		} catch (SecurityException e) {
			System.out.println("Erro - AbstractModelDocMultivalue - getValidFields");
			e.printStackTrace();
		}
		return fields;
	}

	/**
	 * retorna o nome dos campos validos do Multivalue. Para facilitar a leitura do
	 * codigo do AbstractDaoNsf
	 * 
	 * @return
	 */
	public List<String> getValidFieldsNames() {
		List<String> ret = new ArrayList<String>();
		List<Field> fields = getValidFields();
		for (Field field : fields) {
			ret.add(field.getName());
		}
		return ret;

	}

	public String getValorFinal() {
		// TODO Auto-generated method stub
		return null;
	}
}
