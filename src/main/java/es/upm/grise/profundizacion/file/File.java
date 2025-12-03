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
        this.content= new ArrayList<Character>() ;   
    
    }

	/*
	 * Method to code / test
	 */
    public void addProperty(char[] content) throws InvalidContentException, WrongFileTypeException {

    	if (content==null) {
    		throw new InvalidContentException("El atributo no puede ser null");
    	}
    	
    	if(this.type==FileType.IMAGE) {
    		throw new WrongFileTypeException("El tipo de archivo es imagen");
    	}
    	
    	for (char c: content) {
    		this.content.add(c);
    	}
    	
    	
    }

	/*
	 * Method to code / test
	 */
    public long getCRC32() {
    	
    	if(content.isEmpty()) {
            return 0L;
        }

        byte[] bytes = new byte[content.size()];
        for(int i = 0; i < content.size(); i++) {
            char c = content.get(i);
            bytes[i] = (byte)(c & 0xFF);   
        }

        return FileUtils.calculateCRC32(bytes);        
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
