package work.vietdefi.util.json;

import work.vietdefi.json.GsonConverter;
import work.vietdefi.json.IGsonConverter;

/**
 * GsonUtil is a utility class that provides a static instance of
 * IGsonConverter for JSON conversion operations.
 * This class centralizes the Gson-related functionality, allowing
 * for easy access and reusability throughout the application.
 */
public class GsonUtil {

    // Static instance of IGsonConverter using GsonConverter implementation
    public static IGsonConverter gsonConverter = new GsonConverter();
}
