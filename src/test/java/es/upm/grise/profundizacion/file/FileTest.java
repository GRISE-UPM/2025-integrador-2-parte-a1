package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class FileTest {

    private File file;

    @BeforeEach
    void setUp() {
        file = new File();
        file.setType(FileType.PROPERTY);
    }

    @Test
    void addProperty_nullContent_throwsInvalidContentException() {
        assertThrows(InvalidContentException.class, () -> file.addProperty(null));
    }

    @Test
    void addProperty_imageType_throwsWrongFileTypeException() {
        file.setType(FileType.IMAGE);
        assertThrows(WrongFileTypeException.class, () -> file.addProperty("KEY=VALUE".toCharArray()));
    }

    @Test
    void addProperty_validContent_appendsCharacters() throws WrongFileTypeException, InvalidContentException {
        char[] property = "DATE=20250919".toCharArray();
        file.addProperty(property);
        assertEquals(property.length, file.getContent().size());
    }

    @Test
    void getCRC32_emptyContent_returnsZero() {
        assertEquals(0L, file.getCRC32());
    }

    @Test
    void getCRC32_nonEmptyContent_returnsValueFromFileUtils() throws WrongFileTypeException, InvalidContentException {
        char[] property = "KEY=VALUE".toCharArray();
        file.addProperty(property);

        byte[] expectedBytes = new byte[property.length];
        for (int i = 0; i < property.length; i++) {
            expectedBytes[i] = (byte) (property[i] & 0x00FF);
        }

        long crc = 123456789L;
        try (MockedStatic<FileUtils> mocked = Mockito.mockStatic(FileUtils.class)) {
            mocked.when(() -> FileUtils.calculateCRC32(expectedBytes)).thenReturn(crc);
            assertEquals(crc, file.getCRC32());
            mocked.verify(() -> FileUtils.calculateCRC32(expectedBytes));
        }
    }
}
