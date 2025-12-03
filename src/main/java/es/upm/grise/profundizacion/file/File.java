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
	        this.content = new ArrayList<Character>();
	    }

	    /*
	     * Method to code / test
	     */
	    public void addProperty(char[] newcontent) throws WrongFileTypeException, InvalidContentException {
	        if (newcontent == null) {
	            throw new InvalidContentException("Content is null");
	        }
	        if (this.type == FileType.IMAGE) {
	            throw new WrongFileTypeException("Cannot add property to IMAGE file");
	        }
	        for (char c : newcontent) {
	            this.content.add(c);
	        }
	    }

	    /*
	     * Method to code / test
	     */
	    public long getCRC32() {
	        if (this.content == null || this.content.isEmpty()) {
	            return 0L;
	        }
	        byte[] bytes = new byte[this.content.size()];
	        for (int i = 0; i < this.content.size(); i++) {
	            char c = this.content.get(i);
	            bytes[i] = (byte) (c & 0x00FF);
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