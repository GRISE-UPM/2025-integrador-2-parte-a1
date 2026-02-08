package es.upm.grise.profundizacion.file;

public class InvalidContentException extends Exception {
    
    public InvalidContentException() {
        super("El contenido proporcionado es nulo");
    }
    
    public InvalidContentException(String message) {
        super(message);
    }
    
}