package es.upm.grise.profundizacion.file;

import java.util.zip.CRC32;

public class FileUtils {
	static long calculateCRC32(byte[] bytes) {
		if(bytes.length == 0){
            return 0L;
        }
        CRC32 crc = new CRC32();
        crc.update(bytes,0,bytes.length);
		return crc.getValue();
		
	}

}
