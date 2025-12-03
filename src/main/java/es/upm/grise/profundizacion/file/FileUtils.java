package es.upm.grise.profundizacion.file;
import java.util.zip.CRC32;

public class FileUtils {
	
	public static long calculateCRC32(byte[] bytes) {
        CRC32 crc = new CRC32();

        for(byte b : bytes) {
            crc.update(b);
        }
        
        return crc.getValue();

		
	}

}
