package kenneth.coursework.utils;

import kenneth.coursework.exceptions.IncorrectFormatException;

public interface Serializable {
    String serialize();

    static <T> T deserialize(String string) throws IncorrectFormatException {
        return null;
    }
}
