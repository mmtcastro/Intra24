package br.com.tdec.intra.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	/**
	 * Retorna
	 * 
	 * @param modelPackage
	 * @return
	 */
	public static List<String> stringToArrayList(String string, String separador) {
		List<String> ret = new ArrayList<>();
		String[] array = string.split(separador);
		for (String item : array) {
			ret.add(item);
		}
		return ret;

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

		// ArrayList<String> substrings = new ArrayList<String>();
		//
		// int pos = 0;
		//
		// for (int i = 0; i < modelo.length(); i++) {
		// if (Character.isUpperCase(modelo.charAt(i))) {
		// if (i > 0) {
		// substrings.add(modelo.substring(pos, i));
		// pos = i;
		// }
		// }
		// }
		// substrings.add(modelo.substring(pos, modelo.length()));
		//
		// for (String sub : substrings) {
		// ret = ret + removePlural(sub);
		// }
		// return ret;
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
}
