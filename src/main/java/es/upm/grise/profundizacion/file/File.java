package es.upm.grise.profundizacion.file;
import es.upm.grise.profundizacion.file.FileUtils;

import Exceptions.InvalidContentException;
import Exceptions.WrongFileTypeException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class File {

    private FileType type;
    private List<Character> content;

	/*
	 * Constructor
	 */
    public File(FileType type) {
        this.type = type;
        this.content = new ArrayList<>();
    }

	/*
	 * Method to code / test
	 */
    public void addProperty(char[] newcontent) throws InvalidContentException, WrongFileTypeException {
        if (newcontent == null) {
            throw new InvalidContentException("El contenido es null");
        }
        if (this.type == FileType.IMAGE) {
            throw new WrongFileTypeException("No se pueden añadir propiedades a un archivo de tipo IMAGE");
        }
        for (char c : newcontent) {
            content.add(c);
        }
    }

	/*
	 * Method to code / test
	 */
    public long getCRC32() {
        //ContentSize()*2
        if (content.isEmpty()){
            return 0L;
        }else{
            byte[]bytes = new byte[content.size()];
            FileUtils file =new FileUtils();
            for(int i=0;i<content.size();i++){
                bytes[i] = (byte) (content.get(i) & 0x00FF);
            }
            return file.calculateCRC32(bytes);
        }
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
