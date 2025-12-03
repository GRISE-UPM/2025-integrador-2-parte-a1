package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

public class FileTest {

    @Test
    void testAddProperty_isCalledWithCorrectArguments() throws Exception {
        File file = mock(File.class);

        char[] data = {'A','B','C'};
        file.addProperty(data);

        verify(file, times(1)).addProperty(data);
    }

    @Test
    void testAddProperty_null_throwsInvalidContentException_mocked() throws Exception {
        File file = mock(File.class);

        doThrow(new InvalidContentException("Content cannot be null"))
                .when(file).addProperty(null);

        assertThrows(InvalidContentException.class, () -> file.addProperty(null));
    }

    @Test
    void testAddProperty_wrongType_throwsWrongFileTypeException_mocked() throws Exception {
        File file = mock(File.class);

        doThrow(new WrongFileTypeException("wrong type"))
                .when(file).addProperty(new char[]{'X'});

        assertThrows(WrongFileTypeException.class, () -> file.addProperty(new char[]{'X'}));
    }

    @Test
    void testGetCRC32_empty_returnsZero_mocked() {
        File file = mock(File.class);

        when(file.getCRC32()).thenReturn(0L);

        long result = file.getCRC32();
        assertEquals(0L, result);

        verify(file, times(1)).getCRC32();
    }

    @Test
    void testGetCRC32_nonEmpty_returnsMockedValue() {
        File file = mock(File.class);

        when(file.getCRC32()).thenReturn(12345L);

        long result = file.getCRC32();
        assertEquals(12345L, result);

        verify(file, times(1)).getCRC32();
    }
}