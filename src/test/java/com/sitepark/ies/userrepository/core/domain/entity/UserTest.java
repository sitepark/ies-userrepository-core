package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.sitepark.ies.userrepository.core.domain.entity.identity.LdapIdentity;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.entity.role.UserLevelRoles;

import nl.jqno.equalsverifier.EqualsVerifier;

@SuppressWarnings({
	"PMD.AvoidDuplicateLiterals",
	"PMD.TooManyMethods",
	"PMD.GodClass"
})
class UserTest {

	private static Identity TEST_IDENTITY = LdapIdentity.builder()
			.server(2)
			.dn("userdn")
			.build();

	@Test
	@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
	void testEquals() {
		EqualsVerifier.forClass(User.class)
			.verify();
	}

	@Test
	void testBuildWithoutLogin() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().build();
		});
	}

	@Test
	void testSetLogin() throws JsonProcessingException {
		User user = User.builder()
				.login("peterpan")
				.build();
		assertEquals("peterpan", user.getLogin(), "unexpected login");
	}

	@Test
	void testSetId() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.id(123)
				.build();
		assertEquals(123, user.getId().get(), "unexpected id");
	}

	@Test
	void testGetEmptyId() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.build();
		assertTrue(user.getId().isEmpty(), "id should be empty");
	}

	@Test
	void testSetInvalidId() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().id(0);
		});
	}

	@Test
	void testSetAnchor() throws JsonProcessingException {
		Anchor anchor = Anchor.ofString("user.pan.peter");
		User user = this.createBuilderWithRequiredValues()
				.anchor(anchor)
				.build();
		assertEquals("user.pan.peter", user.getAnchor().get().getName(), "unexpected anchor");
	}

	@Test
	void testSetAnchorString() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.anchor("user.pan.peter")
				.build();
		assertEquals("user.pan.peter", user.getAnchor().get().getName(), "unexpected anchor");
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
		User user = this.createBuilderWithRequiredValues()
				.firstname("Peter")
				.build();
		assertEquals("Peter", user.getFirstname().get(), "unexpected firstname");
	}

	@Test
	void testSetFirstnameWithNull() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.firstname(null)
				.build();
		assertTrue(user.getFirstname().isEmpty(), "firstname should be empty");
	}

	@Test
	void testSetFirstnameWithBlank() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.firstname("  ")
				.build();
		assertTrue(user.getFirstname().isEmpty(), "firstname should be empty");
	}

	@Test
	void testSetLastname() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.lastname("Pan")
				.build();
		assertEquals("Pan", user.getLastname().get(), "unexpected lastname");
	}

	@Test
	void testSetLastnameWithNull() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.lastname(null)
				.build();
		assertTrue(user.getLastname().isEmpty(), "lastname should be empty");
	}

	@Test
	void testSetLastnameWithBlank() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.lastname("  ")
				.build();
		assertTrue(user.getLastname().isEmpty(), "lastname should be empty");
	}

	@Test
	void testGetNameOnlyLastname() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.lastname("Pan")
				.build();
		assertEquals("Pan", user.getName(), "unexpected name");
	}

	@Test
	void testGetNameOnlyFirstname() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.firstname("Peter")
				.build();
		assertEquals("Peter", user.getName(), "unexpected name");
	}

	@Test
	void testGetNameFirstnameAndLastName() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.lastname("Pan")
				.firstname("Peter")
				.build();
		assertEquals("Pan, Peter", user.getName(), "unexpected name");
	}

	@Test
	void testSetEmail() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.email("peter.pan@nimmer.land")
				.build();
		assertEquals("peter.pan@nimmer.land", user.getEmail().get(), "unexpected lastname");
	}

	@Test
	void testSetEmailWithNull() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.email(null)
				.build();
		assertTrue(user.getEmail().isEmpty(), "email should be empty");
	}

	@Test
	void testSetEmailWithBlank() throws JsonProcessingException {
		User user = User.builder()
				.login("test")
				.email("  ")
				.build();
		assertTrue(user.getEmail().isEmpty(), "email should be empty");
	}

	@Test
	void testSetRoleListAsList() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.roleList(Arrays.asList(Ref.ofId(123L)))
				.build();
		assertEquals(Arrays.asList(Ref.ofId(123L)), user.getRoleList(), "unexpected roleList");
	}

	@Test
	void testSetOverwriteRoleListAsList() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.roleList(Arrays.asList(Ref.ofId(123L)))
				.build();
		User overwritter = user.toBuilder()
				.roleList(Arrays.asList(Ref.ofId(345L)))
				.build();
		assertEquals(Arrays.asList(Ref.ofId(345L)), overwritter.getRoleList(), "unexpected roleList");
	}

	@Test
	void testSetNullRoleListAsList() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().roleList((List<Role>)null);
		});
	}

	@Test
	void testSetNullRoleInList() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().roleList(Arrays.asList(Ref.ofId(345L), null));
		});
	}

	@Test
	void testSetRoleListAsVArgs() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.roleList(Ref.ofId(123L))
				.build();
		assertEquals(Arrays.asList(Ref.ofId(123L)), user.getRoleList(), "unexpected roleList");
	}

	@Test
	void testSetOverwriteRoleListAsVArgs() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.roleList(Ref.ofId(123L))
				.build();
		User overwritter = user.toBuilder()
				.roleList(Ref.ofId(345L))
				.build();
		assertEquals(Arrays.asList(Ref.ofId(345L)), overwritter.getRoleList(), "unexpected roleList");
	}

	@Test
	void testSetNullRoleListAsVArgs() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().roleList((Role[])null);
		});
	}

	@Test
	void testSetNullRoleInVArgs() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().roleList(Ref.ofId(345L), null);
		});
	}

	@Test
	void testSetRole() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.role(Ref.ofId(123L))
				.build();
		assertEquals(Arrays.asList(Ref.ofId(123L)), user.getRoleList(), "unexpected roleList");
	}

	@Test
	void testAddRole() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.role(Ref.ofId(123L))
				.build();
		User added = user.toBuilder()
				.role(Ref.ofId(456L))
				.build();

		List<Role> expected = Arrays.asList(Ref.ofId(123L), Ref.ofId(456L));

		assertEquals(expected, added.getRoleList(), "unexpected roleList");
	}

	@Test
	void testSetNullRole() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().role(null);
		});
	}

	@Test
	void testSetIdentityListAsList() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identityList(Arrays.asList(TEST_IDENTITY))
				.build();
		assertEquals(Arrays.asList(TEST_IDENTITY), user.getIdentityList(), "unexpected identityList");
	}

	@Test
	void testSetOverwriteIdentityListAsList() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identityList(Arrays.asList(TEST_IDENTITY))
				.build();
		Identity newIdentity = LdapIdentity.builder()
				.server(3)
				.dn("userdn2")
				.build();
		User overwritter = user.toBuilder()
				.identityList(Arrays.asList(newIdentity))
				.build();
		assertEquals(Arrays.asList(newIdentity), overwritter.getIdentityList(), "unexpected identityList");
	}

	@Test
	void testSetNullIdentityListAsList() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().identityList((List<Identity>)null);
		});
	}

	@Test
	void testSetNullIdentityInList() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().identityList(Arrays.asList(TEST_IDENTITY, null));
		});
	}

	@Test
	void testSetIdentityListAsVArgs() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identityList(TEST_IDENTITY)
				.build();
		assertEquals(Arrays.asList(TEST_IDENTITY), user.getIdentityList(), "unexpected identityList");
	}

	@Test
	void testSetOverwriteIdentityListAsVArgs() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identityList(TEST_IDENTITY)
				.build();
		Identity newIdentity = LdapIdentity.builder()
				.server(3)
				.dn("userdn2")
				.build();
		User overwritter = user.toBuilder()
				.identityList(newIdentity)
				.build();
		assertEquals(Arrays.asList(newIdentity), overwritter.getIdentityList(), "unexpected identityList");
	}

	@Test
	void testSetNullIdentityListAsVArgs() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().identityList((Identity[])null);
		});
	}

	@Test
	void testSetNullIdentityInVArgs() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().identityList(TEST_IDENTITY, null);
		});
	}

	@Test
	void testSetIndentity() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identity(TEST_IDENTITY)
				.build();
		assertEquals(Arrays.asList(TEST_IDENTITY), user.getIdentityList(), "unexpected identityList");
	}

	@Test
	void testAddIdentity() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identity(TEST_IDENTITY)
				.build();

		Identity newIdentity = LdapIdentity.builder()
				.server(3)
				.dn("userdn2")
				.build();

		User added = user.toBuilder()
				.identity(newIdentity)
				.build();

		List<Identity> expected = Arrays.asList(TEST_IDENTITY, newIdentity);

		assertEquals(expected, added.getIdentityList(), "unexpected roleList");
	}

	@Test
	void testSetNullIdentity() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().identity(null);
		});
	}

	@Test
	void testGetIdentity() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.identity(TEST_IDENTITY)
				.build();
		Optional<LdapIdentity> identity = user.getIdentity(LdapIdentity.class);
		assertEquals(TEST_IDENTITY, identity.get(), "unexpected identity");
	}

	@Test
	void testGetUnknownIdentity() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.build();
		Optional<LdapIdentity> identity = user.getIdentity(LdapIdentity.class);
		assertTrue(identity.isEmpty(), "identity should be empty");
	}

	@Test
	void testDefaultGender() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.build();
		assertEquals(GenderType.UNKNOWN, user.getGender(), "unexpected gender");
	}

	@Test
	void testSetGender() throws JsonProcessingException {
		User user = this.createBuilderWithRequiredValues()
				.gender(GenderType.MALE)
				.build();
		assertEquals(GenderType.MALE, user.getGender(), "unexpected gender");
	}

	@Test
	void testSetGenderWithNull() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().gender(null);
		});
	}

	@Test
	void testSetValiditiy() throws JsonProcessingException {

		User user = this.createBuilderWithRequiredValues()
				.validity(UserValidity.builder()
						.blocked(true)
						.build()
				)
				.build();

		assertTrue(user.getValidity().isBlocked(), "user should be blocked");
	}

	@Test
	void testSetValiditiyBuilder() throws JsonProcessingException {

		User user = this.createBuilderWithRequiredValues()
				.validity(UserValidity.builder()
						.blocked(true)
				)
				.build();

		assertTrue(user.getValidity().isBlocked(), "user should be blocked");
	}

	@Test
	void testSetNullValiditiy() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().validity((UserValidity)null);
		});
	}

	@Test
	void testSetNullValiditiyBuilder() throws JsonProcessingException {
		assertThrows(AssertionError.class, () -> {
			User.builder().validity((UserValidity.Builder)null);
		});
	}

	@Test
	void testToString() throws JsonProcessingException {
		User user = User.builder()
				.id(100560100000014842L)
				.anchor("user.peterpan")
				.firstname("Peter")
				.lastname("Pan")
				.email("peter.pan@nimmer.land")
				.gender(GenderType.MALE)
				.login("peterpan")
				.roleList(
						UserLevelRoles.USER,
						Ref.ofAnchor("test.anchor"),
						Ref.ofId(123L))
				.identity(TEST_IDENTITY)
				.note("a note")
				.build();
		String expected = "Pan, Peter " +
				"(login: peterpan, id: " +
				"100560100000014842, " +
				"anchor: user.peterpan, " +
				"roleList: [USER, REF(test.anchor), " +
				"REF(123)])";
		assertEquals(expected, user.toString(), "unexpected string representation");
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
				.email("peter.pan@nimmer.land")
				.gender(GenderType.MALE)
				.login("peterpan")
				.roleList(
						UserLevelRoles.USER,
						Ref.ofAnchor("test.anchor"),
						Ref.ofId(123L))
				.identity(TEST_IDENTITY)
				.note("a note")
				.build();

		String json = mapper.writeValueAsString(user);

		String expected = "{\"id\":100560100000014842," +
				"\"anchor\":\"user.peterpan\"," +
				"\"login\":\"peterpan\"," +
				"\"firstname\":\"Peter\"," +
				"\"lastname\":\"Pan\"," +
				"\"email\":\"peter.pan@nimmer.land\"," +
				"\"gender\":\"MALE\"," +
				"\"note\":\"a note\"," +
				"\"validity\":{\"blocked\":false}," +
				"\"identityList\":[{\"@type\":\"ldap\",\"server\":2,\"dn\":\"userdn\"}]," +
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
				"\"email\":\"peter.pan@nimmer.land\"," +
				"\"gender\":\"MALE\"," +
				"\"note\":\"a note\"," +
				"\"identityList\":[{\"@type\":\"ldap\",\"server\":2,\"dn\":\"userdn\"}]," +
				"\"roleList\":[\"USER\",\"REF(test.anchor)\",\"REF(123)\"]" +
		"}";

		User user = mapper.readValue(json, User.class);

		User expected = User.builder()
				.id(100560100000014842L)
				.anchor("user.peterpan")
				.firstname("Peter")
				.lastname("Pan")
				.email("peter.pan@nimmer.land")
				.gender(GenderType.MALE)
				.login("peterpan")
				.roleList(
						UserLevelRoles.USER,
						Ref.ofAnchor("test.anchor"),
						Ref.ofId(123L))
				.identity(TEST_IDENTITY)
				.note("a note")
				.build();

		assertEquals(expected, user, "unexpected user");
	}

	private User.Builder createBuilderWithRequiredValues() {
		return User.builder()
				.login("test");
	}
}
