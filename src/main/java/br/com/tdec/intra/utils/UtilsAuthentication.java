package br.com.tdec.intra.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UtilsAuthentication {

	/**
	 * Utilizado pelo LoginForm para setar os roles baseados nos grupos do Domino
	 * Directory
	 * 
	 */
	public static void setRole(String username, String roleName) {
		// String role = convertGroupNameToRole(groupName); - ja faco isso no User
		// Fetch additional authorities for the user from an external source
		List<GrantedAuthority> additionalAuthorities = fetchAdditionalAuthorities(username, roleName);

		// Get the current authentication object
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Update the user's authorities with the additional authorities
		List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
		authorities.addAll(additionalAuthorities);

		// Create a new authentication object with updated authorities
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(),
				authentication.getCredentials(), authorities);

		// Set the new authentication object in the security context
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);

		// Collection<? extends GrantedAuthority> authorities =
		// authentication.getAuthorities();

//		System.out.println("User Authorities:");
//		authorities.stream().map(GrantedAuthority::getAuthority).forEach(System.out::println);

	}

	public static List<GrantedAuthority> fetchAdditionalAuthorities(String username, String role) {
		// Fetch additional authorities for the user from an external source
		// This could be querying a database, LDAP, or another service
		// For demonstration purposes, we'll return a hardcoded list of authorities
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(role));
		return authorities;
	}

	public static String convertGroupNameToRole(String groupName) {
		String role = "ROLE_" + groupName.trim().toUpperCase();
		return role;
	}

	public static List<String> getRoles() {
		// Get the current authentication object from the SecurityContext
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Check if the authentication object is not null
		if (authentication == null) {
			throw new IllegalStateException("No authentication details found in Security Context");
		}

		// Extract the authorities (roles) and convert them to a list of strings
		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		// Optionally, print the roles to the console (you can remove this if not
		// needed)
//		System.out.println("Current User Authorities:");
//		roles.forEach(System.out::println);

		// Return the list of roles
		return roles;
	}

}
