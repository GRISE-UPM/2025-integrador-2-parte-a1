package es.upm.grise.profundizacion.file;
import Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;



import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileTest {

    private File propertyFile;

    @BeforeEach
    void setUp() {
        propertyFile = new File(FileType.PROPERTY); // suponiendo que tienes un enum FileType
    }

    @Test
    void testAddPropertyValid() throws InvalidContentException, WrongFileTypeException {
        char[] prop = "DATE=20251210".toCharArray();
        propertyFile.addProperty(prop);
        assertFalse(propertyFile.getContent().isEmpty());
    }

    @Test
    void testAddPropertyNullThrowsException() {
        assertThrows(InvalidContentException.class, () -> propertyFile.addProperty(null));
    }

    @Test
    void testAddPropertyOnImageThrowsException() {
        File imageFile = new File(FileType.IMAGE);
        char[] prop = "DATE=20251210".toCharArray();
        assertThrows(WrongFileTypeException.class, () -> imageFile.addProperty(prop));
    }

    @Test
    void testGetCRC32EmptyContentReturnsZero() {
        assertEquals(0L, propertyFile.getCRC32());
    }

    @Test
    void testGetCRC32DelegatesToFileUtils() throws InvalidContentException, WrongFileTypeException {
        char[] prop = "KEY=VALUE".toCharArray();
        propertyFile.addProperty(prop);

        // Convertimos a bytes como lo haría la clase
        byte[] bytes = new byte[prop.length];
        for (int i = 0; i < prop.length; i++) {
            bytes[i] = (byte) (prop[i] & 0xFF);
        }

        try (MockedStatic<FileUtils> mocked = Mockito.mockStatic(FileUtils.class)) {
            mocked.when(() -> FileUtils.calculateCRC32(bytes)).thenReturn(12345L);

            long result = propertyFile.getCRC32();
            assertEquals(12345L, result);

            // Verificamos que se llamó correctamente
            mocked.verify(() -> FileUtils.calculateCRC32(bytes));
        }
    }
}
