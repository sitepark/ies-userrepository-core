package com.sitepark.ies.userrepository.core.domain.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.sitepark.ies.sharedkernel.anchor.Anchor;
import com.sitepark.ies.sharedkernel.base.Identifier;
import com.sitepark.ies.userrepository.core.domain.value.Address;
import com.sitepark.ies.userrepository.core.domain.value.Contact;
import com.sitepark.ies.userrepository.core.domain.value.GenderType;
import com.sitepark.ies.userrepository.core.domain.value.Identity;
import com.sitepark.ies.userrepository.core.domain.value.Organisation;
import com.sitepark.ies.userrepository.core.domain.value.UserValidity;
import com.sitepark.ies.userrepository.core.domain.value.identity.LdapIdentity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.util.List;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({
  "PI_DO_NOT_REUSE_PUBLIC_IDENTIFIERS_CLASS_NAMES",
  "NP_NULL_PARAM_DEREF_NONVIRTUAL",
  "NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"
})
class UserTest {

  private static final Identity TEST_IDENTITY =
      LdapIdentity.builder().serverId("2").dn("userdn").build();

  @Test
  void testEquals() {
    EqualsVerifier.forClass(User.class).verify();
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(User.class).verify();
  }

  @Test
  void testBuildWithoutLogin() {
    assertThrows(IllegalStateException.class, () -> User.builder().build());
  }

  @Test
  void testSetLogin() {
    User user = User.builder().login("peterpan").build();
    assertEquals("peterpan", user.login(), "unexpected login");
  }

  @Test
  void testSetIdentifierWithId() {
    Identifier id = Identifier.ofId("123");
    User user = this.createBuilderWithRequiredValues().identifier(id).build();
    assertEquals("123", user.id(), "unexpected id");
  }

  @Test
  void testSetIdentifierWithAnchor() {
    Identifier anchor = Identifier.ofAnchor(Anchor.ofString("abc"));
    User user = this.createBuilderWithRequiredValues().identifier(anchor).build();
    assertEquals("abc", user.anchor().getName(), "unexpected anchor");
  }

  @Test
  void testSetId() {
    User user = this.createBuilderWithRequiredValues().id("123").build();
    assertEquals("123", user.id(), "unexpected id");
  }

  @Test
  void testGetEmptyId() {
    User user = this.createBuilderWithRequiredValues().build();
    assertNull(user.id(), "id should be null");
  }

  @Test
  void testGetIdentifierWithId() {
    User user = this.createBuilderWithRequiredValues().id("123").build();
    assertEquals(Identifier.ofId("123"), user.toIdentifier(), "unexpected identifier");
  }

  @Test
  void testGetIdentifierWithAnchor() {
    User user = this.createBuilderWithRequiredValues().anchor("abc").build();
    assertEquals(
        Identifier.ofAnchor(Anchor.ofString("abc")), user.toIdentifier(), "unexpected identifier");
  }

  @Test
  void testGetEmptyIdentifier() {
    User user = this.createBuilderWithRequiredValues().build();
    assertNull(user.toIdentifier(), "identifier should be null");
  }

  @Test
  void testSetIdWithZero() {
    assertThrows(
        IllegalArgumentException.class, () -> User.builder().id("0"), "id 0 should't be allowed");
  }

  @Test
  void testSetIdWithNull() {
    assertThrows(
        NullPointerException.class, () -> User.builder().id(null), "null should't be allowed");
  }

  @Test
  void testSeIdWithInvalidValue() {
    assertThrows(
        IllegalArgumentException.class,
        () -> User.builder().id("0x"),
        "invalid id should't be allowed");
  }

  @Test
  void testSetAnchor() {
    Anchor anchor = Anchor.ofString("user.pan.peter");
    User user = this.createBuilderWithRequiredValues().anchor(anchor).build();
    assertEquals("user.pan.peter", user.anchor().getName(), "unexpected anchor");
  }

  @Test
  void testSetAnchorString() {
    User user = this.createBuilderWithRequiredValues().anchor("user.pan.peter").build();
    assertEquals("user.pan.peter", user.anchor().getName(), "unexpected anchor");
  }

  @Test
  @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
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

