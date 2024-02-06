package com.sitepark.ies.userrepository.core.domain.entity.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.EqualsVerifier;

@SuppressWarnings("PMD.TooManyMethods")
@SuppressFBWarnings({
	"PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
	"NP_NULL_PARAM_DEREF_NONVIRTUAL"
})
class LdapIdentityTest {

	private static final String USER_DN = "userdn";

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(LdapIdentity.class)
			.verify();
	}

	@Test
	void testSetServer() throws JsonProcessingException {
		LdapIdentity ldapIdentity = LdapIdentity.builder()
				.server(2)
				.dn(USER_DN)
				.build();
		assertEquals(2, ldapIdentity.getServer(), "unexpected server");
	}

	@Test
	void testSetInvalidServer() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			LdapIdentity.builder().server(0);
		});
	}

	@Test
	void testSetNullDn() throws JsonProcessingException {
		assertThrows(NullPointerException.class, () -> {
			LdapIdentity.builder().dn(null);
		});
	}

	@Test
	void testSetBlankdDn() throws JsonProcessingException {
		assertThrows(IllegalArgumentException.class, () -> {
			LdapIdentity.builder().dn(" ");
		});
	}

	@Test
	void testMissingServer() throws JsonProcessingException {
		assertThrows(IllegalStateException.class, () -> {
			LdapIdentity.builder()
			.dn(USER_DN)
			.build();
		});
	}

	@Test
	void testMissingDn() throws JsonProcessingException {
		assertThrows(IllegalStateException.class, () -> {
			LdapIdentity.builder()
			.server(1)
			.build();
		});
	}

	@Test
	void testSetDn() throws JsonProcessingException {
		LdapIdentity ldapIdentity = LdapIdentity.builder()
				.server(2)
				.dn(USER_DN)
				.build();
		assertEquals(USER_DN, ldapIdentity.getDn(), "unexpected server");
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	void testToBuilder() throws JsonProcessingException {

		LdapIdentity ldapIdentity = LdapIdentity.builder()
				.server(2)
				.dn(USER_DN)
				.build();

		LdapIdentity changedLdapIdentity = ldapIdentity.toBuilder()
				.build();

		assertEquals(2, changedLdapIdentity.getServer(), "unexpected server");
		assertEquals(USER_DN, changedLdapIdentity.getDn(), "dn");
	}

	@Test
	void testSerialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

		LdapIdentity ldapIdentity = LdapIdentity.builder()
				.server(2)
				.dn(USER_DN)
				.build();

		String json = mapper.writeValueAsString(ldapIdentity);

		String expected = "{\"@type\":\"ldap\",\"server\":2,\"dn\":\"userdn\"}";

		assertEquals(expected, json, "unexpected json");
	}

	@Test
	void testDeserialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		SimpleModule module = new SimpleModule();
		mapper.registerModule(module);

		String json = "{\"@type\":\"ldap\",\"server\":2,\"dn\":\"userdn\"}";

		LdapIdentity ldapIdentity = mapper.readValue(json, LdapIdentity.class);

		LdapIdentity expected = LdapIdentity.builder()
				.server(2)
				.dn(USER_DN)
				.build();

		assertEquals(expected, ldapIdentity, "unexpected ldapIdentity");
	}
}
