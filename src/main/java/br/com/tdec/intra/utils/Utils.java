package br.com.tdec.intra.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.tdec.intra.abs.AbstractService;

public class Utils {

	/**
	 * Retorna
	 * 
	 * @param modelPackage
	 * @return
	 */
	public static List<String> stringToArrayList(String string, String separator) {
		List<String> ret = new ArrayList<>();
		// Escape the separator if it's a special character in regex
		String regexSeparator = separator.matches("[\\[\\]().*+?^$\\\\|]") ? "\\" + separator : separator;
		String[] array = string.split(regexSeparator);
		for (String item : array) {
			ret.add(item);
		}
		return new ArrayList<>(ret);
	}

	/**
	 * Retira os plurais dos modelos Ex. GruposEconomicos -> GrupoEconomico; Ex.
	 * AtencoesEspeciais - > AtencaoEspecial (oes=ao, ais=al)
	 * 
	 * @param modelo
	 * @return
	 */
	public static String removePluraisDoModelo(String modelo) {
		String ret = "";
		ArrayList<String> properCase = properCaseToArrayList(modelo);
		for (String sub : properCase) {
			ret = ret + removePlural(sub);
		}
		return ret;
	}

	/**
	 * Devolve um ArrayList com três posicoes para ContratoHomeBean: Contrato - Home
	 * - Bean
	 * 
	 * @param modelo
	 * @return
	 */
	public static ArrayList<String> properCaseToArrayList(String modelo) {
		ArrayList<String> substrings = new ArrayList<String>();
		int pos = 0;
		for (int i = 0; i < modelo.length(); i++) {
			if (Character.isUpperCase(modelo.charAt(i))) {
				if (i > 0) {
					substrings.add(modelo.substring(pos, i));
					pos = i;
				}
			}
		}
		substrings.add(modelo.substring(pos, modelo.length()));
		return substrings;
	}

