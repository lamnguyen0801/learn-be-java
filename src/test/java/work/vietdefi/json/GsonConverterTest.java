package work.vietdefi.json;


import com.google.gson.JsonElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the GsonConverter class, which implements the IGsonConverter interface.
 */
public class GsonConverterTest {


    private IGsonConverter gsonConverter;


    @BeforeEach
    public void setUp() {
        gsonConverter = new GsonConverter();
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
            return age == that.age && name.equals(that.name);
        }
    }


    @Test
    public void testToJsonString() {
        TestObject obj = new TestObject("Sunny", 7);
        String jsonString = gsonConverter.toJsonString(obj);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonString);
    }


    @Test
    public void testFromJsonString() {
        String jsonString = "{\"name\":\"Sunny\",\"age\":7}";
        TestObject obj = gsonConverter.fromJsonString(jsonString, TestObject.class);
        assertEquals(new TestObject("Sunny", 7), obj);
    }


    @Test
    public void testToJsonElementFromObject() {
        TestObject obj = new TestObject("Sunny", 7);
        JsonElement jsonElement = gsonConverter.toJsonElement(obj);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonElement.toString());
    }


    @Test
    public void testToJsonElementFromString() {
        String jsonString = "{\"name\":\"Sunny\",\"age\":7}";
        JsonElement jsonElement = gsonConverter.toJsonElement(jsonString);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonElement.toString());
    }


    @Test
    public void testFromJsonElementToString() {
        JsonElement jsonElement = gsonConverter.toJsonElement("{\"name\":\"Sunny\",\"age\":7}");
        String jsonString = gsonConverter.fromJsonElementToString(jsonElement);
        assertEquals("{\"name\":\"Sunny\",\"age\":7}", jsonString);
    }


    @Test
    public void testFromJsonElementToObject() {
        JsonElement jsonElement = gsonConverter.toJsonElement("{\"name\":\"Sunny\",\"age\":7}");
        TestObject obj = gsonConverter.fromJsonElement(jsonElement, TestObject.class);
        assertEquals(new TestObject("Sunny", 7), obj);
    }
}
