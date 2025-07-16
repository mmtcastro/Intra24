package br.com.tdec.intra.config;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class LdapConfig {

	private final LdapProperties ldapProperties;
	private LdapTemplate ldapTemplate;

	public LdapConfig(LdapProperties ldapProperties) {
		this.ldapProperties = ldapProperties;
		// System.out.println("LdapConfigUrls: " + ldapProperties.getUrls());
		// System.out.println("LdapConfigUrl: " + ldapProperties.getUrls().toArray(new
		// String[0]));
	}

	@Bean
	LdapContextSource contextSource() {

		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrls(ldapProperties.getUrls().toArray(new String[0]));
		contextSource.setUserDn(ldapProperties.getManagerDn());
		contextSource.setPassword(ldapProperties.getManagerPassword());
		contextSource.setPooled(true);
		contextSource.afterPropertiesSet(); // colocado posteriormente para iniciar corretamente.

		System.out.println("✅ Ldap ContextSource configurado!");

		return contextSource;

	}

    /**
     * Bean do LdapTemplate para executar consultas LDAP
     */
    @Bean
    LdapTemplate ldapTemplate(LdapContextSource contextSource) {
		System.out.println("✅ LdapTemplate inicializado!");
		return new LdapTemplate(contextSource);
	}

	@Bean
	AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
		LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(contextSource);
		factory.setUserSearchBase("O=TDec");
		factory.setUserSearchFilter("(|(cn={0})(uid={0}))"); // Marcelo Castro OU mcastro

		return factory.createAuthenticationManager();

	}

	// UtilsLdap

	public List<String> findGroups() {
		LdapQuery query = LdapQueryBuilder.query().attributes("CN") // Specify the attribute to return, typically "cn"
																	// for group names
				.where("objectClass").is("dominoGroup"); // Adjust based on your
															// LDAP schema

		return ldapTemplate.search(query, new AttributesMapper<String>() {
			@Override
			public String mapFromAttributes(Attributes attributes) throws NamingException {
				return attributes.get("cn").get().toString();
			}
		});
	}

	public Map<String, List<String>> findGroupsAndMembers() {
		LdapQuery query = LdapQueryBuilder.query().attributes("cn", "member") // Requesting group name and members
				.where("objectClass").is("dominoGroup");

		return ldapTemplate.search(query, new AttributesMapper<Map.Entry<String, List<String>>>() {
			@Override
			public Map.Entry<String, List<String>> mapFromAttributes(Attributes attributes) throws NamingException {
				String groupName = attributes.get("cn").get().toString();
				// Handling multiple members
				List<String> members = new ArrayList<>();
				if (attributes.get("member") != null) {
					NamingEnumeration<?> memberEnum = attributes.get("member").getAll();
					while (memberEnum.hasMore()) {
						members.add(memberEnum.next().toString());
					}
				}
				return new AbstractMap.SimpleEntry<>(groupName, members);
			}
		}).stream().collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);
	}

	public boolean isMember(String user, String group) {
		Map<String, List<String>> groups = findGroupsAndMembers();
		if (groups.containsKey(group)) {
			// Get the list of members for the specified group
			List<String> members = groups.get(group);
			// Iterate through the members to check for a match
			for (String member : members) {
				if (member.contains(user)) { // Adjust this condition based on how specific you want the match to be
					return true; // User is a member of the group
				}
			}
		}
		return false;
	}

	/**
	 * Retorna os grupos de determinado usuario
	 * 
	 * @param userName
	 * @return
	 */
	public List<String> findGroupsForUser(String userName) {
		List<String> userGroups = new ArrayList<>();
		Map<String, List<String>> groups = findGroupsAndMembers();
		for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
			for (String member : entry.getValue()) {
				if (member.contains(userName)) {
					userGroups.add(entry.getKey());
					break; // Stop checking this group if a match is found
				}
			}
		}

		return userGroups;
	}

	/**
	 * Testa a conexão LDAP após a inicialização da aplicação
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void checkLdapOnStartup() {
		System.out.println("🔍 Testando conexão com o LDAP ao iniciar a aplicação...");
		boolean success = testLdapConnection();

		if (!success) {
			System.err.println("❌ ERRO CRÍTICO: Problema ao conectar ao LDAP! Verifique a configuração.");
		} else {
			System.out.println("✅ Conexão LDAP bem-sucedida no startup!");
		}
	}

	public boolean testLdapConnection() {
		try {
			// Teste 1: Verifica se o servidor responde sem consultar um objeto específico
			ldapTemplate.lookup("");
			System.out.println("✅ O servidor LDAP do Domino Directory está respondendo!");

			// Teste 2: Verifica se o usuário admin consegue autenticar no LDAP
			DirContext ctx = ldapTemplate.getContextSource().getContext(ldapProperties.getManagerDn(),
					ldapProperties.getManagerPassword());
			ctx.close();
			System.out.println("✅ O usuário administrador autenticou com sucesso no LDAP!");

			// Teste 3: Faz uma consulta genérica para verificar se consegue recuperar
			// entradas
			List<String> entries = ldapTemplate.search(LdapQueryBuilder.query().where("objectClass").isPresent(), // (objectClass=*)
					(AttributesMapper<String>) attributes -> attributes.get("cn") != null
							? attributes.get("cn").get().toString()
							: "Sem CN");

			if (!entries.isEmpty()) {
				System.out.println("✅ O LDAP respondeu à pesquisa! Algumas entradas encontradas: " + entries);
			} else {
				System.out.println("⚠️ O LDAP respondeu, mas nenhuma entrada foi encontrada.");
			}

			return true;
		} catch (Exception e) {
			System.err.println("❌ Erro ao conectar ao LDAP: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}
