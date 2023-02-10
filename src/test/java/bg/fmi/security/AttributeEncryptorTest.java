package bg.fmi.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AttributeEncryptorTest {

    private AttributeEncryptor attributeEncryptor = new AttributeEncryptor("secret-key-12345");

    public AttributeEncryptorTest() throws Exception {
    }

    @Test
    public void testConvertToDatabaseColumn() throws Exception {
        String attribute = "secret-key-12345";
        String expected = "T688HeMaG9gm0kr10nAkNGCSKB1IvY6YJ4GfhsogKck=";
        String result = attributeEncryptor.convertToDatabaseColumn(attribute);

        assertEquals(expected, result);
    }

    @Test
    public void testConvertToEntityAttribute() throws Exception {
        String dbData = "T688HeMaG9gm0kr10nAkNGCSKB1IvY6YJ4GfhsogKck=";
        String expected = "secret-key-12345";
        String result = attributeEncryptor.convertToEntityAttribute(dbData);

        assertEquals(expected, result);
    }
}