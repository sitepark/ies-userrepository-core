package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.sitepark.ies.userrepository.core.domain.entity.identity.LdapIdentity;
import com.sitepark.ies.userrepository.core.domain.entity.role.Ref;
import com.sitepark.ies.userrepository.core.domain.entity.role.UserLevelRoles;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods", "PMD.GodClass"})
@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL",
  "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"
})
class UserTest {

  private static final Identity TEST_IDENTITY =
      LdapIdentity.builder().server(2).dn("userdn").build();

  @Test
  @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
  void testEquals() {
    EqualsVerifier.forClass(User.class).verify();
  }

  @Test
  void testBuildWithoutLogin() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          User.builder().build();
        });
  }

  @Test
  void testSetLogin() {
    User user = User.builder().login("peterpan").build();
    assertEquals("peterpan", user.getLogin(), "unexpected login");
  }

  @Test
  void testSetIdentifierWithId() {
    Identifier id = Identifier.ofId("123");
    User user = this.createBuilderWithRequiredValues().identifier(id).build();
    assertEquals("123", user.getId().get(), "unexpected id");
  }

  @Test
  void testSetIdentifierWithAnchor() {
    Identifier anchor = Identifier.ofAnchor(Anchor.ofString("abc"));
    User user = this.createBuilderWithRequiredValues().identifier(anchor).build();
    assertEquals("abc", user.getAnchor().get().getName(), "unexpected anchor");
  }

  @Test
  void testSetId() {
    User user = this.createBuilderWithRequiredValues().id("123").build();
    assertEquals("123", user.getId().get(), "unexpected id");
  }

  @Test
  void testGetEmptyId() {
    User user = this.createBuilderWithRequiredValues().build();
    assertTrue(user.getId().isEmpty(), "id should be empty");
  }

  @Test
  void testGetIdentifierWithId() {
    User user = this.createBuilderWithRequiredValues().id("123").build();
    assertEquals(Identifier.ofId("123"), user.getIdentifier().get(), "unexpected identifier");
  }

  @Test
  void testGetIdentifierWithAnchor() {
    User user = this.createBuilderWithRequiredValues().anchor("abc").build();
    assertEquals(
        Identifier.ofAnchor(Anchor.ofString("abc")),
        user.getIdentifier().get(),
        "unexpected identifier");
  }

  @Test
  void testGetEmptyIdentifier() {
    User user = this.createBuilderWithRequiredValues().build();
    assertTrue(user.getIdentifier().isEmpty(), "identifier should be empty");
  }

  @Test
  void testSetIdWithZero() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          User.builder().id("0");
        },
        "id 0 should't be allowed");
  }

  @Test
  void testSetIdWithNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().id(null);
        },
        "null should't be allowed");
  }

  @Test
  void testSeIdWithInvalidValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          User.builder().id("0x");
        },
        "invalid id should't be allowed");
  }

  @Test
  void testSetAnchor() {
    Anchor anchor = Anchor.ofString("user.pan.peter");
    User user = this.createBuilderWithRequiredValues().anchor(anchor).build();
    assertEquals("user.pan.peter", user.getAnchor().get().getName(), "unexpected anchor");
  }

  @Test
  void testSetAnchorString() {
    User user = this.createBuilderWithRequiredValues().anchor("user.pan.peter").build();
    assertEquals("user.pan.peter", user.getAnchor().get().getName(), "unexpected anchor");
  }

  @Test
  @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
  void testToBuilder() {
    User user =
        User.builder()
            .id("100560100000014842")
            .anchor("user.peterpan")
            .firstName("Peter")
            .lastName("Pan")
            .login("peterpan")
            .description("Test")
            .build();

    User changedUser = user.toBuilder().build();

    assertEquals("100560100000014842", changedUser.getId().get(), "unexpected id");
    assertEquals(
        Optional.of(Anchor.ofString("user.peterpan")),
        changedUser.getAnchor(),
        "unexpected anchor");
    assertEquals(Optional.of("Peter"), changedUser.getFirstName(), "unexpected firstname");
    assertEquals(Optional.of("Pan"), changedUser.getLastName(), "unexpected lastname");
    assertEquals("peterpan", changedUser.getLogin(), "unexpected login");
    assertEquals(Optional.of("Test"), changedUser.getDescription(), "unexpected note");
  }

  @Test
  void testSetFirstname() {
    User user = this.createBuilderWithRequiredValues().firstName("Peter").build();
    assertEquals("Peter", user.getFirstName().get(), "unexpected firstname");
  }

  @Test
  void testSetFirstnameWithNull() {
    User user = this.createBuilderWithRequiredValues().firstName(null).build();
    assertTrue(user.getFirstName().isEmpty(), "firstname should be empty");
  }

  @Test
  void testSetFirstnameWithBlank() {
    User user = this.createBuilderWithRequiredValues().firstName("  ").build();
    assertTrue(user.getFirstName().isEmpty(), "firstname should be empty");
  }

  @Test
  void testSetLastname() {
    User user = this.createBuilderWithRequiredValues().lastName("Pan").build();
    assertEquals("Pan", user.getLastName().get(), "unexpected lastname");
  }

  @Test
  void testSetLastnameWithNull() {
    User user = this.createBuilderWithRequiredValues().lastName(null).build();
    assertTrue(user.getLastName().isEmpty(), "lastname should be empty");
  }

  @Test
  void testSetLastnameWithBlank() {
    User user = this.createBuilderWithRequiredValues().lastName("  ").build();
    assertTrue(user.getLastName().isEmpty(), "lastname should be empty");
  }

  @Test
  void testGetNameOnlyLastname() {
    User user = this.createBuilderWithRequiredValues().lastName("Pan").build();
    assertEquals("Pan", user.getName(), "unexpected name");
  }

  @Test
  void testGetNameOnlyFirstname() {
    User user = this.createBuilderWithRequiredValues().firstName("Peter").build();
    assertEquals("Peter", user.getName(), "unexpected name");
  }

  @Test
  void testGetNameFirstnameAndLastName() {
    User user = this.createBuilderWithRequiredValues().lastName("Pan").firstName("Peter").build();
    assertEquals("Pan, Peter", user.getName(), "unexpected name");
  }

  @Test
  void testSetEmail() {
    User user = this.createBuilderWithRequiredValues().email("peter.pan@nimmer.land").build();
    assertEquals("peter.pan@nimmer.land", user.getEmail().get(), "unexpected lastname");
  }

  @Test
  void testSetEmailWithNull() {
    User user = this.createBuilderWithRequiredValues().email(null).build();
    assertTrue(user.getEmail().isEmpty(), "email should be empty");
  }

  @Test
  void testSetEmailWithBlank() {
    User user = User.builder().login("test").email("  ").build();
    assertTrue(user.getEmail().isEmpty(), "email should be empty");
  }

  @Test
  void testSetRolesAsList() {
    User user =
        this.createBuilderWithRequiredValues().roles(Arrays.asList(Ref.ofId("123"))).build();
    assertEquals(Arrays.asList(Ref.ofId("123")), user.getRoles(), "unexpected roles");
  }

  @Test
  void testSetOverwriteRolesAsList() {
    User user =
        this.createBuilderWithRequiredValues().roles(Arrays.asList(Ref.ofId("123"))).build();
    User overwritter = user.toBuilder().roles(Arrays.asList(Ref.ofId("345"))).build();
    assertEquals(Arrays.asList(Ref.ofId("345")), overwritter.getRoles(), "unexpected roles");
  }

  @Test
  void testSetNullRolesAsList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().roles((List<Role>) null);
        });
  }

  @Test
  void testSetNullRoleInList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().roles(Arrays.asList(Ref.ofId("345"), null));
        });
  }

  @Test
  void testSetRolesAsVArgs() {
    User user = this.createBuilderWithRequiredValues().roles(Ref.ofId("123")).build();
    assertEquals(Arrays.asList(Ref.ofId("123")), user.getRoles(), "unexpected roles");
  }

  @Test
  void testSetOverwriteRolesAsVArgs() {
    User user = this.createBuilderWithRequiredValues().roles(Ref.ofId("123")).build();
    User overwritter = user.toBuilder().roles(Ref.ofId("345")).build();
    assertEquals(Arrays.asList(Ref.ofId("345")), overwritter.getRoles(), "unexpected roles");
  }

  @Test
  void testSetNullRolesAsVArgs() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().roles((Role[]) null);
        });
  }

  @Test
  void testSetNullRoleInVArgs() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().roles(Ref.ofId("345"), null);
        });
  }

  @Test
  void testSetRole() {
    User user = this.createBuilderWithRequiredValues().role(Ref.ofId("123")).build();
    assertEquals(Arrays.asList(Ref.ofId("123")), user.getRoles(), "unexpected roles");
  }

  @Test
  void testAddRole() {
    User user = this.createBuilderWithRequiredValues().role(Ref.ofId("123")).build();
    User added = user.toBuilder().role(Ref.ofId("456")).build();

    List<Role> expected = Arrays.asList(Ref.ofId("123"), Ref.ofId("456"));

    assertEquals(expected, added.getRoles(), "unexpected roles");
  }

  @Test
  void testSetNullRole() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().role(null);
        });
  }

  @Test
  void testSetIdentitiesAsList() {
    User user =
        this.createBuilderWithRequiredValues().identities(Arrays.asList(TEST_IDENTITY)).build();
    assertEquals(Arrays.asList(TEST_IDENTITY), user.getIdentities(), "unexpected identities");
  }

  @Test
  void testSetOverwriteIdentitiesAsList() {
    User user =
        this.createBuilderWithRequiredValues().identities(Arrays.asList(TEST_IDENTITY)).build();
    Identity newIdentity = LdapIdentity.builder().server(3).dn("userdn2").build();
    User overwritter = user.toBuilder().identities(Arrays.asList(newIdentity)).build();
    assertEquals(Arrays.asList(newIdentity), overwritter.getIdentities(), "unexpected identities");
  }

  @Test
  void testSetNullIdentitiesAsList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().identities((List<Identity>) null);
        });
  }

  @Test
  void testSetNullIdentityInList() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().identities(Arrays.asList(TEST_IDENTITY, null));
        });
  }

  @Test
  void testSetIdentitiesAsVArgs() {
    User user = this.createBuilderWithRequiredValues().identities(TEST_IDENTITY).build();
    assertEquals(Arrays.asList(TEST_IDENTITY), user.getIdentities(), "unexpected identities");
  }

  @Test
  void testSetOverwriteIdentitiesAsVArgs() {
    User user = this.createBuilderWithRequiredValues().identities(TEST_IDENTITY).build();
    Identity newIdentity = LdapIdentity.builder().server(3).dn("userdn2").build();
    User overwritter = user.toBuilder().identities(newIdentity).build();
    assertEquals(Arrays.asList(newIdentity), overwritter.getIdentities(), "unexpected identities");
  }

  @Test
  void testSetNullIdentitiesAsVArgs() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().identities((Identity[]) null);
        });
  }

  @Test
  void testSetNullIdentityInVArgs() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().identities(TEST_IDENTITY, null);
        });
  }

  @Test
  void testSetIndentity() {
    User user = this.createBuilderWithRequiredValues().identity(TEST_IDENTITY).build();
    assertEquals(Arrays.asList(TEST_IDENTITY), user.getIdentities(), "unexpected identities");
  }

  @Test
  void testAddIdentity() {
    User user = this.createBuilderWithRequiredValues().identity(TEST_IDENTITY).build();

    Identity newIdentity = LdapIdentity.builder().server(3).dn("userdn2").build();

    User added = user.toBuilder().identity(newIdentity).build();

    List<Identity> expected = Arrays.asList(TEST_IDENTITY, newIdentity);

    assertEquals(expected, added.getIdentities(), "unexpected roles");
  }

  @Test
  void testSetNullIdentity() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().identity(null);
        });
  }

  @Test
  void testGetIdentity() {
    Identity otherIdentity = mock(Identity.class);
    User user =
        this.createBuilderWithRequiredValues()
            .identity(otherIdentity)
            .identity(TEST_IDENTITY)
            .build();
    Optional<LdapIdentity> identity = user.getIdentity(LdapIdentity.class);
    assertEquals(TEST_IDENTITY, identity.get(), "unexpected identity");
  }

  @Test
  void testGetUnknownIdentity() {
    User user = this.createBuilderWithRequiredValues().build();
    Optional<LdapIdentity> identity = user.getIdentity(LdapIdentity.class);
    assertTrue(identity.isEmpty(), "identity should be empty");
  }

  @Test
  void testDefaultGender() {
    User user = this.createBuilderWithRequiredValues().build();
    assertEquals(GenderType.UNKNOWN, user.getGender(), "unexpected gender");
  }

  @Test
  void testSetGender() {
    User user = this.createBuilderWithRequiredValues().gender(GenderType.MALE).build();
    assertEquals(GenderType.MALE, user.getGender(), "unexpected gender");
  }

  @Test
  void testSetGenderWithNull() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().gender(null);
        });
  }

  @Test
  void testSetValiditiy() {

    User user =
        this.createBuilderWithRequiredValues()
            .validity(UserValidity.builder().blocked(true).build())
            .build();

    assertTrue(user.getValidity().isBlocked(), "user should be blocked");
  }

  @Test
  void testSetValiditiyBuilder() {

    User user =
        this.createBuilderWithRequiredValues()
            .validity(UserValidity.builder().blocked(true))
            .build();

    assertTrue(user.getValidity().isBlocked(), "user should be blocked");
  }

  @Test
  void testSetNullValiditiy() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().validity((UserValidity) null);
        });
  }

  @Test
  void testSetNullValiditiyBuilder() {
    assertThrows(
        NullPointerException.class,
        () -> {
          User.builder().validity((UserValidity.Builder) null);
        });
  }

  @Test
  void testToString() {
    User user =
        User.builder()
            .id("100560100000014842")
            .anchor("user.peterpan")
            .firstName("Peter")
            .lastName("Pan")
            .email("peter.pan@nimmer.land")
            .gender(GenderType.MALE)
            .login("peterpan")
            .roles(UserLevelRoles.USER, Ref.ofAnchor("test.anchor"), Ref.ofId("123"))
            .identity(TEST_IDENTITY)
            .description("a note")
            .build();
    String expected =
        """
				User [id=100560100000014842, anchor=user.peterpan, login=peterpan,\s\
				password=null, firstname=Peter, lastname=Pan, email=peter.pan@nimmer.land, gender=MALE,\s\
				note=a note, validity=UserValidity [blocked=false, validFrom=null, validTo=null],\s\
				identities=[LdapIdentity [server=2, dn=userdn]],\s\
				roles=[USER, test.anchor, 123]]""";
    assertEquals(expected, user.toString(), "unexpected string representation");
  }

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    User user =
        User.builder()
            .id("100560100000014842")
            .anchor("user.peterpan")
            .firstName("Peter")
            .lastName("Pan")
            .email("peter.pan@nimmer.land")
            .gender(GenderType.MALE)
            .login("peterpan")
            .roles(UserLevelRoles.USER, Ref.ofAnchor("test.anchor"), Ref.ofId("123"))
            .identity(TEST_IDENTITY)
            .description("a note")
            .build();

    String json = mapper.writeValueAsString(user);

    String expected =
        """
        {"id":"100560100000014842",\
        "anchor":"user.peterpan",\
        "login":"peterpan",\
        "firstName":"Peter",\
        "lastName":"Pan",\
        "email":"peter.pan@nimmer.land",\
        "gender":"MALE",\
        "description":"a note",\
        "validity":{"blocked":false},\
        "identities":[{"@type":"ldap","server":2,"dn":"userdn"}],\
        "roles":["USER","test.anchor","123"]}\
        """;

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    SimpleModule module = new SimpleModule();
    mapper.registerModule(module);

    String json =
        """
			{\
			"id":100560100000014842,\
			"anchor":"user.peterpan",\
			"login":"peterpan",\
			"firstName":"Peter",\
			"lastName":"Pan",\
			"email":"peter.pan@nimmer.land",\
			"gender":"MALE",\
			"description":"a note",\
			"identities":[{"@type":"ldap","server":2,"dn":"userdn"}],\
			"roles":["USER","test.anchor","123"]\
			}""";

    User user = mapper.readValue(json, User.class);

    User expected =
        User.builder()
            .id("100560100000014842")
            .anchor("user.peterpan")
            .firstName("Peter")
            .lastName("Pan")
            .email("peter.pan@nimmer.land")
            .gender(GenderType.MALE)
            .login("peterpan")
            .roles(UserLevelRoles.USER, Ref.ofAnchor("test.anchor"), Ref.ofId("123"))
            .identity(TEST_IDENTITY)
            .description("a note")
            .build();

    assertEquals(expected, user, "unexpected user");
  }

  private User.Builder createBuilderWithRequiredValues() {
    return User.builder().login("test");
  }
}
