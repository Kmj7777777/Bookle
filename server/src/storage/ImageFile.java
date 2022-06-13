package storage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageFile {
	private InputStream is = null;
	
	public ImageFile(String id) {
		is = this.getClass().getResourceAsStream("/bookImage/" + id + ".jpg");
		
		if(is == null) {
			is = this.getClass().getResourceAsStream("/bookImage/not_found.jpg");
		}
	}
	
	public long getFileLength() {
		long length = 0;
		
		try {
			length = is.available();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return length;
	}
	
	public byte[] getImageFile() {
		byte[] file = null;
		
		try {
			long fileSize = getFileLength();
			
			DataInputStream dis = new DataInputStream(is);
			file = new byte[(int)fileSize];
			dis.readFully(file);
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
}