	/**
	 * usada inicialmente para saber se um modelo é um doc ou uma lista Ex. Empresa
	 * = doc, Empresas = lista / Especial = doc, Especiais = lista
	 * 
	 * @return
	 */
	public static boolean isPlural(String sub) {
		boolean ret = false;
		try {
			if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("oes")) {
				ret = true;
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("aes")) {
				ret = true;
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("eis")) {
				ret = true;
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("ais")) {
				ret = true;
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("ois")) {
				ret = true;
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("ens")) {
				ret = true;
			} else if (sub.length() > 2 && sub.substring(sub.length() - 2).equals("is")) {
				ret = true;
			} else if (sub.length() > 2 && sub.substring(sub.length() - 2).equals("es")) {
				ret = true;
			} else if (sub.length() > 1 && sub.substring(sub.length() - 1).equals("s")) {
				ret = true;
			}
		} catch (Exception e) {
			print("Erro - Utils - isPlural - " + sub);
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Remove o plural de uma string Utilizada para achar os forms dentro das
	 * classes
	 * 
	 * @param sub
	 * @return
	 */
	public static String removePlural(String sub) {
		String str = "";
		try {
			if (sub.length() > 4 && sub.substring(sub.length() - 4).equals("ores")) { // aprovadores
				str = sub.substring(0, sub.length() - 4) + "or"; // aprovador
			} else if (sub.length() > 4 && sub.substring(sub.length() - 4).equals("eres")) { // pareceres
				str = sub.substring(0, sub.length() - 4) + "er"; // parecer
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("oes")) {
				str = sub.substring(0, sub.length() - 3) + "ao";
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("aes")) {
				str = sub.substring(0, sub.length() - 3) + "ao";
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("eis")) {
				str = sub.substring(0, sub.length() - 3) + "el";
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("ais")) {
				str = sub.substring(0, sub.length() - 3) + "al";
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("ois")) {
				str = sub.substring(0, sub.length() - 3) + "ol";
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("ens")) { // trem
				str = sub.substring(0, sub.length() - 3) + "em";
			} else if (sub.length() > 3 && sub.substring(sub.length() - 3).equals("dis")) { // "gambi" para o plural de
																							// pdi -> pdis
				str = sub.substring(0, sub.length() - 3) + "di";
			} else if (sub.length() > 2 && sub.substring(sub.length() - 2).equals("is")) { // azul, animal, coronel,
																							// fuzil, funil
				str = sub.substring(0, sub.length() - 2) + "l";
				// } else if (sub.length() > 2 && sub.substring(sub.length() -
				// 2).equals("es")) {
				// str = sub.substring(0, sub.length() - 1);
			} else if (sub.length() > 1 && (sub.substring(sub.length() - 1, sub.length())).equals("s")) {
				str = sub.substring(0, sub.length() - 1);
			} else {
				str = sub;
			}
		} catch (Exception e) {
			print("Erro - Utils - removePlural - removendo plural de " + sub);
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * Adiciona o plural de uma string Utilizada para achar os forms dentro das
	 * classes
	 * 
	 * @param sub
	 * @return
	 */
	public static String addPlural(String sub) {
		String str = "";
		try {
			if (sub.substring(sub.length() - 2).equals("or")) { // aprovador
				str = sub.substring(0, sub.length() - 4) + "ores"; // aprovadores
			} else if (sub.substring(sub.length() - 2).equals("ao")) { // aprovacao
				str = sub.substring(0, sub.length() - 2) + "oes"; // aprovacoes
			} else if (sub.substring(sub.length() - 2).equals("ao")) {
				str = sub.substring(0, sub.length() - 3) + "aes";
			} else if (sub.substring(sub.length() - 2).equals("el")) {
				str = sub.substring(0, sub.length() - 3) + "eis";
			} else if (sub.substring(sub.length() - 2).equals("al")) {
				str = sub.substring(0, sub.length() - 2) + "ais";
			} else if (sub.substring(sub.length() - 2).equals("ol")) {
				str = sub.substring(0, sub.length() - 2) + "ois";
			} else if (sub.substring(sub.length() - 2).equals("em")) {
				str = sub.substring(0, sub.length() - 2) + "ens";
			} else if (sub.substring(sub.length() - 1).equals("l")) {
				str = sub.substring(0, sub.length() - 1) + "is";
			} else if (sub.substring(sub.length() - 1).equals("r")) { // parecer - pareceres
				str = sub.substring(0, sub.length() - 1) + "res";
			} else {
				str = sub + "s";
			}
		} catch (Exception e) {
			print("Erro - Utils - AddPlural - no String - " + sub);
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * uma variação do AddPlural, onde um properCase recebe plural em cada parte.
	 * grupoEcomico, gruposEconomicos
	 * 
	 * @param sub
	 * @return
	 */
	public static String addPlurais(String sub) {
		String str = "";
		ArrayList<String> proper = properCaseToArrayList(sub);
		for (String parte : proper) {
			str = str + addPlural(parte);
		}
		return str;
	}

//	public static String toProperCase(String input) {
//		StringBuilder titleCase = new StringBuilder();
//		boolean nextTitleCase = true;
//
//		for (char c : input.toCharArray()) {
//			if (Character.isSpaceChar(c)) {
//				nextTitleCase = true;
//			} else if (nextTitleCase) {
//				c = Character.toTitleCase(c);
//				nextTitleCase = false;
//			}
//
//			titleCase.append(c);
//		}
//		return titleCase.toString();
//	}

	public static String toProperCase(String input) {
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	public static void print(String str) {
		System.out.println(str);
	}

	public static Class<?> getClassModelFromViewClass(Class<?> viewClass) {
		String fullClassName = viewClass.getName(); // Nome completo da classe, incluindo o pacote

		// Substitui ".view." por ".model." e remove o sufixo "View"
		String modelClassName = fullClassName.replace(".view.", ".model.").replaceAll("View$", "");

		try {
			return Class.forName(modelClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Class<?> getModelClassFromListaClass(Class<?> classLista) {
		Class<?> ret = null;
		String className = "";
		String classeForm = "";
		try {
			List<String> classeList = stringToArrayList(classLista.getCanonicalName(), ".");
			List<String> properCase = properCaseToArrayList(classeList.get(classeList.size() - 1));
			className = removePluraisDoModelo(properCase.get(0));
			classeForm = classeList.get(0) + "." + classeList.get(1) + "." + classeList.get(2) + "." + classeList.get(3)
					+ "." + classeList.get(4) + ".model." + className;
			ret = Class.forName(classeForm);

		} catch (Exception e) {
			print("Erro - Utils - getFormClassFromListaClass - " + classLista.getCanonicalName() + " - " + ret);
			e.printStackTrace();
		}
		return ret;
	}

	public static String[] splitProperCase(String str) {
		// Divide a string usando expressões regulares
		String[] parts = str.split("(?=[A-Z])");
		return parts;
	}

	/**
	 * Retorna a classe do form a partir da classe da lista - ex. VerticaisView,
	 * VerticalView
	 */

	public static Class<?> getViewDocClassFromViewListaClass(Class<?> classLista) {
		Class<?> ret = null;
		String className = "";
		try {
			List<String> classeList = stringToArrayList(classLista.getCanonicalName(), ".");
			List<String> properCase = properCaseToArrayList(classeList.get(classeList.size() - 1));

			for (int i = 0; i < properCase.size(); i++) {
				className = className + removePlural(properCase.get(i));
			}
			String classeForm = classeList.get(0) + "." + classeList.get(1) + "." + classeList.get(2) + "."
					+ classeList.get(3) + "." + classeList.get(4) + "." + classeList.get(5) + "." + className;
			ret = Class.forName(classeForm);

		} catch (Exception e) {
			print("Erro - Utils - getViewDocClassFromViewListaClass - " + classLista.getCanonicalName() + " - " + ret);
			e.printStackTrace();
		}
		return ret;
	}

	public String getZonedDateTimeToStringConverter(ZonedDateTime zonedDateTime) {
		if (zonedDateTime == null) {
			return "";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-MM-dd HH:mm:ss Z", new Locale("pt", "BR"));
		return zonedDateTime.format(formatter);
	}

	/**
	 * Retorna a classe do form a partir da classe da lista - ex.
	 * br.com.tdec.intra.empresas.view.VerticaisView,
	 * br.com.tdec.intra.empresas.model.Vertical
	 * 
	 */
	public static Class<?> getModelClassFromViewListaClass(Class<?> viewListaClass) {
		Class<?> ret = null;
		String className = "";
		try {
			List<String> classeList = stringToArrayList(viewListaClass.getCanonicalName(), ".");
			List<String> properCase = properCaseToArrayList(classeList.get(classeList.size() - 1));

			className = removePlural(properCase.get(0));

			String classeModel = classeList.get(0) + "." + classeList.get(1) + "." + classeList.get(2) + "."
					+ classeList.get(3) + "." + classeList.get(4) + ".model." + className;
			ret = Class.forName(classeModel);

		} catch (Exception e) {
			print("Erro - Utils - getModelClassFromViewListaClass - " + viewListaClass.getCanonicalName() + " - "
					+ ret);
			e.printStackTrace();
		}
		return ret;
	}

	public static Class<?> getServiceClassFromViewClass(Class<?> viewListaClass) {
		Class<?> ret = null;
		String className = "";
		try {
			List<String> classeList = stringToArrayList(viewListaClass.getCanonicalName(), ".");
			List<String> properCase = properCaseToArrayList(classeList.get(classeList.size() - 1));

			className = removePlural(properCase.get(0));
			className = className + "Service";

			String classeModel = classeList.get(0) + "." + classeList.get(1) + "." + classeList.get(2) + "."
					+ classeList.get(3) + "." + classeList.get(4) + ".model." + className;
			ret = Class.forName(classeModel);

		} catch (Exception e) {
			print("Erro - Utils - getServiceClassFromViewClass - " + viewListaClass.getCanonicalName() + " - " + ret);
			e.printStackTrace();
		}
		return ret;

	}

	/**
	 * a partir de uma classe view (VerticalView, VerticaisView, Vertical,
	 * Verticais) retornar o scope (empresas)
	 */

	public static String getScopeFromClass(Class<? extends AbstractService> viewClass) {
		List<String> classeList = stringToArrayList(viewClass.getCanonicalName(), ".");
		return classeList.get(4);
	}

}
