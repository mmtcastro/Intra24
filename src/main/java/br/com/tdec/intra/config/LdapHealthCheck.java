package br.com.tdec.intra.config;

import java.util.List;

import javax.naming.directory.DirContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class LdapHealthCheck {

	private final LdapTemplate ldapTemplate;
	private final LdapProperties ldapProperties;

	@Autowired
	public LdapHealthCheck(LdapTemplate ldapTemplate, LdapProperties ldapProperties) {
		this.ldapTemplate = ldapTemplate;
		this.ldapProperties = ldapProperties;
	}

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

	private boolean testLdapConnection() {
		try {
			// Teste 1: Verifica se o servidor responde
			ldapTemplate.lookup("");
			System.out.println("✅ O servidor LDAP do Domino Directory está respondendo!");

			// Teste 2: Verifica se o usuário admin consegue autenticar no LDAP usando
			// `ldapProperties`
			DirContext ctx = ldapTemplate.getContextSource().getContext(ldapProperties.getManagerDn(),
					ldapProperties.getManagerPassword());
			ctx.close();
			System.out.println("✅ O usuário administrador autenticou com sucesso no LDAP!");

			// Teste 3: Faz uma consulta genérica para verificar se há entradas
			List<String> entries = ldapTemplate.search(LdapQueryBuilder.query().where("objectClass").isPresent(),
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
