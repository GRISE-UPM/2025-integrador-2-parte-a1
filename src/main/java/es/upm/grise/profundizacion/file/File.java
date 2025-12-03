package es.upm.grise.profundizacion.file;

import java.util.ArrayList;
import java.util.List;

public class File {

    private FileType type;
    private List<Character> content;
	private FileUtils FileUtils = new FileUtils();

	/*
	 * Constructor
	 */
    public File() {
		// Inicializamos la lista de contenido
        content = new ArrayList<Character>();
    }

	/*
	 * Method to code / test
	 */
    public void addProperty(char[] newcontent) throws InvalidContentException, WrongFileTypeException {
		// Si el contenido es nulo lanzamos la excepción
		if (newcontent == null){
			throw new InvalidContentException();
		}

		// Si el tipo de archivo es nulo lanzamos la excepción
		if (type == FileType.IMAGE){
			throw new WrongFileTypeException();
		}

		// Añadimos el contenido al contenido existente
		for (char c : newcontent) {
			content.add(c);
		}
    }

	/*
	 * Method to code / test
	 */
    public long getCRC32() {
    	// Convertimos content a byte[]
		byte[] byte_content = new byte[content.size()];
		for (int i = 0; i < content.size(); i++) {
			byte_content[i] = (byte) content.get(i).charValue();
		}

		// Calculamos y devolvemos el CRC32
        return FileUtils.calculateCRC32(byte_content);
    }
    
    
	/*
	 * Setters/getters
	 */
    public void setType(FileType type) {
    	
    	this.type = type;
    	
    }
    
    public List<Character> getContent() {
    	
    	return content;
    	
    }
    
}
