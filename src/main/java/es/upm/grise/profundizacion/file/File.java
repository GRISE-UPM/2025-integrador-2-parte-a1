package es.upm.grise.profundizacion.file;
import java.util.ArrayList;

public class File {

    public enum FileType {
        PROPERTY,
        IMAGE
    }

    private final FileType type;
    private final ArrayList<Character> content;

    public File(FileType type) {
        this.type = type;
        this.content = new ArrayList<>(); // vacío pero no null
    }

    /**
     * Añade contenido tipo propiedad a un File PROPERTY
     */
    public void addProperty(char[] newcontent) throws InvalidContentException, WrongFileTypeException {

        // Regla 2: newcontent == null -> InvalidContentException
        if (newcontent == null) {
            throw new InvalidContentException("Content cannot be null");
        }

        // Regla 3 y 4: Solo PROPERTY admite addProperty()
        if (this.type != FileType.PROPERTY) {
            throw new WrongFileTypeException("addProperty only allowed for PROPERTY files");
        }

        // Regla 1: Añadir newcontent al content existente
        for (char c : newcontent) {
            content.add(c);
        }
    }

    /**
     * Calcula el CRC32 del content
     */
    public long getCRC32() {

		// 1. Si content está vacío → devolver 0
		if (content.isEmpty()) {
			return 0;
		}
	
		// 2. Convertir ArrayList<Character> → byte[] usando el byte menos significativo
		byte[] data = new byte[content.size()];
		for (int i = 0; i < content.size(); i++) {
			data[i] = (byte)(content.get(i) & 0xFF);
		}
	
		// 3. Crear instancia de FileUtils (el método no es estático)
		FileUtils utils = new FileUtils();
	
		// 4. Llamar a calculateCRC32(data)
		return utils.calculateCRC32(data);
	}
}