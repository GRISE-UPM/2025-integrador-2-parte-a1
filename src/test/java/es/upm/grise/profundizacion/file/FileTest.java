package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class FileTest {

    private File file;
    private FileUtils fileUtilsMock;

    @BeforeEach
    void setUp() {
        file = new File();
        fileUtilsMock = mock(FileUtils.class);
        // Inyectamos el mock usando reflexión (si FileUtils no tiene setter)
        try {
            java.lang.reflect.Field field = File.class.getDeclaredField("FileUtils");
            field.setAccessible(true);
            field.set(file, fileUtilsMock);
        } catch (Exception e) {
            fail("Error al inyectar el mock: " + e.getMessage());
        }
    }

    @Test
    void testAddPropertySuccess() throws Exception {
        file.setType(FileType.PROPERTY);
        char[] newContent = {'H', 'i'};
        file.addProperty(newContent);
        List<Character> content = file.getContent();
        assertEquals(2, content.size());
        assertEquals('H', content.get(0));
        assertEquals('i', content.get(1));
    }

    @Test
    void testAddPropertyThrowsInvalidContentException() {
        file.setType(FileType.PROPERTY);
        assertThrows(InvalidContentException.class, () -> file.addProperty(null));
    }

    @Test
    void testAddPropertyThrowsWrongFileTypeException() {
        file.setType(FileType.IMAGE);
        char[] newContent = {'H', 'i'};
        assertThrows(WrongFileTypeException.class, () -> file.addProperty(newContent));
    }

    @Test
    void testGetCRC32Success() {
        file.setType(FileType.PROPERTY);
        char[] newContent = {'A', 'B'};
        try {
            file.addProperty(newContent);
        } catch (Exception e) {
            fail("No debería lanzar excepción");
        }

        byte[] expectedBytes = {(byte) 'A', (byte) 'B'};
        when(fileUtilsMock.calculateCRC32(expectedBytes)).thenReturn(12345L);

        long crc = file.getCRC32();
        assertEquals(12345L, crc);
        verify(fileUtilsMock, times(1)).calculateCRC32(expectedBytes);
    }
}
