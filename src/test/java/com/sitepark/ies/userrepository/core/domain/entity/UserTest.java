package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.entity.role.UserLevelRoles;

import nl.jqno.equalsverifier.EqualsVerifier;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class UserTest {

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(User.class)
			.verify();
	}

	@Test
	void testSerialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

		User user = User.builder()
				.id(100560100000014842L)
				.anchor("user.peterpan")
				.firstname("Peter")
				.lastname("Pan")
				.login("peterpan")
				.roleList(
						UserLevelRoles.USER,
						Ref.ofAnchor("test.anchor"),
						Ref.ofId(123L))
				.ldapIdentity(LdapIdentity.builder()
						.server(2)
						.dn("userdn"))
				.note("a note")
				.build();

		String json = mapper.writeValueAsString(user);

		String expected = "{\"id\":100560100000014842," +
				"\"anchor\":\"user.peterpan\"," +
				"\"login\":\"peterpan\"," +
				"\"firstname\":\"Peter\"," +
				"\"lastname\":\"Pan\"," +
				"\"note\":\"a note\"," +
				"\"ldapIdentity\":{\"server\":2,\"dn\":\"userdn\"}," +
				"\"roleList\":[\"USER\",\"REF(test.anchor)\",\"REF(123)\"]}";

		assertEquals(expected, json, "unexpected json");
	}

	@Test
	void testDeserialize() throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jdk8Module());
		SimpleModule module = new SimpleModule();
		mapper.registerModule(module);

		String json = "{" +
				"\"id\":100560100000014842," +
				"\"anchor\":\"user.peterpan\"," +
				"\"login\":\"peterpan\"," +
				"\"firstname\":\"Peter\"," +
				"\"lastname\":\"Pan\"," +
				"\"note\":\"a note\"," +
				"\"ldapIdentity\":{\"server\":2,\"dn\":\"userdn\"}," +
				"\"roleList\":[\"USER\",\"REF(test.anchor)\",\"REF(123)\"]" +
		"}";

		User user = mapper.readValue(json, User.class);

		User expected = User.builder()
				.id(100560100000014842L)
				.anchor("user.peterpan")
				.firstname("Peter")
				.lastname("Pan")
				.login("peterpan")
				.roleList(
						UserLevelRoles.USER,
						Ref.ofAnchor("test.anchor"),
						Ref.ofId(123L))
				.ldapIdentity(LdapIdentity.builder()
						.server(2)
						.dn("userdn"))
				.note("a note")
				.build();

		assertEquals(expected, user, "unexpected user");
	}

	@Test
	@SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
	void testToBuilder() throws JsonProcessingException {
		User user = User.builder()
				.id(100560100000014842L)
				.anchor("user.peterpan")
				.firstname("Peter")
				.lastname("Pan")
				.login("peterpan")
				.note("Test")
				.build();

		User changedUser = user.toBuilder()
				.build();

		assertEquals(100560100000014842L, changedUser.getId().get(), "unexpected id");
		assertEquals(Optional.of(Anchor.ofString("user.peterpan")), changedUser.getAnchor(), "unexpected anchor");
		assertEquals(Optional.of("Peter"), changedUser.getFirstname(), "unexpected firstname");
		assertEquals(Optional.of("Pan"), changedUser.getLastname(), "unexpected lastname");
		assertEquals("peterpan", changedUser.getLogin(), "unexpected login");
		assertEquals(Optional.of("Test"), changedUser.getNote(), "unexpected note");
	}

	@Test
	void testSetFirstname() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.firstname("Peter")
				.build();
		assertEquals("Peter", user.getFirstname().get(), "unexpected firstname");
	}

	@Test
	void testSetFirstnameWithNull() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.firstname(null)
				.build();
		assertTrue(user.getFirstname().isEmpty(), "firstname should be empty");
	}

	@Test
	void testSetFirstnameWithBlank() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.firstname("  ")
				.build();
		assertTrue(user.getFirstname().isEmpty(), "firstname should be empty");
	}

	@Test
	void testSetLastname() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.lastname("Pan")
				.build();
		assertEquals("Pan", user.getLastname().get(), "unexpected lastname");
	}

	@Test
	void testSetLastnameWithNull() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.lastname(null)
				.build();
		assertTrue(user.getLastname().isEmpty(), "lastname should be empty");
	}

	@Test
	void testSetLastnameWithBlank() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.lastname("  ")
				.build();
		assertTrue(user.getLastname().isEmpty(), "lastname should be empty");
	}

	@Test
	void testSetLdapIdentity() throws JsonProcessingException {

		LdapIdentity ldapIdentity = LdapIdentity.builder()
				.server(2)
				.dn("userdn")
				.build();

		User user = User.builder()
				.login("test")
				.ldapIdentity(ldapIdentity)
				.build();

		assertEquals(2, user.getLdapIdentity().get().getServer(), "unexpected ldapIdentity server");
	}

	@Test
	void testSetLdapIdentityBuilder() throws JsonProcessingException {

		User user = User.builder()
				.login("test")
				.ldapIdentity(LdapIdentity.builder()
						.server(2)
						.dn("userdn"))
				.build();

		assertEquals(2, user.getLdapIdentity().get().getServer(), "unexpected ldapIdentity server");
	}


	@Test
	void testSetNullLdapIdentity() throws JsonProcessingException {

		assertThrows(AssertionError.class, () -> {
			User.builder()
			.login("test")
			.ldapIdentity((LdapIdentity)null);
		});
	}
}
