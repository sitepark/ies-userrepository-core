package com.sitepark.ies.userrepository.core.domain.databind;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UniquePropertyPolymorphicDeserializerTest {

  @Test
  @SuppressWarnings("PMD.CloseResource")
  void test() throws IOException {

    UniquePropertyPolymorphicDeserializer<A> deserializer =
        new UniquePropertyPolymorphicDeserializer<>(A.class);

    deserializer.register("b", B.class);
    deserializer.register("c", C.class);

    JsonParser jp = mock();
    DeserializationContext context = mock();
    ObjectMapper mapper = mock();
    ObjectNode obj = mock();

    Entry<String, JsonNode> element = mock();

    Set<Entry<String, JsonNode>> properties = Collections.singleton(element);

    when(mapper.readTree(any(JsonParser.class))).thenReturn(obj);
    when(jp.getCodec()).thenReturn(mapper);
    when(obj.properties()).thenReturn(properties);
    when(element.getKey()).thenReturn("c");

    deserializer.deserialize(jp, context);

    verify(mapper).treeToValue(obj, C.class);
  }

  @Test
  @SuppressWarnings("PMD.CloseResource")
  void testNotRegistered() throws IOException {

    UniquePropertyPolymorphicDeserializer<A> deserializer =
        new UniquePropertyPolymorphicDeserializer<>(A.class);

    deserializer.register("b", B.class);
    deserializer.register("c", C.class);

    JsonParser jp = mock();
    DeserializationContext context = mock();
    ObjectMapper mapper = mock();
    ObjectNode obj = mock();

    Entry<String, JsonNode> element = mock();

    Set<Entry<String, JsonNode>> properties = Collections.singleton(element);

    when(mapper.readTree(any(JsonParser.class))).thenReturn(obj);
    when(jp.getCodec()).thenReturn(mapper);
    when(obj.properties()).thenReturn(properties);
    when(element.getKey()).thenReturn("d");

    deserializer.deserialize(jp, context);

    verify(context)
        .reportInputMismatch(any(UniquePropertyPolymorphicDeserializer.class), any(String.class));
  }

  private static class A {}

  private static final class B extends A {}

  private static final class C extends A {}
}
