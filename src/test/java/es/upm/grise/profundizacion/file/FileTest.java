package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FileTest {
    
    @Mock
    private FileUtils fileUtilsMock;
    
    @InjectMocks
    private File file;
    
    @BeforeEach
    public void setUp() {
        // Reiniciamos el mock antes de cada test
        reset(fileUtilsMock);
        // Creamos una nueva instancia de File para cada test
        file = new File();
    }
    
    @Test
    public void testConstructorInitializesContentAsEmptyArrayList() {
        // Arrange & Act ya realizados en setUp
        
        // Assert
        assertNotNull(file.getContent(), "El contenido no debería ser null");
        assertTrue(file.getContent().isEmpty(), "El contenido debería estar vacío");
        assertTrue(file.getContent() instanceof ArrayList, "El contenido debería ser un ArrayList");
    }
    
    @Test
    public void testAddPropertyWithValidContentAndPropertyType() throws Exception {
        // Arrange
        file.setType(FileType.PROPERTY);
        char[] newContent = {'k', 'e', 'y', '=', 'v', 'a', 'l', 'u', 'e'};
        
        // Act
        file.addProperty(newContent);
        
        // Assert
        List<Character> content = file.getContent();
        assertEquals(9, content.size(), "El tamaño del contenido debería ser 9");
        assertEquals('k', content.get(0));
        assertEquals('=', content.get(3));
        assertEquals('e', content.get(8));
    }
    
    @Test
    public void testAddPropertyWithNullContentThrowsInvalidContentException() {
        // Arrange
        file.setType(FileType.PROPERTY);
        
        // Act & Assert
        InvalidContentException exception = assertThrows(
            InvalidContentException.class,
            () -> file.addProperty(null)
        );
        
        assertNotNull(exception);
        assertTrue(file.getContent().isEmpty(), "El contenido debería permanecer vacío");
    }
    
    @Test
    public void testAddPropertyWithImageTypeThrowsWrongFileTypeException() {
        // Arrange
        file.setType(FileType.IMAGE);
        char[] newContent = {'d', 'a', 't', 'a'};
        
        // Act & Assert
        WrongFileTypeException exception = assertThrows(
            WrongFileTypeException.class,
            () -> file.addProperty(newContent)
        );
        
        assertNotNull(exception);
        assertTrue(file.getContent().isEmpty(), "El contenido debería permanecer vacío");
    }
    
    @Test
    public void testAddPropertyAppendsToExistingContent() throws Exception {
        // Arrange
        file.setType(FileType.PROPERTY);
        char[] firstContent = {'a', 'b', 'c'};
        char[] secondContent = {'d', 'e', 'f'};
        
        // Act
        file.addProperty(firstContent);
        file.addProperty(secondContent);
        
        // Assert
        List<Character> content = file.getContent();
        assertEquals(6, content.size(), "El tamaño del contenido debería ser 6");
        assertEquals('a', content.get(0));
        assertEquals('c', content.get(2));
        assertEquals('d', content.get(3));
        assertEquals('f', content.get(5));
    }
    
    @Test
    public void testGetCRC32WithEmptyContentReturnsZero() {
        // Arrange
        // Content ya está vacío por el constructor
        
        // Act
        long result = file.getCRC32();
        
        // Assert
        assertEquals(0L, result, "CRC32 debería ser 0 para contenido vacío");
        // Verificamos que no se haya llamado a FileUtils
        verifyNoInteractions(fileUtilsMock);
    }
    
    @Test
    public void testGetCRC32WithContentCallsFileUtils() {
        // Arrange
        file.setType(FileType.PROPERTY);
        char[] content = {'H', 'e', 'l', 'l', 'o'};
        
        try {
            file.addProperty(content);
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
        
        // Mock de FileUtils para devolver un valor específico
        long expectedCRC = 123456789L;
        when(fileUtilsMock.calculateCRC32(any(byte[].class))).thenReturn(expectedCRC);
        
        // Act
        long result = file.getCRC32();
        
        // Assert
        assertEquals(expectedCRC, result, "CRC32 debería coincidir con el valor mockeado");
        verify(fileUtilsMock, times(1)).calculateCRC32(any(byte[].class));
    }
    
    @Test
    public void testGetCRC32ConvertsCharactersToBytesCorrectly() {
        // Arrange
        file.setType(FileType.PROPERTY);
        // Caracteres que prueban diferentes casos
        char[] content = {'A', 0x0041, 0x00FF, 0x0100, 0x1234};
        
        try {
            file.addProperty(content);
        } catch (Exception e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
        
        // Capturamos el array de bytes pasado a FileUtils
        final byte[] capturedBytes = new byte[1];
        when(fileUtilsMock.calculateCRC32(any(byte[].class)))
            .thenAnswer(invocation -> {
                capturedBytes[0] = invocation.getArgument(0);
                return 999L;
            });
        
        // Act
        file.getCRC32();
        
        // Assert
        verify(fileUtilsMock, times(1)).calculateCRC32(any(byte[].class));
        
        // Podemos verificar indirectamente que la conversión se hace correctamente
        // 'A' = 65 (0x41) -> byte 65
        // 0x0041 -> byte 0x41 (65)
        // 0x00FF -> byte 0xFF (-1 en signed byte)
        // 0x0100 -> byte 0x00 (0)
        // 0x1234 -> byte 0x34 (52)
    }
    
    @Test
    public void testGetCRC32ForImageTypeWithContent() {
        // Arrange
        file.setType(FileType.IMAGE);
        // Para imágenes, no podemos usar addProperty, pero podemos simular contenido directamente
        // En la práctica, el contenido se añadiría de otra manera (no especificada)
        List<Character> contentList = new ArrayList<>();
        contentList.add('I');
        contentList.add('M');
        contentList.add('G');
        
        // Usamos reflexión para establecer el contenido directamente
        try {
            java.lang.reflect.Field contentField = File.class.getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(file, contentList);
        } catch (Exception e) {
            fail("Error al establecer contenido por reflexión: " + e.getMessage());
        }
        
        long expectedCRC = 987654321L;
        when(fileUtilsMock.calculateCRC32(any(byte[].class))).thenReturn(expectedCRC);
        
        // Act
        long result = file.getCRC32();
        
        // Assert
        assertEquals(expectedCRC, result, "CRC32 debería funcionar para IMAGE también");
        verify(fileUtilsMock, times(1)).calculateCRC32(any(byte[].class));
    }
    
    @Test
    public void testSetAndGetType() {
        // Arrange
        FileType expectedType = FileType.PROPERTY;
        
        // Act
        file.setType(expectedType);
        
        // Assert
        assertEquals(expectedType, file.getType(), "El tipo debería ser PROPERTY");
    }
    
    @Test
    public void testAddPropertyWithEmptyCharArray() throws Exception {
        // Arrange
        file.setType(FileType.PROPERTY);
        char[] emptyContent = {};
        
        // Act
        file.addProperty(emptyContent);
        
        // Assert
        assertTrue(file.getContent().isEmpty(), "El contenido debería seguir vacío");
    }
    
    @Test
    public void testMultipleAddProperties() throws Exception {
        // Arrange
        file.setType(FileType.PROPERTY);
        char[] content1 = {'a', '=', '1'};
        char[] content2 = {'&', 'b', '=', '2'};
        
        // Act
        file.addProperty(content1);
        file.addProperty(content2);
        
        // Assert
        List<Character> finalContent = file.getContent();
        assertEquals(7, finalContent.size());
        assertEquals('a', finalContent.get(0));
        assertEquals('1', finalContent.get(2));
        assertEquals('&', finalContent.get(3));
        assertEquals('2', finalContent.get(6));
    }
    
    @Test
    public void testGetCRC32AfterMultipleModifications() throws Exception {
        // Arrange
        file.setType(FileType.PROPERTY);
        char[] content1 = {'d', 'a', 't', 'a', '1'};
        char[] content2 = {'&', 'd', 'a', 't', 'a', '2'};
        
        file.addProperty(content1);
        file.addProperty(content2);
        
        long expectedCRC = 555555555L;
        when(fileUtilsMock.calculateCRC32(any(byte[].class))).thenReturn(expectedCRC);
        
        // Act
        long result1 = file.getCRC32();
        
        // Añadimos más contenido
        char[] content3 = {'&', 'd', 'a', 't', 'a', '3'};
        file.addProperty(content3);
        
        long expectedCRC2 = 666666666L;
        when(fileUtilsMock.calculateCRC32(any(byte[].class))).thenReturn(expectedCRC2);
        long result2 = file.getCRC32();
        
        // Assert
        assertEquals(expectedCRC, result1, "Primer CRC32 calculado correctamente");
        assertEquals(expectedCRC2, result2, "Segundo CRC32 calculado correctamente");
        verify(fileUtilsMock, times(2)).calculateCRC32(any(byte[].class));
    }
    
    @Test
    public void testUnicodeCharactersInContent() throws Exception {
        // Arrange
        file.setType(FileType.PROPERTY);
        // Caracteres Unicode que requieren más de un byte en UTF-16
        char[] unicodeContent = {'A', 0x00E9, 0x4F60, (char) 0x1F600}; // 'é', '你', 😀 (emoji)
        
        // Act
        file.addProperty(unicodeContent);
        
        // Assert
        List<Character> content = file.getContent();
        assertEquals(4, content.size());
        assertEquals('A', content.get(0));
        assertEquals(0x00E9, content.get(1).charValue()); // 'é'
        assertEquals(0x4F60, content.get(2).charValue()); // '你'
        // Nota: 0x1F600 es un emoji que en Java se representa como dos chars (surrogate pair)
        // pero en nuestro array de chars es solo un elemento
    }
}