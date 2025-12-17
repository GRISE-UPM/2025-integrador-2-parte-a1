package es.upm.grise.profundizacion.file;

public class WrongFileTypeException extends Exception {
    
    public WrongFileTypeException() {
        super("No se pueden añadir propiedades a un archivo de tipo IMAGE");
    }
    
    public WrongFileTypeException(String message) {
        super(message);
    }
    
}