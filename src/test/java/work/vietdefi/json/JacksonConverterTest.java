package work.vietdefi.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JacksonConverter class, which implements the IJacksonConverter interface.
 */
public class JacksonConverterTest {

    private IJacksonConverter jacksonConverter;

    @BeforeEach
    public void setUp() {
        jacksonConverter = new JacksonConverter();
    }

    // Test object for serialization and deserialization
    public static class TestObject {
        public String name;
        public int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }

        // Default constructor for deserialization
        public TestObject() {}

        // Override equals to verify object values
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return age == that.age && ((name == null && that.name == null) || (name != null && name.equals(that.name)));
        }
    }

    @Test
    public void testToJsonString() {
        TestObject obj = new TestObject("Sunny", 7);
        String jsonString = jacksonConverter.toJsonString(obj);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonString);
    }

    @Test
    public void testFromJsonString() {
        String jsonString = "{\"name\":\"Sunny\",\"age\":7}";
        TestObject obj = jacksonConverter.fromJsonString(jsonString, TestObject.class);
        assertEquals(new TestObject("Sunny", 7), obj);
    }

    @Test
    public void testToJsonElementFromObject() {
        TestObject obj = new TestObject("Sunny", 7);
        JsonNode jsonNode = jacksonConverter.toJsonElement(obj);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonNode.toString());
    }

    @Test
    public void testToJsonElementFromString() {
        String jsonString = "{\"name\":\"Sunny\",\"age\":7}";
        JsonNode jsonNode = jacksonConverter.toJsonElement(jsonString);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonNode.toString());
    }

    @Test
    public void testFromJsonElementToString() {
        JsonNode jsonNode = jacksonConverter.toJsonElement("{\"name\":\"Sunny\",\"age\":7}");
        String jsonString = jacksonConverter.fromJsonElementToString(jsonNode);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonString);
    }

    @Test
    public void testFromJsonElementToObject() {
        JsonNode jsonNode = jacksonConverter.toJsonElement("{\"name\":\"Sunny\",\"age\":7}");
        TestObject obj = jacksonConverter.fromJsonElement(jsonNode, TestObject.class);
        assertEquals(new TestObject("Sunny", 7), obj);
    }
}
