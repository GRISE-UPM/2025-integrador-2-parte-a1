
package es.upm.grise.profundizacion.file;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.zip.CRC32;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class FileTest {

  @Test
  @DisplayName("addProperty: lanza InvalidContentException cuando content es null")
  void addProperty_throwsInvalidContent_whenNull() {
    File file = new File();
    file.setType(FileType.PROPERTY);

    InvalidContentException ex = assertThrows(
        InvalidContentException.class,
        () -> file.addProperty(null));

    assertEquals("El atributo no puede ser null", ex.getMessage());
  }

  @Test
  @DisplayName("addProperty: lanza WrongFileTypeException cuando el tipo es IMAGE")
  void addProperty_throwsWrongFileType_whenImage() {
    File file = new File();
    file.setType(FileType.IMAGE);

    WrongFileTypeException ex = assertThrows(
        WrongFileTypeException.class,
        () -> file.addProperty("abc".toCharArray()));

    assertEquals("El tipo de archivo es imagen", ex.getMessage());
  }

  @Test
  @DisplayName("addProperty: añade los caracteres al contenido cuando el tipo no es IMAGE")
  void addProperty_appendsContent_whenPropertyType() throws InvalidContentException, WrongFileTypeException {
    File file = new File();
    file.setType(FileType.PROPERTY);

    char[] toAdd = "abc".toCharArray();
    file.addProperty(toAdd);

    List<Character> content = file.getContent();
    assertEquals(3, content.size());
    assertEquals(Character.valueOf('a'), content.get(0));
    assertEquals(Character.valueOf('b'), content.get(1));
    assertEquals(Character.valueOf('c'), content.get(2));
  }

  @Test
  @DisplayName("getCRC32: devuelve 0L cuando no hay contenido")
  void getCRC32_returnsZero_whenEmpty() {
    File file = new File(); // content vacío
    long crc = file.getCRC32();
    assertEquals(0L, crc);
  }

  @Test
  @DisplayName("getCRC32: delega en FileUtils.calculateCRC32 con bytes enmascarados (0xFF) y retorna su valor")
  void getCRC32_delegatesToFileUtils_withMaskedBytes() throws InvalidContentException, WrongFileTypeException {
    File file = new File();
    file.setType(FileType.PROPERTY);

    // Incluye un char > 0xFF para comprobar el enmascarado de bajo byte
    char[] chars = new char[] { 'A', (char) 0x0101 }; // 0x41 y 0x0101 -> bytes [0x41, 0x01]
    file.addProperty(chars);

    byte[] expectedBytes = new byte[] { (byte) 0x41, (byte) 0x01 };
    long expectedCrc = 123456789L;

    try (MockedStatic<FileUtils> mocked = mockStatic(FileUtils.class)) {
      mocked.when(() -> FileUtils.calculateCRC32(expectedBytes)).thenReturn(expectedCrc);

      long result = file.getCRC32();
      assertEquals(expectedCrc, result);

      // Verifica que se llamó con el array esperado
      mocked.verify(() -> FileUtils.calculateCRC32(expectedBytes), times(1));
    }
  }

  @Test
  @DisplayName("getCRC32: cálculo de CRC real (sin mocks) para contenido simple")
  void getCRC32_realCalculation_integration() throws InvalidContentException, WrongFileTypeException {
    File file = new File();
    file.setType(FileType.PROPERTY);

    char[] chars = "ABC".toCharArray(); // 'A','B','C'
    file.addProperty(chars);

    // Reproduce la lógica de File.getCRC32: bajo byte de cada char
    byte[] bytes = new byte[] {
        (byte) ('A' & 0xFF),
        (byte) ('B' & 0xFF),
        (byte) ('C' & 0xFF)
    };

    CRC32 crc = new CRC32();
    for (byte b : bytes) crc.update(b);
    long expected = crc.getValue();

    assertEquals(expected, file.getCRC32());
  }
}
