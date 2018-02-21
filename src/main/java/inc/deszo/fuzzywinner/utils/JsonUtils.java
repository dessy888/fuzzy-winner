package inc.deszo.fuzzywinner.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import javafx.util.Pair;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

public final class JsonUtils {

  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
      .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
      .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
      .registerModule(new ParameterNamesModule())
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());

  private JsonUtils() {
  }

  public static ObjectMapper getMAPPER() {
    return MAPPER.copy();
  }

  public static <T> String serialize(final T value) {
    try {
      return MAPPER.writeValueAsString(value);
    } catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> byte[] serializeBytes(final T value) {
    return serialize(value).getBytes();
  }

  public static <T> T deser(final byte[] bytes, final Class<T> valueType) throws IOException {
    return MAPPER.readValue(new String(bytes), valueType);
  }

  public static <T> T deser(final String content, final Class<T> valueType) throws IOException {
    return MAPPER.readValue(content, valueType);
  }

  public static <T> T deserialize(final String content, final Class<T> valueType) {
    try {
      return deser(content, valueType);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> Function<String, T> deserialize(final TypeReference<T> valueType) {
    return value -> {
      try {
        return MAPPER.readValue(value, valueType);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static <T> T deserialize(final String content) {
    try {
      return (T) MAPPER.readValue(content, Object.class);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getJsonSchema(final Class clazz) throws IOException {
    final ObjectMapper mapper = JsonUtils.getMAPPER();
    mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
    JsonSchema schema = mapper.generateJsonSchema(clazz);

    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
  }

  public static Document flattenDoc(Document document ){

    Document flattened = new Document();
    Queue<Pair<String, Document>> queue = new ArrayDeque<>();
    queue.add( new Pair<>( "", document ) );

    while( !queue.isEmpty() ){
      Pair<String, Document> pair = queue.poll();
      String key = pair.getKey();
      for( Map.Entry<String, Object> entry : pair.getValue().entrySet() ){
        if( entry.getValue() instanceof Document ){
          queue.add( new Pair<>( key + entry.getKey() + ".", ( Document ) entry.getValue() ) );

        }else{
          flattened.put( key + entry.getKey(), entry.getValue() );

        }
      }//end for
    }

    return flattened;
  }
}

