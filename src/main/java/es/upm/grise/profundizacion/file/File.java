package es.upm.grise.profundizacion.file;

import java.util.ArrayList;
import java.util.List;

public class File {

    private FileType type;
    private List<Character> content;

	/*
	 * Constructor
	 */
    public File() {
    	// content deberá estar vacío, pero no null
        this.content = new ArrayList<Character>();
    }

	/*
	 * Method to code / test
	 */
public void addProperty(char[] newcontent) throws InvalidContentException, WrongFileTypeException {
        
        // Si newcontent es null, se lanzará una InvalidContentException
        if (newcontent == null) {
            throw new InvalidContentException();
        }
        
        // Si el type del file es IMAGE, se lanzará una excepción WrongFileTypeException
        if (this.type == FileType.IMAGE) {
            throw new WrongFileTypeException();
        }
        
        // newcontent se añade al content existente
        for (char c : newcontent) {
            this.content.add(c);
        }
    }

	/*
	 * Method to code / test
	 */
public long getCRC32() {
    
    // Si content está vacío, getCRC32() devolverá el valor 0 (cero)
    if (this.content.isEmpty()) {
        return 0L;
    }
    
    // content debe transformarse en un byte[] antes de usar el método calculateCRC32()
    byte[] byteArray = new byte[this.content.size()];
    
    for (int i = 0; i < this.content.size(); i++) {
        // Obtener el byte menos significativo usando máscara 0xFF
        char c = this.content.get(i);
        byte lsbByte = (byte) (c & 0x00FF);
        byteArray[i] = lsbByte;
    }
    
    // Usar FileUtils para calcular el CRC32
    FileUtils fileUtils = new FileUtils();
    return fileUtils.calculateCRC32(byteArray);
}

    
    
	/*
	 * Setters/getters
	 */
    public void setType(FileType type) {
    	
    	this.type = type;
    	
    }
    
    public FileType getType() {
        return this.type;
    }
    
    public List<Character> getContent() {
    	
    	return content;
    	
    }
    
}
