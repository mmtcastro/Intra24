package br.com.tdec.intra.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UtilsLdap {
	private LdapContextSource contextSource;
	private LdapTemplate ldapTemplate;

	public UtilsLdap(LdapContextSource contextSource) {
		this.contextSource = contextSource;
		this.ldapTemplate = new LdapTemplate(contextSource);
	}

//	public List<String> findGroupsForUser(String userDn) {
//		LdapQuery query = LdapQueryBuilder.query().attributes("CN") // Specify the attribute to return, typically "cn"
//																	// for group names
//				.where("objectClass").is("dominoGroup").and("member").is(userDn); // Adjust based on your
//																					// LDAP schema
//
//		return ldapTemplate.search(query, new AttributesMapper<String>() {
//			@Override
//			public String mapFromAttributes(Attributes attributes) throws NamingException {
//				return attributes.get("cn").get().toString();
//			}
//		});
//	}

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
}