    assertEquals("100560100000014842", changedUser.id(), "unexpected id");
    assertEquals(Anchor.ofString("user.peterpan"), changedUser.anchor(), "unexpected anchor");
    assertEquals("Peter", changedUser.firstName(), "unexpected firstname");
    assertEquals("Pan", changedUser.lastName(), "unexpected lastname");
    assertEquals("peterpan", changedUser.login(), "unexpected login");
    assertEquals("Test", changedUser.description(), "unexpected note");
  }

  @Test
  void testSetTitle() {
    User user = this.createBuilderWithRequiredValues().title("Dr.").build();
    assertEquals("Dr.", user.title(), "unexpected title");
  }

  @Test
  void testSetFirstname() {
    User user = this.createBuilderWithRequiredValues().firstName("Peter").build();
    assertEquals("Peter", user.firstName(), "unexpected firstname");
  }

  @Test
  void testSetFirstnameWithNull() {
    User user = this.createBuilderWithRequiredValues().firstName(null).build();
    assertNull(user.firstName(), "firstname should be null");
  }

  @Test
  void testSetFirstnameWithBlank() {
    User user = this.createBuilderWithRequiredValues().firstName("  ").build();
    assertNull(user.firstName(), "firstname should be null");
  }

  @Test
  void testSetLastname() {
    User user = this.createBuilderWithRequiredValues().lastName("Pan").build();
    assertEquals("Pan", user.lastName(), "unexpected lastname");
  }

  @Test
  void testSetLastnameWithNull() {
    User user = this.createBuilderWithRequiredValues().lastName(null).build();
    assertNull(user.lastName(), "lastname should be null");
  }

  @Test
  void testSetLastnameWithBlank() {
    User user = this.createBuilderWithRequiredValues().lastName("  ").build();
    assertNull(user.lastName(), "lastname should be null");
  }

  @Test
  void testGetNameOnlyLastname() {
    User user = this.createBuilderWithRequiredValues().lastName("Pan").build();
    assertEquals("Pan", user.toDisplayName(), "unexpected name");
  }

  @Test
  void testGetNameOnlyFirstname() {
    User user = this.createBuilderWithRequiredValues().firstName("Peter").build();
    assertEquals("Peter", user.toDisplayName(), "unexpected name");
  }

  @Test
  void testGetNameFirstnameAndLastName() {
    User user = this.createBuilderWithRequiredValues().lastName("Pan").firstName("Peter").build();
    assertEquals("Pan, Peter", user.toDisplayName(), "unexpected name");
  }

  @Test
  void testSetEmail() {
    User user = this.createBuilderWithRequiredValues().email("peter.pan@nimmer.land").build();
    assertEquals("peter.pan@nimmer.land", user.email(), "unexpected lastname");
  }

  @Test
  void testSetEmailWithNull() {
    User user = this.createBuilderWithRequiredValues().email(null).build();
    assertNull(user.email(), "email should be null");
  }

  @Test
  void testSetEmailWithBlank() {
    User user = User.builder().login("test").email("  ").build();
    assertNull(user.email(), "email should be null");
  }

  @Test
  void testSetIdentitiesAsList() {
    User user = this.createBuilderWithRequiredValues().identities(List.of(TEST_IDENTITY)).build();
    assertEquals(List.of(TEST_IDENTITY), user.identities(), "unexpected identities");
  }

  @Test
  void testSetIdentitiesWithConsumer() {
    User user =
        this.createBuilderWithRequiredValues().identities(b -> b.add(TEST_IDENTITY)).build();
    assertEquals(List.of(TEST_IDENTITY), user.identities(), "unexpected identities");
  }

  @Test
  void testGetIdentity() {
    Identity otherIdentity = mock(Identity.class);
    User user =
        this.createBuilderWithRequiredValues()
            .identities(b -> b.add(otherIdentity).add(TEST_IDENTITY))
            .build();
    LdapIdentity identity = user.getIdentity(LdapIdentity.class);
    assertEquals(TEST_IDENTITY, identity, "unexpected identity");
  }

  @Test
  void testGetUnknownIdentity() {
    User user = this.createBuilderWithRequiredValues().build();
    LdapIdentity identity = user.getIdentity(LdapIdentity.class);
    assertNull(identity, "identity should be empty");
  }

  @Test
  void testDefaultGender() {
    User user = this.createBuilderWithRequiredValues().build();
    assertEquals(GenderType.UNKNOWN, user.gender(), "unexpected gender");
  }

  @Test
  void testSetGender() {
    User user = this.createBuilderWithRequiredValues().gender(GenderType.MALE).build();
    assertEquals(GenderType.MALE, user.gender(), "unexpected gender");
  }

  @Test
  void testSetGenderWithNull() {
    assertThrows(NullPointerException.class, () -> User.builder().gender(null));
  }

  @Test
  void testSetCreatedAt() {
    Instant now = Instant.now();
    User user = this.createBuilderWithRequiredValues().createdAt(now).build();
    assertEquals(now, user.createdAt(), "unexpected createdAt");
  }

  @Test
  void testSetChangedAt() {
    Instant now = Instant.now();
    User user = this.createBuilderWithRequiredValues().changedAt(now).build();
    assertEquals(now, user.changedAt(), "unexpected changedAt");
  }

  @Test
  void testSetValiditiy() {

    User user =
        this.createBuilderWithRequiredValues()
            .validity(UserValidity.builder().blocked(true).build())
            .build();

    assertTrue(user.validity().blocked(), "user should be blocked");
  }

  @Test
  void testSetValiditiyBuilder() {

    User user =
        this.createBuilderWithRequiredValues()
            .validity(UserValidity.builder().blocked(true))
            .build();

    assertTrue(user.validity().blocked(), "user should be blocked");
  }

  @Test
  void testSetNullValiditiy() {
    assertThrows(NullPointerException.class, () -> User.builder().validity((UserValidity) null));
  }

  @Test
  void testSetNullValiditiyBuilder() {
    assertThrows(
        NullPointerException.class, () -> User.builder().validity((UserValidity.Builder) null));
  }

  @Test
  void testSetAddress() {
    Address address = Address.builder().street("Musterstraße").city("Berlin").build();
    User user = this.createBuilderWithRequiredValues().address(address).build();
    assertEquals(address, user.address(), "unexpected address");
  }

  @Test
  void testSetAddressWithNull() {
    User user = this.createBuilderWithRequiredValues().address(null).build();
    assertNull(user.address(), "address should be null");
  }

  @Test
  void testSetContact() {
    Contact contact =
        Contact.builder().phonePrivate("0123456789").phoneOffice("0987654321").build();
    User user = this.createBuilderWithRequiredValues().contact(contact).build();
    assertEquals(contact, user.contact(), "unexpected contact");
  }

  @Test
  void testSetContactWithNull() {
    User user = this.createBuilderWithRequiredValues().contact(null).build();
    assertNull(user.contact(), "contact should be null");
  }

  @Test
  void testSetOrganisation() {
    Organisation organisation = Organisation.builder().name("ACME Corp").build();
    User user = this.createBuilderWithRequiredValues().organisation(organisation).build();
    assertEquals(organisation, user.organisation(), "unexpected organisation");
  }

  @Test
  void testSetOrganisationWithNull() {
    User user = this.createBuilderWithRequiredValues().organisation(null).build();
    assertNull(user.organisation(), "organisation should be null");
  }

  @Test
  void testSerialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY);

    User user =
        User.builder()
            .id("100560100000014842")
            .anchor("user.peterpan")
            .firstName("Peter")
            .lastName("Pan")
            .email("peter.pan@nimmer.land")
            .gender(GenderType.MALE)
            .login("peterpan")
            .identities(b -> b.add(TEST_IDENTITY))
            .description("a note")
            .build();

    String json = mapper.writeValueAsString(user);

    String expected =
        """
        {"id":"100560100000014842","anchor":"user.peterpan","firstName":"Peter","lastName":"Pan","email":"peter.pan@nimmer.land","gender":"MALE","description":"a note","login":"peterpan","identities":[{"serverId":"2","dn":"userdn","type":"ldap"}],"validity":{"blocked":false}}\
        """;

    assertEquals(expected, json, "unexpected json");
  }

  @Test
  void testDeserialize() throws JsonProcessingException {

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());

    String json =
        """
        {"id":"100560100000014842","anchor":"user.peterpan","firstName":"Peter","lastName":"Pan","email":"peter.pan@nimmer.land","gender":"MALE","description":"a note","login":"peterpan","identities":[{"serverId":"2","dn":"userdn","type":"ldap"}],"validity":{"blocked":false}}\
        """;

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
            .identities(b -> b.add(TEST_IDENTITY))
            .description("a note")
            .build();

    assertEquals(expected, user, "unexpected user");
  }

  private User.Builder createBuilderWithRequiredValues() {
    return User.builder().login("test");
  }
}
