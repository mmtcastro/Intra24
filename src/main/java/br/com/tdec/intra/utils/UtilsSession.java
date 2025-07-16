package br.com.tdec.intra.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.flow.server.VaadinSession;

import br.com.tdec.intra.directory.model.User;
import jakarta.servlet.http.HttpSession;

public class UtilsSession {
	private LdapTemplate ldapTemplate;

	public UtilsSession(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	// Function to get all session properties as a map
	public static Map<String, Object> getAllSessionProperties(HttpSession session) {
		Map<String, Object> sessionProperties = new HashMap<>();

		// Get all attribute names in the session
		Enumeration<String> attributeNames = session.getAttributeNames();

		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			Object attributeValue = session.getAttribute(attributeName);

			// Add the attribute name and value to the map
			sessionProperties.put(attributeName, attributeValue);
		}

		return sessionProperties;
	}

	public List<SearchResult> searchGroups(String name) {
		return ldapTemplate.search("O=TDec", // Customize with your groups search base
				"(cn=" + name + ")", // Filter by group name
				new AttributesMapper<SearchResult>() {
					public Map<String, Object> mapAttributes(Attributes attrs) {
						Map<String, Object> map = new HashMap<>();
						try {
							map.put("name", attrs.get("cn").get());
						} catch (NamingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// Add other desired attributes if needed
						return map;
					}

					@Override
					public SearchResult mapFromAttributes(Attributes attributes) throws NamingException {
						// TODO Auto-generated method stub
						return null;
					}
				});
	}

	public static String getCurrentUsername2() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof UserDetails details) {
				return details.getUsername();
			} else if (principal instanceof String string) {
				return string;
			}
		}
		return null; // No user is authenticated
	}

	public static String getCurrentUserName() {
		String ret = "User não encontrado em VaadinSession";
		User user = (User) VaadinSession.getCurrent().getAttribute("user");
		if (user != null) {
			ret = user.getCommonName();
		}
		return ret;

	}

}
