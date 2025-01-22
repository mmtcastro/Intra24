package br.com.tdec.intra.utils;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.tdec.intra.abs.AbstractService;

public class Utils {

	/**
	 * valida senha forte (?=.*[a-z]) The string must contain at least 1 lowercase
	 * alphabetical character (?=.*[A-Z]) The string must * contain at least 1
	 * uppercase alphabetical character (?=.*[0-9]) The string must contain at least
	 * 1 numeric character (?=.[!@#\$%\^&]) The string must contain at least one
	 * special character, but we are escaping reserved RegEx characters to avoid
	 * conflict (?=.{8,}) The string must be eight characters or longer
	 * 
	 * Ao menos uma minúscula, uma maiúscula, um número, um símbolo e no minimo 8
	 * caracteres
	 * 
	 * @param password
	 * @return
	 */
	public static boolean isStrongPassword(String password) {
		boolean ret = false;
		String strongRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%\\^&\\*])(?=.{8,})";
		// Create a Pattern object
		Pattern pattern = Pattern.compile(strongRegex);
		// Now create matcher object.
		Matcher matcher = pattern.matcher(password);
		ret = matcher.find();
		return ret;
	}

	/**
	 * Veririca se é um id tipo "tdec", isto é empresa_Empresa_UUID tem que ter três
	 * componentes e o ultimo tem que ser um UUID
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isId(String id) {
		boolean ret = true;
		try {
			if (id == null) {
				return false;
			}
			List<String> check = stringToArrayList(id, "_");
			if (check.size() != 3) {
				return false;
			}
			String uuid = check.get(2);
			// UUID
			if (uuid.length() < 36) {
				return false;
			}
			if (!isUUID(uuid)) {
				return false;
			}

		} catch (Exception e) {
			print("Erro - Utils - isId - id = id");
			e.printStackTrace();
		}
		return ret;

	}

	/**
	 * Verifica se o string passado é ou nao um UUID Id
	 * 
	 * @param id
	 * @return
	 */
	public static Boolean isUUID(String id) {
		if (id == null) {
			return false;
		}
		boolean ret = false;
		Pattern p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
		ret = p.matcher(id).matches();
		return ret;
	}

	/**
	 * Seis caracteres ao menos, uma maiuscula, uma minuscula e pelo menos um numero
	 * 
	 * @param password
	 * @return
	 */
	public static boolean isMediumPassword(String password) {
		boolean ret = false;
		String strongRegex = "^(((?=.*[a-z])(?=.*[A-Z]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[A-Z])(?=.*[0-9])))(?=.{6,})";
		// Create a Pattern object
		Pattern pattern = Pattern.compile(strongRegex);
		// Now create matcher object.
		Matcher matcher = pattern.matcher(password);
		ret = matcher.find();
		return ret;
	}

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
		try {
			int length = sub.length();

			if (length > 1) {
				// Definindo terminações plurais comuns
				Set<String> pluralSuffixes = Set.of("oes", "aes", "eis", "ais", "ois", "ens", "is", "es", "s");
				String suffix = length > 3 ? sub.substring(length - 3) : sub.substring(length - 1);
				// Verifica se o sufixo está entre as terminações de plural
				return pluralSuffixes.contains(suffix);
			}
		} catch (Exception e) {
			System.err.println("Erro - Utils - isPlural - " + sub);
			e.printStackTrace();
		}
		return false;
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
		String str;
		try {
			int length = sub.length();
			if (length >= 2) {
				String lastTwo = sub.substring(length - 2);
				switch (lastTwo) {
				case "or": // Ex: aprovador -> aprovadores
					str = sub + "es";
					break;
				case "ao": // Ex: aprovação -> aprovações
					str = sub.substring(0, length - 2) + "ões";
					break;
				case "el": // Ex: papel -> papéis
					str = sub.substring(0, length - 2) + "éis";
					break;
				case "al": // Ex: animal -> animais
					str = sub.substring(0, length - 2) + "ais";
					break;
				case "ol": // Ex: sol -> sóis
					str = sub.substring(0, length - 2) + "óis";
					break;
				case "em": // Ex: item -> itens
					str = sub.substring(0, length - 2) + "ens";
					break;
				default:
					String lastChar = sub.substring(length - 1);
					if ("l".equals(lastChar)) {
						str = sub.substring(0, length - 1) + "is";
					} else if ("r".equals(lastChar)) { // Ex: parecer -> pareceres
						str = sub + "es";
					} else {
						str = sub + "s"; // Caso geral
					}
					break;
				}
			} else {
				str = sub + "s"; // Caso para strings de comprimento menor que 2
			}
		} catch (Exception e) {
			System.err.println("Erro - Utils - addPlural - no String - " + sub);
			e.printStackTrace();
			str = sub; // Retorna a string original em caso de erro
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

	public static String getScopeFromClass(Class<?> viewClass) {
		List<String> classeList = stringToArrayList(viewClass.getCanonicalName(), ".");
		return classeList.get(4);
	}

	/**
	 * Utilizado no AbstractService para criar o modelo correto para colocar nos
	 * metodos abstratos
	 * 
	 * @param class1
	 * @return
	 */
	public static Class<?> getModelClassFromServiceClass(Class<? extends AbstractService> serviceClass) {
		Class<?> ret = null;
		String className = "";
		try {
			List<String> classeList = stringToArrayList(serviceClass.getCanonicalName(), ".");
			List<String> properCase = properCaseToArrayList(classeList.get(classeList.size() - 1));

			className = removePlural(properCase.get(0));

			String classeModel = classeList.get(0) + "." + classeList.get(1) + "." + classeList.get(2) + "."
					+ classeList.get(3) + "." + classeList.get(4) + ".model." + className;
			ret = Class.forName(classeModel);

		} catch (Exception e) {
			print("Erro - Utils - getModelClassFromServiceClass - " + serviceClass.getCanonicalName() + " - " + ret);
			e.printStackTrace();
		}
		return ret;
	}

	public String generateNewModelId() {
		String ret = "";
		try {
			List<String> classe = Utils.stringToArrayList(this.getClass().getCanonicalName(), ".");
			ret = classe.get(4) + "_" + classe.get(6) + "_" + UUID.randomUUID().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Retorna a versão pluralizada do nome de classe com base no pacote. Exemplo:
	 * `br.com.tdec.intra.empresas.model.Empresa` ->
	 * `br.com.tdec.intra.empresas.model.Empresas`
	 *
	 * @param modelPackage o pacote completo da classe do modelo
	 * @return pacote modificado com o nome da classe em plural, se aplicável
	 */
	public static String getListaPackageFromPackage(String modelPackage) {
		String listaModelPackage = "";

		if (modelPackage == null || modelPackage.isEmpty()) {
			return listaModelPackage;
		}

		// Converte o pacote em uma lista de partes separadas por "."
		List<String> packageParts = stringToArrayList(modelPackage, ".");

		// Identifica a última parte como o nome da classe
		String className = packageParts.get(packageParts.size() - 1);
		List<String> classParts = properCaseToArrayList(className);
		String baseClassName = classParts.get(0); // Nome base da classe, como "Empresa"

		// Pluraliza o nome da classe se necessário
		if (!isPlural(baseClassName)) {
			baseClassName = addPlural(baseClassName);
		}

		// Substitui a última parte do pacote pelo nome da classe pluralizada
		packageParts.set(packageParts.size() - 1, baseClassName);
		listaModelPackage = String.join(".", packageParts);
		return listaModelPackage;
	}

	/**
	 * retorna GruposEconomicos a partir de GrupoEconomico
	 * 
	 * @param modelName
	 * @return
	 */
	public static String getListaNameFromModelName(String modelName) {
		if (modelName == null || modelName.isEmpty()) {
			return "";
		}
		// Divide o nome da classe em partes com base em camel case
		List<String> classParts = properCaseToArrayList(modelName);
		StringBuilder pluralizedName = new StringBuilder();
		for (String part : classParts) {
			part = addPlural(part);
			print(part);
			// Concatena a parte processada
			pluralizedName.append(part);
		}
		return pluralizedName.toString();
	}

	public static String randomString(int length) {
		// char[] CHARSET_AZ_09 =
		// "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		Random random = new SecureRandom();
		char[] result = new char[length];
		for (int i = 0; i < result.length; i++) {
			// picks a random index out of character set > random character
			int randomCharIndex = random.nextInt(characterSet.length);
			result[i] = characterSet[randomCharIndex];
		}
		return new String(result);
	}

	public static String getModelPackageFromPackage(String modelPackage) {
		String ret = "";
		try {
			if (modelPackage == null || modelPackage.isEmpty()) {
				return ret;
			}

			if (!classeExiste(modelPackage)) {
				return ret;
			}

			List<String> classe = stringToArrayList(modelPackage, ".");

			// Verifica o tamanho do pacote e ajusta o índice conforme necessário
			int modelIndex = (classe.size() == 6) ? 5 : 4; // Adapta para o novo tamanho de pacote (6 ao invés de 5)

			if (classe.size() >= modelIndex + 1) {
				ArrayList<String> properCase = properCaseToArrayList(classe.get(modelIndex));

				// Remove o sufixo "Bean" se houver
				String bean = properCase.get(properCase.size() - 1);
				if (bean.length() > 3 && bean.endsWith("Bean")) {
					properCase.set(properCase.size() - 1, bean.substring(0, bean.length() - 4));
				}

				// Constrói o nome da classe com remoção de plurais
				for (String part : properCase) {
					ret += removePlural(part);
				}

				// Ajusta o prefixo do pacote conforme o tamanho detectado
				if (classe.size() == 6) {
					// Estrutura nova: br.com.tdec.intra.empresas.model.Empresa
					ret = classe.get(0) + "." + classe.get(1) + "." + classe.get(2) + "." + classe.get(3) + ".model."
							+ ret;
				} else if (classe.size() == 5) {
					// Estrutura antiga: br.com.tdec.empresas.model.Empresa
					ret = classe.get(0) + "." + classe.get(1) + "." + classe.get(2) + ".model." + ret;
				} else {
					print("ERRO - Utils.getModelPackageFromPackage - " + modelPackage + " - pacote não compatível");
					return "";
				}
			} else {
				print("ERRO - Utils.getModelPackageFromPackage - " + modelPackage + " - estrutura do pacote inválida");
			}
		} catch (Exception e) {
			print("Erro Utils - Utils.getModelPackageFromPackage - " + modelPackage);
			e.printStackTrace();
		}
		return ret;
	}

	public static String getModelPackageFromClass(Class<?> modelClass) {
		if (modelClass == null) {
			return "";
		}

		// Obtém o nome completo do pacote a partir da classe
		String modelPackage = modelClass.getPackage().getName();

		// Reutiliza a função existente que recebe uma String
		return getModelPackageFromPackage(modelPackage);
	}

	/**
	 * verifica se determinada classe existe no pacote
	 * 
	 * @param classe
	 * @return
	 */
	public static boolean classeExiste(String classe) {
		try {
			Class.forName(classe);
			return true;
		} catch (ClassNotFoundException e) {
			System.out.println("Erro - Utils - classe NAO existe  - " + classe);
			return false;
		}
	}

	public static String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Normaliza o nome do arquivo, removendo acentos e caracteres especiais.
	 */
	public static String normalizeFileName(String fileName) {
		// Remove acentos
		String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		normalized = pattern.matcher(normalized).replaceAll("");

		// Substitui caracteres inválidos por "_"
		return normalized.replaceAll("[^a-zA-Z0-9._\\-]", "_");
	}

	/**
	 * Sanitiza o nome do arquivo, removendo acentos, espaços e caracteres
	 * especiais.
	 */
	public static String sanitizeFileName(String fileName) {
		// Normaliza para remover acentos
		String normalized = Normalizer.normalize(fileName, Normalizer.Form.NFD);
		String withoutAccents = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

		// Substitui espaços e caracteres especiais por "_"
		String sanitized = withoutAccents.replaceAll("[^a-zA-Z0-9._-]", "_");

		// Garante que não existam múltiplos "_" seguidos
		sanitized = sanitized.replaceAll("_+", "_");

		// Remove "_" do início ou final do nome
		sanitized = sanitized.replaceAll("^_|_$", "");

		return sanitized;
	}

}
